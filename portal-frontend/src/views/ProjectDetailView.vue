<template>
  <div class="detail-page">
    <div class="page-inner" v-loading="loading">
      <!-- 返回按钮 -->
      <el-button text class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>

      <template v-if="project">
        <div class="detail-layout">
          <!-- 左：主信息 -->
          <div class="detail-main">
            <!-- 项目头部 -->
            <div class="project-header">
              <div class="project-icon-wrap">
                <img v-if="project.icon" :src="project.icon" :alt="project.name" class="project-icon" @error="e => e.target.style.display='none'" />
                <div v-else class="project-icon-fallback">{{ project.name.charAt(0) }}</div>
              </div>
              <div class="project-header-info">
                <div class="header-tags">
                  <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
                  <el-tag v-if="project.isFeatured" type="warning" size="small" effect="plain">
                    <el-icon><Star /></el-icon> 精选
                  </el-tag>
                  <el-tag size="small" effect="plain">{{ project.categoryName }}</el-tag>
                </div>
                <h1 class="project-title">{{ project.name }}</h1>
                <p class="project-desc">{{ project.description }}</p>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="project-actions">
              <el-button type="primary" size="large" @click="visitProject" :disabled="project.status === 'OFFLINE'">
                <el-icon><Position /></el-icon>
                {{ project.status === 'OFFLINE' ? '已下线' : '立即访问' }}
              </el-button>
              <el-button v-if="project.githubUrl" size="large" @click="openGithub">
                <el-icon><Link /></el-icon>
                GitHub
              </el-button>
            </div>

            <!-- 技术栈 -->
            <div class="detail-section" v-if="project.techStack">
              <h3 class="section-label">技术栈</h3>
              <div class="tech-list">
                <el-tag
                  v-for="tech in techList"
                  :key="tech"
                  effect="plain"
                  class="tech-tag"
                >{{ tech }}</el-tag>
              </div>
            </div>

            <!-- 标签 -->
            <div class="detail-section" v-if="project.tags">
              <h3 class="section-label">标签</h3>
              <div class="tech-list">
                <el-tag v-for="tag in tagList" :key="tag" type="info" effect="plain" size="small">
                  # {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 右：侧边信息 -->
          <div class="detail-sidebar">
            <div class="info-card">
              <h3 class="info-card-title">项目信息</h3>
              <div class="info-list">
                <div class="info-item">
                  <span class="info-label"><el-icon><View /></el-icon> 访问次数</span>
                  <span class="info-value">{{ formatVisits(project.visitCount) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label"><el-icon><FolderOpened /></el-icon> 分类</span>
                  <span class="info-value">{{ project.categoryName }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label"><el-icon><Clock /></el-icon> 创建时间</span>
                  <span class="info-value">{{ formatDate(project.createTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label"><el-icon><Refresh /></el-icon> 更新时间</span>
                  <span class="info-value">{{ formatDate(project.updateTime) }}</span>
                </div>
              </div>
            </div>

            <!-- 快速访问 -->
            <div class="qr-card">
              <h3 class="info-card-title">访问地址</h3>
              <p class="project-url">{{ project.url }}</p>
              <el-button type="primary" size="large" class="visit-full-btn" @click="visitProject">
                <el-icon><Position /></el-icon> 立即前往
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 加载失败 -->
      <div v-else-if="!loading" class="error-state">
        <el-result icon="error" title="项目不存在" sub-title="该项目可能已删除或ID错误">
          <template #extra>
            <el-button @click="$router.push('/projects')">返回项目列表</el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProjectById, visitProject as visitApi } from '@/api/portal'
import { ElMessage } from 'element-plus'

const route = useRoute()
const project = ref(null)
const loading = ref(true)

const techList = computed(() =>
  project.value?.techStack?.split(',').map(t => t.trim()) || []
)

const tagList = computed(() =>
  project.value?.tags?.split(',').map(t => t.trim()) || []
)

const statusType = computed(() => {
  const map = { ACTIVE: 'success', MAINTENANCE: 'warning', OFFLINE: 'info' }
  return map[project.value?.status] || 'success'
})

const statusText = computed(() => {
  const map = { ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }
  return map[project.value?.status] || '运行中'
})

const formatVisits = (n) => {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return n.toString()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const visitProject = async () => {
  try {
    const res = await visitApi(project.value.id)
    window.open(res.data, '_blank')
  } catch {
    window.open(project.value.url, '_blank')
  }
}

const openGithub = () => {
  window.open(project.value.githubUrl, '_blank')
}

onMounted(async () => {
  try {
    const res = await getProjectById(route.params.id)
    project.value = res.data
  } catch {
    ElMessage.error('项目加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  padding-top: 64px;
  min-height: 100vh;
}

.page-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 32px 24px;
}

.back-btn {
  color: var(--text-secondary) !important;
  margin-bottom: 24px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 32px;
}

/* 项目头部 */
.project-header {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.project-icon-wrap {
  flex-shrink: 0;
}

.project-icon,
.project-icon-fallback {
  width: 80px;
  height: 80px;
  border-radius: 20px;
}

.project-icon-fallback {
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  color: white;
}

.header-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.project-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.project-desc {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 15px;
}

/* 操作 */
.project-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

/* 详情章节 */
.detail-section {
  margin-bottom: 24px;
  padding: 20px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-muted);
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.tech-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  background: rgba(99, 102, 241, 0.1) !important;
  border-color: rgba(99, 102, 241, 0.3) !important;
  color: var(--primary-light) !important;
}

/* 侧边 */
.info-card,
.qr-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.info-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-muted);
}

.info-value {
  color: var(--text-primary);
  font-weight: 500;
}

.project-url {
  font-size: 12px;
  color: var(--text-muted);
  word-break: break-all;
  margin-bottom: 16px;
  padding: 10px;
  background: var(--bg-color);
  border-radius: 8px;
}

.visit-full-btn {
  width: 100%;
  background: var(--gradient-primary) !important;
  border: none !important;
}

.error-state {
  padding: 60px 0;
}

@media (max-width: 768px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .project-header {
    flex-direction: column;
  }
}
</style>
