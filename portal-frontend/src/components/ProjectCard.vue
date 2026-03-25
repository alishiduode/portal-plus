<template>
  <div class="project-card" :class="{ offline: isOffline }">

    <!-- 顶部：状态 + 精选 -->
    <div class="card-top">
      <div class="card-status" :class="statusClass">
        <span class="status-dot"></span>
        {{ statusText }}
      </div>
      <el-icon v-if="project.isFeatured" class="featured-star" title="精选推荐">
        <StarFilled />
      </el-icon>
    </div>

    <!-- 图标 + 标题区 -->
    <div class="card-head">
      <div class="card-icon-wrap">
        <img
          v-if="project.icon && !iconError"
          :src="project.icon"
          :alt="project.name"
          class="card-icon"
          @error="iconError = true"
        />
        <div v-else class="card-icon-fallback" :style="{ background: iconBg }">
          {{ project.name.charAt(0).toUpperCase() }}
        </div>
      </div>
      <div class="card-title-wrap">
        <h3 class="card-title">{{ project.name }}</h3>
        <span class="card-category">
          <el-icon><FolderOpened /></el-icon>
          {{ project.categoryName }}
        </span>
      </div>
    </div>

    <!-- 描述 -->
    <p class="card-desc">{{ project.description || '暂无描述' }}</p>

    <!-- 技术栈标签 -->
    <div v-if="techList.length" class="card-tech">
      <span v-for="tech in techList" :key="tech" class="tech-chip">{{ tech }}</span>
      <span v-if="extraCount > 0" class="tech-chip more">+{{ extraCount }}</span>
    </div>

    <!-- 底部操作栏（常驻，不依赖 hover） -->
    <div class="card-footer">
      <span class="card-visits">
        <el-icon><View /></el-icon>
        {{ formatVisits(project.visitCount) }}
      </span>
      <div class="card-actions">
        <!-- 复制链接 -->
        <el-tooltip content="复制链接" placement="top" :show-after="400">
          <button class="action-btn" @click.stop="copyLink">
            <el-icon><CopyDocument /></el-icon>
          </button>
        </el-tooltip>
        <!-- GitHub -->
        <el-tooltip v-if="project.githubUrl" content="查看源码" placement="top" :show-after="400">
          <button class="action-btn" @click.stop="openGithub">
            <el-icon><Link /></el-icon>
          </button>
        </el-tooltip>
        <!-- 详情 -->
        <el-tooltip content="查看详情" placement="top" :show-after="400">
          <button class="action-btn" @click.stop="goDetail">
            <el-icon><InfoFilled /></el-icon>
          </button>
        </el-tooltip>
        <!-- 访问（主按钮） -->
        <button
          class="visit-btn"
          :class="{ disabled: isOffline }"
          :disabled="isOffline"
          @click.stop="handleVisit"
        >
          <el-icon v-if="visiting"><Loading /></el-icon>
          <el-icon v-else><Position /></el-icon>
          {{ isOffline ? '已下线' : '访问' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { visitProject } from '@/api/portal'
import { ElMessage } from 'element-plus'

const props = defineProps({
  project: { type: Object, required: true },
})

const router = useRouter()
const iconError = ref(false)
const visiting = ref(false)

// 根据项目名生成固定渐变色
const GRADIENTS = [
  'linear-gradient(135deg,#6366f1,#8b5cf6)',
  'linear-gradient(135deg,#06b6d4,#3b82f6)',
  'linear-gradient(135deg,#10b981,#06b6d4)',
  'linear-gradient(135deg,#f59e0b,#ef4444)',
  'linear-gradient(135deg,#ec4899,#8b5cf6)',
  'linear-gradient(135deg,#14b8a6,#6366f1)',
]
const iconBg = computed(() => {
  const idx = props.project.name.charCodeAt(0) % GRADIENTS.length
  return GRADIENTS[idx]
})

const isOffline = computed(() => props.project.status === 'OFFLINE')

const statusClass = computed(() => ({
  'status-active': props.project.status === 'ACTIVE',
  'status-maintenance': props.project.status === 'MAINTENANCE',
  'status-offline': props.project.status === 'OFFLINE',
}))
const statusText = computed(() => {
  return { ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }[props.project.status] || '运行中'
})

// 技术栈最多显示 3 个，其余折叠
const ALL_TECHS = computed(() =>
  props.project.techStack ? props.project.techStack.split(',').map(t => t.trim()).filter(Boolean) : []
)
const techList = computed(() => ALL_TECHS.value.slice(0, 3))
const extraCount = computed(() => Math.max(0, ALL_TECHS.value.length - 3))

const formatVisits = (n) => {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

// ── 操作 ──────────────────────────────────────────────────────

/** 访问项目（记录访问量 + 打开新标签） */
const handleVisit = async () => {
  if (isOffline.value || visiting.value) return
  visiting.value = true
  try {
    const res = await visitProject(props.project.id)
    const url = res.data || props.project.url
    if (url && url !== '#') {
      window.open(url, '_blank', 'noopener,noreferrer')
    } else {
      ElMessage.warning('该项目暂无有效访问地址')
    }
  } catch {
    // visitProject 已内置降级，理论上不会走到这里
    window.open(props.project.url, '_blank', 'noopener,noreferrer')
  } finally {
    visiting.value = false
  }
}

/** 复制项目链接到剪贴板 */
const copyLink = async () => {
  const url = props.project.url
  if (!url || url === '#') {
    ElMessage.warning('该项目暂无链接')
    return
  }
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('链接已复制到剪贴板')
  } catch {
    // 兜底：创建临时 input 复制
    const el = document.createElement('input')
    el.value = url
    document.body.appendChild(el)
    el.select()
    document.execCommand('copy')
    document.body.removeChild(el)
    ElMessage.success('链接已复制')
  }
}

/** 打开 GitHub */
const openGithub = () => {
  if (props.project.githubUrl) {
    window.open(props.project.githubUrl, '_blank', 'noopener,noreferrer')
  }
}

/** 跳转详情页 */
const goDetail = () => {
  router.push(`/project/${props.project.id}`)
}
</script>

<style scoped>
.project-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-card);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: var(--transition);
  position: relative;
  overflow: hidden;
}

