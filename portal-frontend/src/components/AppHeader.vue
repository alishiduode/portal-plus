<template>
  <header class="app-header" :class="{ scrolled: isScrolled }">
    <div class="header-inner">

      <!-- Logo -->
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <el-icon><Monitor /></el-icon>
        </div>
        <span class="logo-text">Dev<span class="logo-accent">Portal</span></span>
      </router-link>

      <!-- 导航 -->
      <nav class="nav-links hide-mobile">
        <router-link to="/" class="nav-link" exact>
          <el-icon><House /></el-icon> 首页
        </router-link>
        <router-link to="/projects" class="nav-link">
          <el-icon><Grid /></el-icon> 全部项目
        </router-link>
        <router-link to="/about" class="nav-link">
          <el-icon><User /></el-icon> 关于我
        </router-link>
      </nav>

      <!-- 全局搜索框（带实时联想） -->
      <div class="header-search hide-mobile" ref="searchWrapRef">
        <div class="search-box">
          <el-icon class="search-prefix"><Search /></el-icon>
          <input
            ref="searchInputRef"
            v-model="searchQuery"
            class="search-input"
            placeholder="搜索项目..."
            autocomplete="off"
            @input="onSearchInput"
            @keydown.enter="submitSearch"
            @keydown.esc="closeSuggest"
            @focus="onSearchFocus"
          />
          <button v-if="searchQuery" class="search-clear" @click="clearSearch">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <!-- 联想下拉 -->
        <transition name="suggest">
          <div v-if="showSuggest && suggestions.length" class="suggest-panel">
            <div
              v-for="item in suggestions"
              :key="item.id"
              class="suggest-item"
              @mousedown.prevent="selectSuggest(item)"
            >
              <div class="si-icon" :style="{ background: getIconBg(item.name) }">
                {{ item.name.charAt(0).toUpperCase() }}
              </div>
              <div class="si-info">
                <span class="si-name" v-html="highlight(item.name)"></span>
                <span class="si-cat">{{ item.categoryName }}</span>
              </div>
              <div class="si-status" :class="'s-' + item.status.toLowerCase()">
                {{ { ACTIVE: '运行中', MAINTENANCE: '维护中', OFFLINE: '已下线' }[item.status] }}
              </div>
            </div>
            <div class="suggest-footer" @mousedown.prevent="submitSearch">
              <el-icon><Search /></el-icon>
              搜索"{{ searchQuery }}"的全部结果
            </div>
          </div>
        </transition>

        <!-- 无结果 -->
        <transition name="suggest">
          <div v-if="showSuggest && searchQuery.trim() && !suggestions.length && !searchLoading" class="suggest-panel">
            <div class="suggest-empty">
              <el-icon><Search /></el-icon>
              没有找到"{{ searchQuery }}"相关项目
            </div>
          </div>
        </transition>
      </div>

      <!-- 右侧 -->
      <div class="header-actions">
        <el-button class="admin-btn hide-mobile" size="small" @click="goAdmin">
          <el-icon><Setting /></el-icon>
          管理后台
        </el-button>
        <!-- 移动端汉堡 -->
        <button class="mobile-menu-btn show-mobile" @click="drawerVisible = true">
          <el-icon><Menu /></el-icon>
        </button>
      </div>
    </div>

    <!-- 移动端抽屉 -->
    <el-drawer v-model="drawerVisible" direction="rtl" size="260px" :with-header="false">
      <div class="mobile-nav">
        <router-link to="/" class="mobile-nav-item" @click="drawerVisible = false">
          <el-icon><House /></el-icon> 首页
        </router-link>
        <router-link to="/projects" class="mobile-nav-item" @click="drawerVisible = false">
          <el-icon><Grid /></el-icon> 全部项目
        </router-link>
        <router-link to="/about" class="mobile-nav-item" @click="drawerVisible = false">
          <el-icon><User /></el-icon> 关于我
        </router-link>
        <router-link to="/admin" class="mobile-nav-item" @click="drawerVisible = false">
          <el-icon><Setting /></el-icon> 管理后台
        </router-link>
        <div class="mobile-search">
          <el-input
            v-model="searchQuery"
            placeholder="搜索项目..."
            @keyup.enter="submitSearch"
            clearable
            prefix-icon="Search"
          />
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProjects } from '@/api/portal'

