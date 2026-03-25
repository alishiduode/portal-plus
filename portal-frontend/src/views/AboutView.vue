<template>
  <div class="about-page">
    <div class="page-inner">

      <!-- ── 个人介绍卡片 ─────────────────────────────── -->
      <div class="profile-card fade-in-up">
        <div class="profile-avatar">
          <img v-if="profile.avatar" :src="profile.avatar" alt="avatar" />
          <div v-else class="avatar-fallback">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="online-dot"></div>
        </div>
        <div class="profile-info">
          <h1 class="profile-name">{{ profile.name }}</h1>
          <p class="profile-title">{{ profile.title }}</p>
          <p class="profile-bio">{{ profile.bio }}</p>

          <div class="profile-links">
            <a v-if="profile.github" :href="profile.github" target="_blank" class="link-btn github-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/>
              </svg>
              GitHub
            </a>
            <a v-if="profile.email" :href="`mailto:${profile.email}`" class="link-btn email-btn">
              <el-icon><Message /></el-icon>
              发邮件
            </a>
            <a v-if="profile.blog" :href="profile.blog" target="_blank" class="link-btn blog-btn">
              <el-icon><Reading /></el-icon>
              博客
            </a>
          </div>
        </div>

        <!-- 右侧快速统计 -->
        <div class="profile-stats">
          <div class="ps-item">
            <span class="ps-num">{{ stats.totalProjects }}</span>
            <span class="ps-label">个项目</span>
          </div>
          <div class="ps-divider"></div>
          <div class="ps-item">
            <span class="ps-num">{{ stats.activeProjects }}</span>
            <span class="ps-label">运行中</span>
          </div>
          <div class="ps-divider"></div>
          <div class="ps-item">
            <span class="ps-num">{{ formatVisits(stats.totalVisits) }}</span>
            <span class="ps-label">总访问</span>
          </div>
        </div>
      </div>

      <div class="two-col">
        <!-- ── 技能进度条 ─────────────────────────────── -->
        <div class="section-card fade-in-up" style="animation-delay:.1s">
          <h2 class="section-title">
            <el-icon><TrendCharts /></el-icon> 技能熟练度
          </h2>
          <div class="skills-list">
            <div v-for="skill in profile.skills" :key="skill.name" class="skill-item">
              <div class="skill-header">
                <span class="skill-name">{{ skill.name }}</span>
                <span class="skill-pct">{{ skill.level }}%</span>
              </div>
              <div class="skill-bar">
                <div
                  class="skill-fill"
                  :style="{ width: animatedSkills ? skill.level + '%' : '0%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- ── 技术标签云 ─────────────────────────────── -->
        <div class="section-card fade-in-up" style="animation-delay:.2s">
          <h2 class="section-title">
            <el-icon><CollectionTag /></el-icon> 技术栈
          </h2>
          <div class="tag-cloud">
            <span
              v-for="(tag, i) in profile.skillTags"
              :key="tag"
              class="skill-tag"
              :style="{ animationDelay: `${i * 0.04}s` }"
            >{{ tag }}</span>
          </div>

          <h2 class="section-title" style="margin-top:28px">
            <el-icon><Timer /></el-icon> 开发理念
          </h2>
          <div class="philosophy-list">
            <div class="ph-item" v-for="ph in philosophies" :key="ph.title">
              <div class="ph-icon">{{ ph.icon }}</div>
              <div>
                <div class="ph-title">{{ ph.title }}</div>
                <div class="ph-desc">{{ ph.desc }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ── 项目时间线 ─────────────────────────────── -->
      <div class="section-card fade-in-up" style="animation-delay:.3s">
        <h2 class="section-title">
          <el-icon><Clock /></el-icon> 项目时间线
        </h2>
        <div class="timeline">
          <div
            v-for="(project, i) in timelineProjects"
            :key="project.id"
            class="timeline-item"
            :class="{ 'tl-right': i % 2 === 1 }"
          >
            <div class="tl-dot" :class="project.status === 'ACTIVE' ? 'dot-active' : 'dot-other'"></div>
            <div class="tl-card" @click="goProject(project.id)">
              <div class="tl-header">
                <span class="tl-name">{{ project.name }}</span>
                <el-tag :type="statusType(project.status)" size="small">{{ statusText(project.status) }}</el-tag>
              </div>
              <p class="tl-desc">{{ project.description }}</p>
              <div class="tl-footer">
                <span class="tl-cat">{{ project.categoryName }}</span>
                <span class="tl-date">{{ formatDate(project.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { PROFILE } from '@/api/mockData'
import { getProjects, getStats } from '@/api/portal'

const router = useRouter()
const profile = PROFILE
const animatedSkills = ref(false)
const stats = ref({ totalProjects: 0, activeProjects: 0, totalVisits: 0 })
const timelineProjects = ref([])

const philosophies = [
  { icon: '🎯', title: '用户优先', desc: '每个功能都从用户的真实需求出发' },
  { icon: '⚡', title: '快速迭代', desc: '先做出 MVP，再不断打磨细节' },
  { icon: '🔧', title: '代码质量', desc: '可读性与可维护性同等重要' },
  { icon: '📦', title: '简单架构', desc: '能用简单方案解决的绝不过度设计' },
]

const formatVisits = (n) => {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long' }) : ''

const statusType = (s) => ({ ACTIVE: 'success', MAINTENANCE: 'warning', OFFLINE: 'info' }[s] || 'info')
const statusText = (s) => ({ ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }[s] || s)

const goProject = (id) => router.push(`/project/${id}`)

onMounted(async () => {
  // 加载统计
  const statsRes = await getStats()
  stats.value = statsRes.data || stats.value

  // 加载项目时间线
  const projRes = await getProjects()
  timelineProjects.value = (projRes.data || []).slice(0, 6)

  // 延迟触发技能进度条动画
  setTimeout(() => { animatedSkills.value = true }, 300)
})
</script>

<style scoped>
.about-page {
  padding-top: 64px;
  min-height: 100vh;
}
.page-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 24px 60px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* ── 个人卡片 ── */
.profile-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  padding: 36px;
  display: flex;
  gap: 32px;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}
.profile-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: var(--gradient-primary);
}

.profile-avatar {
  flex-shrink: 0;
  position: relative;
}
.profile-avatar img,
.avatar-fallback {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}
.avatar-fallback {
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: white;
}
.online-dot {
  position: absolute;
  bottom: 6px; right: 6px;
  width: 14px; height: 14px;
  background: var(--success-color);
  border-radius: 50%;
  border: 2px solid var(--bg-secondary);
  animation: pulse-glow 2s infinite;
}

.profile-info { flex: 1; }
.profile-name {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 6px;
}
.profile-title {
  font-size: 14px;
  color: var(--primary-light);
  margin-bottom: 14px;
  font-weight: 500;
}
.profile-bio {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 15px;
  margin-bottom: 20px;
  max-width: 560px;
}
.profile-links { display: flex; gap: 10px; flex-wrap: wrap; }
.link-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: var(--transition);
  cursor: pointer;
}
.github-btn {
  background: rgba(255,255,255,0.07);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}
.github-btn:hover { background: rgba(255,255,255,0.12); border-color: var(--text-muted); }
.email-btn {
  background: rgba(6, 182, 212, 0.1);
  border: 1px solid rgba(6, 182, 212, 0.3);
  color: var(--secondary-color);
}
.email-btn:hover { background: rgba(6, 182, 212, 0.2); }
.blog-btn {
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.3);
  color: var(--primary-light);
}
.blog-btn:hover { background: rgba(99, 102, 241, 0.2); }

.profile-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  padding: 20px 28px;
  background: rgba(99,102,241,0.06);
  border: 1px solid rgba(99,102,241,0.15);
  border-radius: 16px;
  flex-shrink: 0;
}
.ps-item { text-align: center; }
.ps-num {
  display: block;
  font-size: 26px;
  font-weight: 700;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.ps-label { font-size: 12px; color: var(--text-muted); }
.ps-divider { width: 40px; height: 1px; background: var(--border-color); }

/* ── 双列布局 ── */
.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

/* ── 通用卡片 ── */
.section-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 28px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 20px;
}
.section-title .el-icon { color: var(--primary-light); font-size: 18px; }

