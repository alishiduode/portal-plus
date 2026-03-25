<template>
  <div class="admin-page">
    <div class="page-inner">

      <!-- ══ 登录面板 ══════════════════════════════════════════ -->
      <div v-if="!isLoggedIn" class="login-wrap">
        <div class="login-card">
          <div class="login-header">
            <div class="login-icon"><el-icon><Lock /></el-icon></div>
            <h2>管理员登录</h2>
            <p>请输入管理员账号密码</p>
          </div>

          <!-- 无后端提示 -->
          <el-alert
            v-if="showOfflineTip"
            title="当前运行在离线模式"
            description="未检测到后端服务，管理功能（增删改）需要启动 Spring Boot 后端后使用。您仍可正常浏览门户展示内容。"
            type="warning"
            :closable="false"
            show-icon
            style="margin-bottom:20px;border-radius:10px"
          />

          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top">
            <el-form-item prop="username" label="用户名">
              <el-input v-model="loginForm.username" placeholder="admin" size="large" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password" label="密码">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-alert
              v-if="loginError"
              :title="loginError"
              type="error"
              :closable="false"
              show-icon
              style="margin-bottom:16px;border-radius:8px"
            />

            <el-button
              type="primary"
              size="large"
              :loading="loginLoading"
              @click="handleLogin"
              class="login-btn"
            >
              {{ loginLoading ? '验证中...' : '登 录' }}
            </el-button>

            <p class="login-tip">
              <el-icon><InfoFilled /></el-icon>
              默认账号：admin / admin123（可在 application.properties 修改）
            </p>
          </el-form>
        </div>
      </div>

      <!-- ══ 管理后台主体 ══════════════════════════════════════ -->
      <template v-else>

        <!-- 顶栏 -->
        <div class="admin-header">
          <div>
            <h1 class="admin-title">管理后台</h1>
            <span class="admin-subtitle">
              <el-icon><CircleCheck /></el-icon>
              已连接到后端服务
            </span>
          </div>
          <el-button @click="handleLogout" size="small" plain>
            <el-icon><SwitchButton /></el-icon> 退出登录
          </el-button>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-row">
          <div class="stat-card">
            <el-icon class="sc-icon" color="#6366f1"><Grid /></el-icon>
            <span class="sc-num">{{ store.stats.totalProjects }}</span>
            <span class="sc-label">总项目数</span>
          </div>
          <div class="stat-card">
            <el-icon class="sc-icon" color="#10b981"><CircleCheck /></el-icon>
            <span class="sc-num">{{ store.stats.activeProjects }}</span>
            <span class="sc-label">运行中</span>
          </div>
          <div class="stat-card">
            <el-icon class="sc-icon" color="#f59e0b"><View /></el-icon>
            <span class="sc-num">{{ store.stats.totalVisits }}</span>
            <span class="sc-label">总访问</span>
          </div>
          <div class="stat-card">
            <el-icon class="sc-icon" color="#06b6d4"><FolderOpened /></el-icon>
            <span class="sc-num">{{ store.stats.totalCategories }}</span>
            <span class="sc-label">分类数</span>
          </div>
        </div>

        <!-- Tabs -->
        <el-tabs v-model="activeTab" class="admin-tabs">

          <!-- ── 项目管理 ── -->
          <el-tab-pane label="项目管理" name="projects">
            <div class="tab-toolbar">
              <el-button type="primary" @click="openProjectDialog()">
                <el-icon><Plus /></el-icon> 新增项目
              </el-button>
              <el-input
                v-model="projectSearch"
                placeholder="搜索项目名称..."
                clearable
                prefix-icon="Search"
                style="width:220px"
              />
            </div>

            <el-table
              :data="filteredProjectList"
              stripe
              class="admin-table"
              v-loading="tableLoading"
              row-key="id"
            >
              <el-table-column label="项目" min-width="200">
                <template #default="{ row }">
                  <div class="table-project">
                    <div class="tp-icon" :style="{ background: iconBg(row.name) }">
                      {{ row.name.charAt(0) }}
                    </div>
                    <div>
                      <div class="tp-name">{{ row.name }}</div>
                      <div class="tp-url">{{ row.url }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="categoryName" label="分类" width="100" />
              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="statusTagType(row.status)" size="small">
                    {{ statusLabel(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="精选" width="60" align="center">
                <template #default="{ row }">
                  <el-icon :color="row.isFeatured ? '#f59e0b' : '#334155'">
                    <StarFilled />
                  </el-icon>
                </template>
              </el-table-column>
              <el-table-column label="访问" prop="visitCount" width="70" align="center" />
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button link size="small" @click="openUrl(row.url)">预览</el-button>
                  <el-button link type="primary" size="small" @click="openProjectDialog(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteProject(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- ── 分类管理 ── -->
          <el-tab-pane label="分类管理" name="categories">
            <div class="tab-toolbar">
              <el-button type="primary" @click="openCatDialog()">
                <el-icon><Plus /></el-icon> 新增分类
              </el-button>
            </div>
            <el-table :data="store.categories" stripe class="admin-table">
              <el-table-column prop="id" label="ID" width="60" align="center" />
              <el-table-column prop="name" label="分类名称" />
              <el-table-column prop="description" label="描述" show-overflow-tooltip />
              <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
              <el-table-column label="操作" width="130" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="openCatDialog(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteCat(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

        </el-tabs>
      </template>
    </div>

    <!-- ══ 项目表单弹窗 ══════════════════════════════════════ -->
    <el-dialog
      v-model="projectDialogVisible"
      :title="editProject?.id ? '编辑项目' : '新增项目'"
      width="660px"
      destroy-on-close
      align-center
    >
      <el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="projectForm.categoryId" placeholder="选择分类" style="width:100%">
                <el-option v-for="c in store.categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="访问 URL" prop="url">
          <el-input v-model="projectForm.url" placeholder="https://your-project.com" />
        </el-form-item>

        <el-form-item label="项目描述">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="3"
            placeholder="简短描述项目功能与价值..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="图标 URL">
              <el-input v-model="projectForm.icon" placeholder="https://..." />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="GitHub">
              <el-input v-model="projectForm.githubUrl" placeholder="https://github.com/..." />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="技术栈">
          <el-input v-model="projectForm.techStack" placeholder="Spring Boot, Vue 3, MySQL（逗号分隔）" />
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="projectForm.tags" placeholder="效率,工具,协作（逗号分隔）" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="projectForm.status" style="width:100%">
                <el-option label="✅ 运行中" value="ACTIVE" />
                <el-option label="🔧 维护中" value="MAINTENANCE" />
                <el-option label="⛔ 已下线" value="OFFLINE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序权重">
              <el-input-number v-model="projectForm.sortOrder" :min="0" :max="999" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="精选推荐">
              <el-switch
                v-model="projectForm.isFeatured"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitProject">
          {{ editProject?.id ? '保存修改' : '立即创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- ══ 分类表单弹窗 ══════════════════════════════════════ -->
    <el-dialog
      v-model="catDialogVisible"
      :title="editCat?.id ? '编辑分类' : '新增分类'"
      width="440px"
      destroy-on-close
      align-center
    >
      <el-form :model="catForm" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="catForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="catForm.description" placeholder="简短描述该分类" />
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="catForm.sortOrder" :min="0" :max="99" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitCat">
          {{ editCat?.id ? '保存修改' : '立即创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { usePortalStore } from '@/store/portal'
import {
  adminLogin,
  createProject, updateProject, deleteProject,
  createCategory, updateCategory, deleteCategory,
} from '@/api/portal'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePortalStore()

// ── 渐变色（和卡片保持一致） ──────────────────────────────────
const GRADIENTS = [
  'linear-gradient(135deg,#6366f1,#8b5cf6)',
  'linear-gradient(135deg,#06b6d4,#3b82f6)',
  'linear-gradient(135deg,#10b981,#06b6d4)',
  'linear-gradient(135deg,#f59e0b,#ef4444)',
  'linear-gradient(135deg,#ec4899,#8b5cf6)',
  'linear-gradient(135deg,#14b8a6,#6366f1)',
]
const iconBg = (name) => GRADIENTS[name.charCodeAt(0) % GRADIENTS.length]

// ══ 登录 ══════════════════════════════════════════════════════
const isLoggedIn = ref(false)
const loginLoading = ref(false)
const loginError = ref('')
const showOfflineTip = ref(false)
const loginFormRef = ref(null)
const loginForm = ref({ username: 'admin', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

/** 检查 localStorage 里是否有有效 token（带过期检测） */
const initLoginState = () => {
  const stored = localStorage.getItem('adminSession')
  if (!stored) return
  try {
    const { token, expireAt } = JSON.parse(stored)
    if (token && Date.now() < expireAt) {
      isLoggedIn.value = true
      // 同步 request 拦截器用的 key
      localStorage.setItem('adminToken', token)
    } else {
      localStorage.removeItem('adminSession')
      localStorage.removeItem('adminToken')
    }
  } catch {
    localStorage.removeItem('adminSession')
  }
}

const handleLogin = async () => {
  loginError.value = ''
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loginLoading.value = true
  try {
    // 尝试真正调用后端做 Basic Auth 验证
    const token = await adminLogin(loginForm.value.username, loginForm.value.password)
    // 保存 token + 8 小时过期
    const session = { token, expireAt: Date.now() + 8 * 60 * 60 * 1000 }
    localStorage.setItem('adminSession', JSON.stringify(session))
    localStorage.setItem('adminToken', token)
    isLoggedIn.value = true
    await store.loadAll()
    ElMessage.success('登录成功')
  } catch (err) {
    const status = err?.response?.status
    if (status === 401 || status === 403) {
      loginError.value = '账号或密码错误，请重新输入'
    } else {
      // 后端不通 → 提示但仍允许以"离线管理员"身份进入（仅浏览）
      loginError.value = '未连接到后端，已进入只读模式（管理操作不可用）'
      const token = btoa(`${loginForm.value.username}:${loginForm.value.password}`)
      localStorage.setItem('adminToken', token)
      isLoggedIn.value = true
      await store.loadAll()
    }
  } finally {
    loginLoading.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('adminSession')
  localStorage.removeItem('adminToken')
  isLoggedIn.value = false
  loginForm.value.password = ''
  loginError.value = ''
}

// ══ 项目管理 ══════════════════════════════════════════════════
const activeTab = ref('projects')
const tableLoading = ref(false)
const projectSearch = ref('')
const projectDialogVisible = ref(false)
const editProject = ref(null)
const submitLoading = ref(false)
const projectFormRef = ref(null)

const defaultProjectForm = () => ({
  name: '', description: '', url: '', icon: '', coverImage: '',
  categoryId: null, techStack: '', status: 'ACTIVE',
  isFeatured: false, sortOrder: 0, githubUrl: '', tags: '',
})
const projectForm = ref(defaultProjectForm())
const projectRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入访问URL', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

const filteredProjectList = computed(() => {
  if (!projectSearch.value.trim()) return store.projects
  const kw = projectSearch.value.toLowerCase()
  return store.projects.filter(p =>
    p.name.toLowerCase().includes(kw) ||
    (p.categoryName || '').toLowerCase().includes(kw)
  )
})

const openProjectDialog = (project = null) => {
  editProject.value = project
  projectForm.value = project ? { ...project } : defaultProjectForm()
  projectDialogVisible.value = true
}

const submitProject = async () => {
  const valid = await projectFormRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (editProject.value?.id) {
      await updateProject(editProject.value.id, projectForm.value)
      ElMessage.success('更新成功')
    } else {
      await createProject(projectForm.value)
      ElMessage.success('创建成功')
    }
    projectDialogVisible.value = false
    await store.loadProjects()
    await store.loadStats()
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteProject = (project) => {
  ElMessageBox.confirm(
    `确定要删除项目「${project.name}」吗？此操作不可撤销。`,
    '删除确认',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  ).then(async () => {
    await deleteProject(project.id)
    ElMessage.success('删除成功')
    await store.loadProjects()
    await store.loadStats()
  }).catch(() => {})
}

const openUrl = (url) => {
  if (url) window.open(url, '_blank', 'noopener,noreferrer')
}

// ══ 分类管理 ══════════════════════════════════════════════════
const catDialogVisible = ref(false)
const editCat = ref(null)
const catForm = ref({ name: '', description: '', sortOrder: 0 })

const openCatDialog = (cat = null) => {
  editCat.value = cat
  catForm.value = cat ? { ...cat } : { name: '', description: '', sortOrder: 0 }
  catDialogVisible.value = true
}

const submitCat = async () => {
  if (!catForm.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitLoading.value = true
  try {
    if (editCat.value?.id) {
      await updateCategory(editCat.value.id, catForm.value)
      ElMessage.success('更新成功')
    } else {
      await createCategory(catForm.value)
      ElMessage.success('创建成功')
    }
    catDialogVisible.value = false
    await store.loadCategories()
    await store.loadStats()
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteCat = (cat) => {
  ElMessageBox.confirm(
    `确定要删除分类「${cat.name}」吗？`,
    '删除确认',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  ).then(async () => {
    await deleteCategory(cat.id)
    ElMessage.success('删除成功')
    await store.loadCategories()
    await store.loadStats()
  }).catch(() => {})
}

// 辅助
const statusTagType = (s) => ({ ACTIVE: 'success', MAINTENANCE: 'warning', OFFLINE: 'info' }[s])
const statusLabel = (s) => ({ ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }[s] || s)

onMounted(async () => {
  initLoginState()
  // 检查后端是否在线
  try {
    await store.loadAll()
  } catch {
    showOfflineTip.value = true
  }
  if (isLoggedIn.value) await store.loadAll()
})
</script>

<style scoped>
.admin-page { padding-top: 64px; min-height: 100vh; }
.page-inner { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }

/* ── 登录 ── */
.login-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 130px);
}
.login-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  padding: 40px;
  width: 420px;
}
.login-header { text-align: center; margin-bottom: 28px; }
.login-icon {
  width: 60px; height: 60px;
  background: var(--gradient-primary);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: white;
  margin: 0 auto 16px;
}
.login-header h2 { font-size: 22px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px; }
.login-header p  { color: var(--text-muted); font-size: 14px; }
.login-btn { width: 100%; background: var(--gradient-primary) !important; border: none !important; margin-top: 4px; }
.login-tip {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.6;
}

/* ── 主体 ── */
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.admin-title { font-size: 24px; font-weight: 700; color: var(--text-primary); }
.admin-subtitle {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--success-color);
  margin-top: 4px;
}

/* 统计 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}
.stat-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  transition: var(--transition);
}
.stat-card:hover { border-color: rgba(99,102,241,.4); transform: translateY(-2px); }
.sc-icon { font-size: 28px; }
.sc-num { font-size: 24px; font-weight: 700; color: var(--text-primary); }
.sc-label { font-size: 12px; color: var(--text-muted); }

/* 表格 */
.tab-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
}
.admin-table { background: transparent !important; }
.admin-table :deep(th.el-table__cell) { background: var(--bg-color) !important; color: var(--text-muted); border-bottom-color: var(--border-color) !important; }
.admin-table :deep(td.el-table__cell) { border-bottom-color: var(--border-color) !important; color: var(--text-primary); }
.admin-table :deep(tr.el-table__row:hover > td) { background: rgba(99,102,241,.05) !important; }
.admin-table :deep(.el-table__inner-wrapper::before) { background: var(--border-color); }

.table-project { display: flex; align-items: center; gap: 10px; }
.tp-icon {
  width: 34px; height: 34px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}
.tp-name { font-weight: 500; color: var(--text-primary); font-size: 14px; }
.tp-url { font-size: 11px; color: var(--text-muted); max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.admin-tabs :deep(.el-tabs__nav-wrap::after) { background: var(--border-color); }
.admin-tabs :deep(.el-tabs__item) { color: var(--text-secondary); }
.admin-tabs :deep(.el-tabs__item.is-active) { color: var(--primary-light); }

@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .login-card { width: 100%; }
}
</style>