const router = useRouter()
const searchQuery = ref('')
const isScrolled = ref(false)
const drawerVisible = ref(false)
const showSuggest = ref(false)
const suggestions = ref([])
const searchLoading = ref(false)
const searchWrapRef = ref(null)
const searchInputRef = ref(null)

// ── 图标渐变色（与 ProjectCard 一致） ──────────────────────────
const GRADIENTS = [
  'linear-gradient(135deg,#6366f1,#8b5cf6)',
  'linear-gradient(135deg,#06b6d4,#3b82f6)',
  'linear-gradient(135deg,#10b981,#06b6d4)',
  'linear-gradient(135deg,#f59e0b,#ef4444)',
  'linear-gradient(135deg,#ec4899,#8b5cf6)',
  'linear-gradient(135deg,#14b8a6,#6366f1)',
]
const getIconBg = (name) => GRADIENTS[name.charCodeAt(0) % GRADIENTS.length]

// ── 高亮匹配文字 ──────────────────────────────────────────────
const highlight = (text) => {
  if (!searchQuery.value.trim()) return text
  const kw = searchQuery.value.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return text.replace(new RegExp(kw, 'gi'), m => `<mark>${m}</mark>`)
}

// ── 搜索防抖 ──────────────────────────────────────────────────
let debounceTimer = null
const onSearchInput = () => {
  clearTimeout(debounceTimer)
  if (!searchQuery.value.trim()) {
    suggestions.value = []
    showSuggest.value = false
    return
  }
  debounceTimer = setTimeout(fetchSuggestions, 280)
}

const fetchSuggestions = async () => {
  if (!searchQuery.value.trim()) return
  searchLoading.value = true
  try {
    const res = await getProjects({ keyword: searchQuery.value.trim() })
    suggestions.value = (res.data || []).slice(0, 6)
    showSuggest.value = true
  } finally {
    searchLoading.value = false
  }
}

const onSearchFocus = () => {
  if (searchQuery.value.trim() && suggestions.value.length) {
    showSuggest.value = true
  }
}

const closeSuggest = () => {
  showSuggest.value = false
}

const clearSearch = () => {
  searchQuery.value = ''
  suggestions.value = []
  showSuggest.value = false
  searchInputRef.value?.focus()
}

const selectSuggest = (item) => {
  showSuggest.value = false
  searchQuery.value = ''
  router.push(`/project/${item.id}`)
}

const submitSearch = () => {
  showSuggest.value = false
  const kw = searchQuery.value.trim()
  if (kw) {
    router.push({ path: '/projects', query: { keyword: kw } })
    drawerVisible.value = false
  }
}

const goAdmin = () => router.push('/admin')

// ── 点击外部关闭联想 ──────────────────────────────────────────
const handleOutsideClick = (e) => {
  if (searchWrapRef.value && !searchWrapRef.value.contains(e.target)) {
    showSuggest.value = false
  }
}

