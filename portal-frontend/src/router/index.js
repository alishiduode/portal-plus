import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页 - 开发者门户' }
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import('@/views/ProjectsView.vue'),
    meta: { title: '全部项目 - 开发者门户' }
  },
  {
    path: '/project/:id',
    name: 'ProjectDetail',
    component: () => import('@/views/ProjectDetailView.vue'),
    meta: { title: '项目详情 - 开发者门户' }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/AboutView.vue'),
    meta: { title: '关于我 - 开发者门户' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/AdminView.vue'),
    meta: { title: '管理后台 - 开发者门户' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0, behavior: 'smooth' }
  }
})

router.beforeEach((to) => {
  document.title = to.meta.title || '开发者门户'
})

export default router