/* ── 技能进度条 ── */
.skills-list { display: flex; flex-direction: column; gap: 16px; }
.skill-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 14px;
}
.skill-name { color: var(--text-primary); font-weight: 500; }
.skill-pct { color: var(--primary-light); font-weight: 600; }
.skill-bar {
  height: 6px;
  background: rgba(99,102,241,0.1);
  border-radius: 3px;
  overflow: hidden;
}
.skill-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: 3px;
  transition: width 1.2s cubic-bezier(.4,0,.2,1);
}

/* ── 标签云 ── */
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.skill-tag {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  background: rgba(99,102,241,0.1);
  border: 1px solid rgba(99,102,241,0.25);
  color: var(--primary-light);
  cursor: default;
  transition: var(--transition);
  animation: fadeInUp 0.4s ease both;
}
.skill-tag:hover {
  background: rgba(99,102,241,0.2);
  transform: translateY(-2px);
}

/* ── 开发理念 ── */
.philosophy-list { display: flex; flex-direction: column; gap: 14px; }
.ph-item { display: flex; align-items: flex-start; gap: 12px; }
.ph-icon { font-size: 22px; flex-shrink: 0; line-height: 1.4; }
.ph-title { font-size: 14px; font-weight: 600; color: var(--text-primary); margin-bottom: 2px; }
.ph-desc { font-size: 12px; color: var(--text-muted); line-height: 1.6; }

/* ── 时间线 ── */
.timeline {
  position: relative;
  padding-left: 28px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 8px; top: 0; bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, var(--primary-color), transparent);
}
.timeline-item {
  position: relative;
  margin-bottom: 24px;
}
.tl-dot {
  position: absolute;
  left: -24px;
  top: 14px;
  width: 12px; height: 12px;
  border-radius: 50%;
  border: 2px solid var(--bg-secondary);
  z-index: 1;
}
.dot-active { background: var(--success-color); box-shadow: 0 0 6px var(--success-color); }
.dot-other { background: var(--border-color); }

.tl-card {
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  transition: var(--transition);
}
.tl-card:hover {
  border-color: rgba(99,102,241,0.4);
  transform: translateX(4px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}
.tl-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}
.tl-name { font-size: 15px; font-weight: 600; color: var(--text-primary); }
.tl-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.tl-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 900px) {
  .profile-card { flex-direction: column; }
  .profile-stats { flex-direction: row; width: 100%; justify-content: center; }
  .ps-divider { width: 1px; height: 40px; }
  .two-col { grid-template-columns: 1fr; }
}
</style>
