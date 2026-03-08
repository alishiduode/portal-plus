<template>
  <div class="project-card" @click="handleClick">
    <!-- 状态标记 -->
    <div class="card-status" :class="statusClass">
      <span class="status-dot"></span>
      {{ statusText }}
    </div>

    <!-- 图标区域 -->
    <div class="card-icon-wrap">
      <img
        v-if="project.icon"
        :src="project.icon"
        :alt="project.name"
        class="card-icon"
        @error="onIconError"
      />
      <div v-else class="card-icon-fallback">
        {{ project.name.charAt(0) }}
      </div>
      <!-- 推荐标签 -->
      <div v-if="project.isFeatured" class="featured-badge">
        <el-icon><Star /></el-icon>
      </div>
    </div>

    <!-- 内容 -->
    <div class="card-body">
      <h3 class="card-title">{{ project.name }}</h3>
      <p class="card-desc">{{ project.description || '暂无描述' }}</p>

      <!-- 技术栈 -->
      <div v-if="project.techStack" class="card-tech">
        <el-tag
          v-for="tech in techList"
          :key="tech"
          size="small"
          class="tech-tag"
          effect="plain"
        >{{ tech }}</el-tag>
      </div>
    </div>

    <!-- 底部 -->
    <div class="card-footer">
      <span class="card-category">
        <el-icon><FolderOpened /></el-icon>
        {{ project.categoryName }}
      </span>
      <span class="card-visits">
        <el-icon><View /></el-icon>
        {{ formatVisits(project.visitCount) }}
      </span>
    </div>

    <!-- Hover遮罩 -->
    <div class="card-overlay">
      <el-button type="primary" size="large" round class="visit-btn">
        <el-icon><Position /></el-icon>
        立即访问
      </el-button>
      <el-button size="large" round class="detail-btn" @click.stop="goDetail">
        <el-icon><InfoFilled /></el-icon>
        查看详情
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { visitProject } from '@/api/portal'
import { ElMessage } from 'element-plus'

const props = defineProps({
  project: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const techList = computed(() => {
  if (!props.project.techStack) return []
  return props.project.techStack.split(',').map(t => t.trim()).slice(0, 3)
})

const statusClass = computed(() => ({
  'status-active': props.project.status === 'ACTIVE',
  'status-maintenance': props.project.status === 'MAINTENANCE',
  'status-offline': props.project.status === 'OFFLINE'
}))

const statusText = computed(() => {
  const map = { ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }
  return map[props.project.status] || '运行中'
})

const formatVisits = (count) => {
  if (!count) return '0'
  if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
  return count.toString()
}

const handleClick = async () => {
  if (props.project.status === 'OFFLINE') {
    ElMessage.warning('该项目已下线')
    return
  }
  try {
    const res = await visitProject(props.project.id)
    window.open(res.data, '_blank')
  } catch {
    window.open(props.project.url, '_blank')
  }
}

const goDetail = () => {
  router.push(`/project/${props.project.id}`)
}

const onIconError = (e) => {
  e.target.style.display = 'none'
}
</script>

<style scoped>
.project-card {
  position: relative;
  background: var(--gradient-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-card);
  padding: 24px;
  cursor: pointer;
  transition: var(--transition);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.project-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: var(--transition);
}

.project-card:hover {
  border-color: rgba(99, 102, 241, 0.5);
  box-shadow: var(--shadow-hover);
  transform: translateY(-4px);
}

.project-card:hover::before {
  opacity: 1;
}

/* 状态标记 */
.card-status {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-active { 
  background: rgba(16, 185, 129, 0.15); 
  color: var(--success-color);
}
.status-active .status-dot { 
  background: var(--success-color);
  animation: pulse-glow 2s infinite;
}
.status-maintenance { 
  background: rgba(245, 158, 11, 0.15); 
  color: var(--warning-color);
}
.status-maintenance .status-dot { background: var(--warning-color); }
.status-offline { 
  background: rgba(100, 116, 139, 0.15); 
  color: var(--text-muted);
}
.status-offline .status-dot { background: var(--text-muted); }

/* 图标 */
.card-icon-wrap {
  position: relative;
  width: 56px;
  height: 56px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  object-fit: cover;
}

.card-icon-fallback {
  width: 56px;
  height: 56px;
  background: var(--gradient-primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.featured-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  background: var(--warning-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
}

/* 内容 */
.card-body {
  flex: 1;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.3;
}

.card-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tech-tag {
  font-size: 11px !important;
  background: rgba(99, 102, 241, 0.1) !important;
  border-color: rgba(99, 102, 241, 0.3) !important;
  color: var(--primary-light) !important;
}

/* 底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
  font-size: 12px;
  color: var(--text-muted);
}

.card-category,
.card-visits {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Hover遮罩 */
.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: var(--transition);
  border-radius: var(--radius-card);
}

.project-card:hover .card-overlay {
  opacity: 1;
}

.visit-btn {
  background: var(--gradient-primary) !important;
  border: none !important;
  width: 160px;
}

.detail-btn {
  background: rgba(30, 41, 59, 0.8) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
  width: 160px;
}

.detail-btn:hover {
  color: var(--text-primary) !important;
  border-color: var(--primary-color) !important;
}
</style>
