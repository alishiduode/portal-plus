<template>
  <div class="admin-page">
    <div class="page-inner">
      <!-- 登录 -->
      <div v-if="!isLoggedIn" class="login-wrap">
        <div class="login-card">
          <div class="login-header">
            <div class="login-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <h2>管理员登录</h2>
            <p>请输入管理员账号密码</p>
          </div>
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top" class="login-form">
            <el-form-item prop="username" label="用户名">
              <el-input v-model="loginForm.username" placeholder="admin" size="large" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password" label="密码">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-button type="primary" size="large" :loading="loginLoading" @click="handleLogin" class="login-btn">
              登录
            </el-button>
          </el-form>
        </div>
      </div>

      <!-- 管理后台 -->
      <template v-else>
        <!-- 统计卡片 -->
        <div class="admin-header">
          <h1>管理后台</h1>
          <el-button @click="handleLogout" size="small">退出登录</el-button>
        </div>

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
          <!-- 项目管理 -->
          <el-tab-pane label="项目管理" name="projects">
            <div class="tab-toolbar">
              <el-button type="primary" @click="openProjectDialog()">
                <el-icon><Plus /></el-icon> 新增项目
              </el-button>
              <el-input v-model="projectSearch" placeholder="搜索..." clearable style="width: 220px" />
            </div>
            <el-table :data="filteredProjectList" stripe class="admin-table" v-loading="tableLoading">
              <el-table-column label="项目" min-width="200">
                <template #default="{ row }">
                  <div class="table-project">
                    <div class="tp-icon">{{ row.name.charAt(0) }}</div>
                    <div>
                      <div class="tp-name">{{ row.name }}</div>
                      <div class="tp-cat">{{ row.categoryName }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="techStack" label="技术栈" min-width="150" show-overflow-tooltip />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="推荐" width="80" align="center">
                <template #default="{ row }">
                  <el-icon v-if="row.isFeatured" color="#f59e0b"><Star /></el-icon>
                  <el-icon v-else color="#475569"><StarFilled /></el-icon>
                </template>
              </el-table-column>
              <el-table-column prop="visitCount" label="访问" width="80" align="center" />
              <el-table-column label="操作" width="140" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="openProjectDialog(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteProject(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 分类管理 -->
          <el-tab-pane label="分类管理" name="categories">
            <div class="tab-toolbar">
              <el-button type="primary" @click="openCatDialog()">
                <el-icon><Plus /></el-icon> 新增分类
              </el-button>
            </div>
            <el-table :data="store.categories" stripe class="admin-table">
              <el-table-column prop="name" label="分类名称" />
              <el-table-column prop="description" label="描述" show-overflow-tooltip />
              <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
              <el-table-column label="操作" width="140" fixed="right">
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

    <!-- 项目表单弹窗 -->
    <el-dialog v-model="projectDialogVisible" :title="editProject?.id ? '编辑项目' : '新增项目'" width="640px" destroy-on-close>
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
        <el-form-item label="访问URL" prop="url">
          <el-input v-model="projectForm.url" placeholder="https://example.com" />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="projectForm.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="图标URL">
              <el-input v-model="projectForm.icon" placeholder="图标地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="GitHub">
              <el-input v-model="projectForm.githubUrl" placeholder="GitHub地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技术栈">
          <el-input v-model="projectForm.techStack" placeholder="例：Spring Boot, Vue 3, MySQL（逗号分隔）" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="projectForm.tags" placeholder="例：效率,工具（逗号分隔）" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="projectForm.status" style="width:100%">
                <el-option label="运行中" value="ACTIVE" />
                <el-option label="维护中" value="MAINTENANCE" />
                <el-option label="已下线" value="OFFLINE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序">
              <el-input-number v-model="projectForm.sortOrder" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="精选推荐">
              <el-switch v-model="projectForm.isFeatured" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitProject">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="catDialogVisible" :title="editCat?.id ? '编辑分类' : '新增分类'" width="480px" destroy-on-close>
      <el-form :model="catForm" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="catForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="catForm.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="catForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitCat">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { usePortalStore } from '@/store/portal'
import { createProject, updateProject, deleteProject, createCategory, updateCategory, deleteCategory } from '@/api/portal'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = usePortalStore()

// ===== 登录 =====
const isLoggedIn = ref(!!localStorage.getItem('adminToken'))
const loginLoading = ref(false)
const loginFormRef = ref(null)
const loginForm = ref({ username: 'admin', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  loginLoading.value = true
  try {
    const token = btoa(`${loginForm.value.username}:${loginForm.value.password}`)
    localStorage.setItem('adminToken', token)
    isLoggedIn.value = true
    await store.loadAll()
    ElMessage.success('登录成功')
  } catch {
    localStorage.removeItem('adminToken')
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loginLoading.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('adminToken')
  isLoggedIn.value = false
}

// ===== 项目管理 =====
const activeTab = ref('projects')
const tableLoading = ref(false)
const projectSearch = ref('')
const projectDialogVisible = ref(false)
const editProject = ref(null)
const submitLoading = ref(false)
const projectFormRef = ref(null)

const projectForm = ref({
  name: '', description: '', url: '', icon: '', coverImage: '',
  categoryId: null, techStack: '', status: 'ACTIVE',
  isFeatured: false, sortOrder: 0, githubUrl: '', tags: ''
})

const projectRules = {
  name: [{ required: true, message: '请输入项目名称' }],
  url: [{ required: true, message: '请输入访问URL' }],
  categoryId: [{ required: true, message: '请选择分类' }]
}

const filteredProjectList = computed(() => {
  if (!projectSearch.value) return store.projects
  const kw = projectSearch.value.toLowerCase()
  return store.projects.filter(p =>
    p.name.toLowerCase().includes(kw) || p.techStack?.toLowerCase().includes(kw)
  )
})

const openProjectDialog = (project = null) => {
  editProject.value = project
  if (project) {
    Object.assign(projectForm.value, { ...project })
  } else {
    projectForm.value = {
      name: '', description: '', url: '', icon: '', coverImage: '',
      categoryId: null, techStack: '', status: 'ACTIVE',
      isFeatured: false, sortOrder: 0, githubUrl: '', tags: ''
    }
  }
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
  ElMessageBox.confirm(`确定删除项目「${project.name}」？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteProject(project.id)
      ElMessage.success('删除成功')
      await store.loadProjects()
      await store.loadStats()
    })
    .catch(() => {})
}

// ===== 分类管理 =====
const catDialogVisible = ref(false)
const editCat = ref(null)
const catForm = ref({ name: '', description: '', sortOrder: 0 })

const openCatDialog = (cat = null) => {
  editCat.value = cat
  catForm.value = cat ? { ...cat } : { name: '', description: '', sortOrder: 0 }
  catDialogVisible.value = true
}

const submitCat = async () => {
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
  ElMessageBox.confirm(`确定删除分类「${cat.name}」？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteCategory(cat.id)
      ElMessage.success('删除成功')
      await store.loadCategories()
    })
    .catch(() => {})
}

// 辅助
const getStatusType = (s) => ({ ACTIVE: 'success', MAINTENANCE: 'warning', OFFLINE: 'info' }[s])
const getStatusText = (s) => ({ ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }[s])

onMounted(async () => {
  if (isLoggedIn.value) await store.loadAll()
})
</script>

<style scoped>
.admin-page {
  padding-top: 64px;
  min-height: 100vh;
}

.page-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* 登录 */
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
  width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-icon {
  width: 60px;
  height: 60px;
  background: var(--gradient-primary);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin: 0 auto 16px;
}

.login-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.login-header p {
  color: var(--text-muted);
  font-size: 14px;
}

.login-form :deep(.el-form-item__label) {
  color: var(--text-secondary);
}

.login-btn {
  width: 100%;
  background: var(--gradient-primary) !important;
  border: none !important;
  margin-top: 8px;
}

/* 管理后台 */
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.admin-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

/* 统计 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.sc-icon { font-size: 28px; }
.sc-num { font-size: 24px; font-weight: 700; color: var(--text-primary); }
.sc-label { font-size: 12px; color: var(--text-muted); }

/* 表格 */
.tab-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.admin-table {
  background: transparent !important;
  --el-table-bg-color: transparent;
}

.admin-table :deep(th) {
  background: var(--bg-color) !important;
  color: var(--text-muted);
}

.admin-table :deep(td) {
  border-bottom-color: var(--border-color) !important;
  color: var(--text-primary);
}

.admin-table :deep(tr:hover td) {
  background: rgba(99, 102, 241, 0.05) !important;
}

.table-project {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tp-icon {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}

.tp-name {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.tp-cat {
  font-size: 12px;
  color: var(--text-muted);
}

.admin-tabs :deep(.el-tabs__nav-wrap::after) {
  background: var(--border-color);
}

.admin-tabs :deep(.el-tabs__item) {
  color: var(--text-secondary);
}

.admin-tabs :deep(.el-tabs__item.is-active) {
  color: var(--primary-light);
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