.project-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: var(--gradient-primary);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.project-card:hover {
  border-color: rgba(99, 102, 241, 0.45);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transform: translateY(-3px);
}
.project-card:hover::before {
  transform: scaleX(1);
}

.project-card.offline {
  opacity: 0.6;
}

/* ── 顶部状态行 ── */
.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
}
.status-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}
.status-active  { background: rgba(16,185,129,.12); color: var(--success-color); }
.status-active  .status-dot { background: var(--success-color); animation: pulse-glow 2s infinite; }
.status-maintenance { background: rgba(245,158,11,.12); color: var(--warning-color); }
.status-maintenance .status-dot { background: var(--warning-color); }
.status-offline { background: rgba(100,116,139,.12); color: var(--text-muted); }
.status-offline .status-dot { background: var(--text-muted); }

.featured-star {
  color: #f59e0b;
  font-size: 16px;
  filter: drop-shadow(0 0 4px rgba(245,158,11,.6));
}

/* ── 图标 + 标题 ── */
.card-head {
  display: flex;
  align-items: center;
  gap: 14px;
}
.card-icon-wrap { flex-shrink: 0; }
.card-icon,
.card-icon-fallback {
  width: 52px; height: 52px;
  border-radius: 14px;
}
.card-icon { object-fit: cover; }
.card-icon-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 800;
  color: white;
}
.card-title-wrap { flex: 1; min-width: 0; }
.card-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.card-category {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

/* ── 描述 ── */
.card-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

/* ── 技术栈 ── */
.card-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tech-chip {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  background: rgba(99,102,241,.1);
  border: 1px solid rgba(99,102,241,.25);
  color: var(--primary-light);
}
.tech-chip.more {
  background: rgba(100,116,139,.1);
  border-color: rgba(100,116,139,.25);
  color: var(--text-muted);
}

/* ── 底部操作栏（常驻） ── */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
  gap: 8px;
}
.card-visits {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
  flex-shrink: 0;
}
.card-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 小图标按钮 */
.action-btn {
  width: 30px; height: 30px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 13px;
  transition: var(--transition);
}
.action-btn:hover {
  background: rgba(99,102,241,.1);
  border-color: rgba(99,102,241,.4);
  color: var(--primary-light);
}

/* 访问主按钮 */
.visit-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  border: none;
  background: var(--gradient-primary);
  color: white;
  cursor: pointer;
  transition: var(--transition);
  white-space: nowrap;
}
.visit-btn:hover {
  opacity: 0.88;
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(99,102,241,.4);
}
.visit-btn.disabled {
  background: var(--border-color) !important;
  color: var(--text-muted) !important;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}
</style>
