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
        <router-link to="/" class="nav-link">
          <el-icon><House /></el-icon> 首页
        </router-link>
        <router-link to="/projects" class="nav-link">
          <el-icon><Grid /></el-icon> 全部项目
        </router-link>
      </nav>

      <!-- 搜索框 -->
      <div class="header-search hide-mobile">
        <el-input
          v-model="searchQuery"
          placeholder="搜索项目..."
          prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleClear"
          class="search-input"
        />
      </div>

      <!-- 右侧操作 -->
      <div class="header-actions">
        <el-button class="admin-btn" size="small" @click="goAdmin">
          <el-icon><Setting /></el-icon>
          <span class="hide-mobile">管理后台</span>
        </el-button>
        <!-- 移动端菜单 -->
        <el-button class="mobile-menu-btn" size="small" @click="drawerVisible = true" style="display:none">
          <el-icon><Menu /></el-icon>
        </el-button>
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
        <router-link to="/admin" class="mobile-nav-item" @click="drawerVisible = false">
          <el-icon><Setting /></el-icon> 管理后台
        </router-link>
        <div class="mobile-search">
          <el-input v-model="searchQuery" placeholder="搜索项目..." @keyup.enter="handleSearch" clearable />
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchQuery = ref('')
const isScrolled = ref(false)
const drawerVisible = ref(false)

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/projects', query: { keyword: searchQuery.value.trim() } })
    drawerVisible.value = false
  }
}

const handleClear = () => {
  router.push('/projects')
}

const goAdmin = () => {
  router.push('/admin')
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: var(--transition);
  height: 64px;
}

.app-header.scrolled {
  border-bottom-color: var(--border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  width: 36px;
  height: 36px;
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
  letter-spacing: -0.5px;
}

.logo-accent {
  color: var(--primary-light);
}

.nav-links {
  display: flex;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 14px;
  transition: var(--transition);
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--text-primary);
  background: rgba(99, 102, 241, 0.1);
}

.nav-link.router-link-exact-active {
  color: var(--primary-light);
}

.header-search {
  flex: 1;
  max-width: 280px;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid var(--border-color);
  box-shadow: none;
  border-radius: 10px;
}

.search-input :deep(.el-input__inner) {
  color: var(--text-primary);
}

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
}

.admin-btn {
  background: rgba(99, 102, 241, 0.1) !important;
  border: 1px solid rgba(99, 102, 241, 0.3) !important;
  color: var(--primary-light) !important;
  border-radius: 8px !important;
  gap: 6px;
}

.admin-btn:hover {
  background: rgba(99, 102, 241, 0.2) !important;
}

/* 移动端 */
.mobile-nav {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

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

.mobile-nav-item:hover {
  background: rgba(99, 102, 241, 0.1);
  color: var(--text-primary);
}

.mobile-search {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .hide-mobile {
    display: none !important;
  }
  .mobile-menu-btn {
    display: flex !important;
  }
}
</style>