// ── 滚动效果 ──────────────────────────────────────────────────
const handleScroll = () => { isScrolled.value = window.scrollY > 20 }

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleOutsideClick)
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleOutsideClick)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: var(--transition);
  height: 64px;
}
.app-header.scrolled {
  border-bottom-color: var(--border-color);
  box-shadow: 0 4px 24px rgba(0,0,0,.3);
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 20px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon {
  width: 36px; height: 36px;
  background: var(--gradient-primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -.5px;
}
.logo-accent { color: var(--primary-light); }

/* 导航 */
.nav-links { display: flex; gap: 2px; }
.nav-link {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border-radius: 8px;
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 14px;
  transition: var(--transition);
}
.nav-link:hover { color: var(--text-primary); background: rgba(99,102,241,.08); }
.nav-link.router-link-active { color: var(--primary-light); background: rgba(99,102,241,.1); }

/* 搜索框 */
.header-search {
  flex: 1;
  max-width: 300px;
  position: relative;
}
.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 0 12px;
  transition: var(--transition);
}
.search-box:focus-within {
  border-color: rgba(99,102,241,.5);
  box-shadow: 0 0 0 3px rgba(99,102,241,.12);
}
.search-prefix { color: var(--text-muted); font-size: 14px; flex-shrink: 0; }
.search-input {
  flex: 1;
  height: 36px;
  background: transparent;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: 14px;
}
.search-input::placeholder { color: var(--text-muted); }
.search-clear {
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 2px;
  display: flex;
  align-items: center;
  border-radius: 4px;
  transition: var(--transition);
}
.search-clear:hover { color: var(--text-primary); }

/* 联想下拉 */
.suggest-panel {
  position: absolute;
  top: calc(100% + 8px);
  left: 0; right: 0;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0,0,0,.4);
  overflow: hidden;
  z-index: 9999;
}
.suggest-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  cursor: pointer;
  transition: background .15s;
}
.suggest-item:hover { background: rgba(99,102,241,.07); }
.si-icon {
  width: 32px; height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}
.si-info { flex: 1; min-width: 0; }
.si-name {
  display: block;
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.si-name :deep(mark) {
  background: transparent;
  color: var(--primary-light);
  font-weight: 700;
}
.si-cat { font-size: 11px; color: var(--text-muted); }
.si-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 12px;
  white-space: nowrap;
}
.s-active   { background: rgba(16,185,129,.12); color: var(--success-color); }
.s-maintenance { background: rgba(245,158,11,.12); color: var(--warning-color); }
.s-offline  { background: rgba(100,116,139,.12); color: var(--text-muted); }

.suggest-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  font-size: 13px;
  color: var(--primary-light);
  border-top: 1px solid var(--border-color);
  cursor: pointer;
  transition: background .15s;
}
.suggest-footer:hover { background: rgba(99,102,241,.07); }

.suggest-empty {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 14px;
  font-size: 13px;
  color: var(--text-muted);
}

.suggest-enter-active, .suggest-leave-active { transition: opacity .15s, transform .15s; }
.suggest-enter-from, .suggest-leave-to { opacity: 0; transform: translateY(-6px); }

/* 右侧 */
.header-actions { margin-left: auto; display: flex; align-items: center; gap: 10px; }
.admin-btn {
  background: rgba(99,102,241,.1) !important;
  border: 1px solid rgba(99,102,241,.3) !important;
  color: var(--primary-light) !important;
  border-radius: 8px !important;
  gap: 6px;
  display: flex;
  align-items: center;
}
.admin-btn:hover { background: rgba(99,102,241,.18) !important; }

.mobile-menu-btn {
  display: none;
  width: 36px; height: 36px;
  border-radius: 8px;
  background: rgba(30,41,59,.8);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  transition: var(--transition);
}
.mobile-menu-btn:hover { color: var(--text-primary); border-color: var(--primary-color); }

/* 移动端抽屉 */
.mobile-nav { padding: 20px; display: flex; flex-direction: column; gap: 6px; }
.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 15px;
  transition: var(--transition);
}
.mobile-nav-item:hover, .mobile-nav-item.router-link-active {
  background: rgba(99,102,241,.1);
  color: var(--primary-light);
}
.mobile-search { margin-top: 12px; }

@media (max-width: 768px) {
  .hide-mobile { display: none !important; }
  .show-mobile { display: flex !important; }
  .mobile-menu-btn { display: flex; }
}
</style>
