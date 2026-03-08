<template>
  <div class="home-page">
    <!-- Hero区域 -->
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-orb orb-1"></div>
        <div class="hero-orb orb-2"></div>
        <div class="hero-grid"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">
          <el-icon><Lightning /></el-icon> 
          个人开发者作品集
        </div>
        <h1 class="hero-title">
          将想法变成<br/>
          <span class="gradient-text">真实产品</span>
        </h1>
        <p class="hero-desc">
          这里收录了我开发的所有在线应用 —— 每一个都是从零开始，解决真实问题的完整产品。
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" round @click="$router.push('/projects')">
            <el-icon><Grid /></el-icon>
            浏览全部项目
          </el-button>
          <el-button size="large" round class="ghost-btn" @click="scrollToFeatured">
            <el-icon><Star /></el-icon>
            查看精选
          </el-button>
        </div>
        <!-- 统计数据 -->
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-num">{{ store.stats.totalProjects }}</span>
            <span class="stat-label">个项目</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">{{ store.stats.activeProjects }}</span>
            <span class="stat-label">运行中</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">{{ formatVisits(store.stats.totalVisits) }}</span>
            <span class="stat-label">总访问</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">{{ store.stats.totalCategories }}</span>
            <span class="stat-label">个分类</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类快捷入口 -->
    <section class="categories-section">
      <div class="section-inner">
        <div class="categories-scroll">
          <div
            v-for="cat in store.categories"
            :key="cat.id"
            class="category-pill"
            :class="{ active: activeCat === cat.id }"
            @click="filterByCategory(cat.id)"
          >
            <el-icon><FolderOpened /></el-icon>
            {{ cat.name }}
            <span class="cat-count">{{ getCatCount(cat.id) }}</span>
          </div>
          <div class="category-pill" :class="{ active: activeCat === null }" @click="filterByCategory(null)">
            <el-icon><Grid /></el-icon>
            全部
          </div>
        </div>
      </div>
    </section>

    <!-- 精选项目 -->
    <section ref="featuredSection" class="featured-section" v-if="featuredProjects.length">
      <div class="section-inner">
        <div class="section-header">
          <div>
            <h2 class="section-title">
              <el-icon class="title-icon"><Star /></el-icon>
              精选项目
            </h2>
            <p class="section-subtitle">重点推荐，功能完善，值得一试</p>
          </div>
          <router-link to="/projects?featured=true" class="view-all">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="projects-grid featured-grid" v-loading="store.loading">
          <ProjectCard
            v-for="(project, index) in featuredProjects"
            :key="project.id"
            :project="project"
            class="fade-in-up"
            :style="{ animationDelay: `${index * 0.1}s` }"
          />
        </div>
      </div>
    </section>

    <!-- 最新项目 -->
    <section class="latest-section">
      <div class="section-inner">
        <div class="section-header">
          <div>
            <h2 class="section-title">
              <el-icon class="title-icon"><Clock /></el-icon>
              {{ activeCat ? currentCatName + '类' : '全部' }}项目
            </h2>
            <p class="section-subtitle">共 {{ displayProjects.length }} 个项目</p>
          </div>
          <router-link to="/projects" class="view-all">
            全部项目 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="projects-grid" v-loading="store.loading">
          <ProjectCard
            v-for="(project, index) in displayProjects.slice(0, 6)"
            :key="project.id"
            :project="project"
            class="fade-in-up"
            :style="{ animationDelay: `${index * 0.08}s` }"
          />
        </div>
        <div v-if="displayProjects.length > 6" class="load-more">
          <el-button size="large" round class="ghost-btn" @click="$router.push('/projects')">
            查看更多项目
            <el-icon><ArrowDown /></el-icon>
          </el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { usePortalStore } from '@/store/portal'
import { getProjects } from '@/api/portal'
import ProjectCard from '@/components/ProjectCard.vue'

const store = usePortalStore()
const featuredSection = ref(null)
const activeCat = ref(null)
const allProjects = ref([])

const featuredProjects = computed(() =>
  allProjects.value.filter(p => p.isFeatured).slice(0, 3)
)

const displayProjects = computed(() => {
  if (activeCat.value) {
    return allProjects.value.filter(p => p.categoryId === activeCat.value)
  }
  return allProjects.value
})

const currentCatName = computed(() => {
  const cat = store.categories.find(c => c.id === activeCat.value)
  return cat ? cat.name : ''
})

const getCatCount = (catId) =>
  allProjects.value.filter(p => p.categoryId === catId).length

const formatVisits = (n) => {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return n.toString()
}

const scrollToFeatured = () => {
  featuredSection.value?.scrollIntoView({ behavior: 'smooth' })
}

const filterByCategory = async (catId) => {
  activeCat.value = catId
  if (catId) {
    const res = await getProjects({ categoryId: catId })
    allProjects.value = res.data || []
  } else {
    const res = await getProjects()
    allProjects.value = res.data || []
  }
}

onMounted(async () => {
  await store.loadAll()
  allProjects.value = store.projects
})
</script>

<style scoped>
.home-page {
  padding-top: 64px;
}

/* Hero */
.hero-section {
  position: relative;
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: var(--primary-color);
  top: -200px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: var(--secondary-color);
  bottom: -100px;
  left: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(99, 102, 241, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(99, 102, 241, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
}

.hero-content {
  position: relative;
  text-align: center;
  padding: 80px 24px;
  max-width: 800px;
  margin: 0 auto;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.3);
  color: var(--primary-light);
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: clamp(36px, 6vw, 72px);
  font-weight: 800;
  line-height: 1.15;
  color: var(--text-primary);
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.gradient-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  color: var(--text-secondary);
  line-height: 1.8;
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 60px;
}

.ghost-btn {
  background: transparent !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
}

.ghost-btn:hover {
  border-color: var(--primary-color) !important;
  color: var(--primary-light) !important;
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
  display: block;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-color);
}

/* 分类 */
.categories-section {
  padding: 20px 0;
  border-bottom: 1px solid var(--border-color);
}

.section-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.categories-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 4px 0;
  scrollbar-width: none;
}

.categories-scroll::-webkit-scrollbar { display: none; }

.category-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: var(--transition);
}

.category-pill:hover {
  border-color: var(--primary-color);
  color: var(--primary-light);
}

.category-pill.active {
  background: rgba(99, 102, 241, 0.15);
  border-color: var(--primary-color);
  color: var(--primary-light);
}

.cat-count {
  background: rgba(99, 102, 241, 0.2);
  color: var(--primary-light);
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 11px;
}

/* 章节 */
.featured-section,
.latest-section {
  padding: 60px 0;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.title-icon {
  color: var(--primary-light);
}

.section-subtitle {
  color: var(--text-muted);
  font-size: 14px;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--primary-light);
  text-decoration: none;
  font-size: 14px;
  transition: var(--transition);
}

.view-all:hover {
  gap: 8px;
}

/* 项目网格 */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.featured-grid {
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
}

.load-more {
  text-align: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .hero-stats {
    gap: 20px;
  }
  .stat-divider {
    display: none;
  }
  .projects-grid {
    grid-template-columns: 1fr;
  }
}
</style>
