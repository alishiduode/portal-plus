<template>
  <div class="projects-page">
    <div class="page-inner">
      <!-- 页头 -->
      <div class="page-header">
        <h1 class="page-title">全部项目</h1>
        <p class="page-desc">共 {{ filteredProjects.length }} 个项目</p>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <!-- 搜索 -->
        <el-input
          v-model="keyword"
          placeholder="搜索项目名称、描述、技术栈..."
          prefix-icon="Search"
          clearable
          size="large"
          class="filter-search"
          @input="debouncedSearch"
          @clear="handleClear"
        />

        <!-- 分类筛选 -->
        <div class="filter-cats">
          <div
            class="filter-cat"
            :class="{ active: !activeCat }"
            @click="setCategory(null)"
          >全部</div>
          <div
            v-for="cat in store.categories"
            :key="cat.id"
            class="filter-cat"
            :class="{ active: activeCat === cat.id }"
            @click="setCategory(cat.id)"
          >{{ cat.name }}</div>
        </div>

        <!-- 状态筛选 -->
        <el-select v-model="statusFilter" placeholder="状态" clearable class="filter-status">
          <el-option label="运行中" value="ACTIVE" />
          <el-option label="维护中" value="MAINTENANCE" />
          <el-option label="已下线" value="OFFLINE" />
        </el-select>
      </div>

      <!-- 项目网格 -->
      <div v-loading="loading" class="projects-grid">
        <template v-if="filteredProjects.length > 0">
          <ProjectCard
            v-for="(project, i) in filteredProjects"
            :key="project.id"
            :project="project"
            class="fade-in-up"
            :style="{ animationDelay: `${i * 0.06}s` }"
          />
        </template>
        <div v-else class="empty-state">
          <el-empty description="没有找到匹配的项目">
            <el-button @click="resetFilters">清除筛选</el-button>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePortalStore } from '@/store/portal'
import { getProjects } from '@/api/portal'
import ProjectCard from '@/components/ProjectCard.vue'

const store = usePortalStore()
const route = useRoute()
const router = useRouter()

const allProjects = ref([])
const keyword = ref('')
const activeCat = ref(null)
const statusFilter = ref('')
const loading = ref(false)

const filteredProjects = computed(() => {
  let list = allProjects.value
  if (statusFilter.value) {
    list = list.filter(p => p.status === statusFilter.value)
  }
  return list
})

let searchTimer = null
const debouncedSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadProjects(), 400)
}

const handleClear = () => {
  keyword.value = ''
  loadProjects()
}

const setCategory = (catId) => {
  activeCat.value = catId
  loadProjects()
}

const resetFilters = () => {
  keyword.value = ''
  activeCat.value = null
  statusFilter.value = ''
  loadProjects()
}

const loadProjects = async () => {
  loading.value = true
  try {
    const params = {}
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (activeCat.value) params.categoryId = activeCat.value
    const res = await getProjects(params)
    allProjects.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!store.categories.length) await store.loadCategories()
  // 处理URL参数
  if (route.query.keyword) keyword.value = route.query.keyword
  if (route.query.categoryId) activeCat.value = Number(route.query.categoryId)
  await loadProjects()
})

watch(() => route.query.keyword, (val) => {
  if (val) {
    keyword.value = val
    loadProjects()
  }
})
</script>

<style scoped>
.projects-page {
  padding-top: 64px;
  min-height: 100vh;
}

.page-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 40px 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-desc {
  color: var(--text-muted);
  font-size: 14px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 32px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
}

.filter-search {
  flex: 1;
  min-width: 200px;
}

.filter-search :deep(.el-input__wrapper) {
  background: var(--bg-color);
  border-color: var(--border-color);
  box-shadow: none;
}

.filter-cats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.filter-cat {
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
}

.filter-cat:hover {
  border-color: var(--primary-color);
  color: var(--primary-light);
}

.filter-cat.active {
  background: rgba(99, 102, 241, 0.15);
  border-color: var(--primary-color);
  color: var(--primary-light);
}

.filter-status {
  width: 130px;
}

.filter-status :deep(.el-select__wrapper) {
  background: var(--bg-color);
  border-color: var(--border-color);
  box-shadow: none;
  color: var(--text-primary);
}

/* 项目网格 */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 0;
}

@media (max-width: 768px) {
  .projects-grid {
    grid-template-columns: 1fr;
  }
  .filter-bar {
    flex-direction: column;
  }
}
</style>
