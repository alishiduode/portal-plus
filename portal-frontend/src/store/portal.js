import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getProjects, getCategories, getStats } from '@/api/portal'

export const usePortalStore = defineStore('portal', () => {
  // 状态
  const projects = ref([])
  const categories = ref([])
  const stats = ref({ totalProjects: 0, activeProjects: 0, totalVisits: 0, totalCategories: 0 })
  const loading = ref(false)
  const currentCategory = ref(null)
  const searchKeyword = ref('')

  // 计算属性
  const filteredProjects = computed(() => {
    return projects.value
  })

  const featuredProjects = computed(() => {
    return projects.value.filter(p => p.isFeatured)
  })

  // 加载所有数据
  async function loadAll() {
    loading.value = true
    try {
      const [projectsRes, categoriesRes, statsRes] = await Promise.all([
        getProjects(),
        getCategories(),
        getStats()
      ])
      projects.value = projectsRes.data || []
      categories.value = categoriesRes.data || []
      stats.value = statsRes.data || stats.value
    } finally {
      loading.value = false
    }
  }

  // 加载项目（带筛选）
  async function loadProjects(params = {}) {
    loading.value = true
    try {
      const res = await getProjects(params)
      projects.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  // 加载统计
  async function loadStats() {
    const res = await getStats()
    stats.value = res.data || stats.value
  }

  // 加载分类
  async function loadCategories() {
    const res = await getCategories()
    categories.value = res.data || []
  }

  return {
    projects,
    categories,
    stats,
    loading,
    currentCategory,
    searchKeyword,
    filteredProjects,
    featuredProjects,
    loadAll,
    loadProjects,
    loadStats,
    loadCategories
  }
})
