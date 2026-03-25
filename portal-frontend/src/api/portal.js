import request from './request'
import {
  MOCK_CATEGORIES,
  MOCK_PROJECTS,
  MOCK_STATS,
} from './mockData'

// ── 工具：把 mock 包成和后端一样的 Result 格式 ──────────────────
const mockResult = (data) => ({ code: 200, message: 'success', data })

// ── 工具：过滤/搜索 mock 项目 ──────────────────────────────────
function filterMockProjects(params = {}) {
  let list = [...MOCK_PROJECTS]
  if (params.keyword) {
    const kw = params.keyword.toLowerCase()
    list = list.filter(p =>
      p.name.toLowerCase().includes(kw) ||
      (p.description || '').toLowerCase().includes(kw) ||
      (p.techStack || '').toLowerCase().includes(kw) ||
      (p.tags || '').toLowerCase().includes(kw)
    )
  }
  if (params.categoryId) {
    list = list.filter(p => p.categoryId === Number(params.categoryId))
  }
  if (params.featured) {
    list = list.filter(p => p.isFeatured)
  }
  return list
}

// ═══════════════════════════════════════════════════════════════
//  项目接口
// ═══════════════════════════════════════════════════════════════

/**
 * 获取所有项目（支持 keyword / categoryId / featured 参数）
 */
export const getProjects = (params) => {
  return request.get('/projects', { params }).catch(() => {
    return mockResult(filterMockProjects(params))
  })
}

/**
 * 获取项目详情
 */
export const getProjectById = (id) => {
  return request.get(`/projects/${id}`).catch(() => {
    const p = MOCK_PROJECTS.find(p => p.id === Number(id))
    if (!p) return Promise.reject(new Error('项目不存在'))
    return mockResult(p)
  })
}

/**
 * 访问项目（记录访问量，返回跳转 URL）
 * 无后端时直接返回项目 URL，浏览器负责跳转
 */
export const visitProject = (id) => {
  return request.post(`/projects/${id}/visit`).catch(() => {
    const p = MOCK_PROJECTS.find(p => p.id === Number(id))
    return mockResult(p ? p.url : '#')
  })
}

/**
 * 获取统计数据
 */
export const getStats = () => {
  return request.get('/projects/stats').catch(() => mockResult(MOCK_STATS))
}

/**
 * 创建项目（管理员）
 */
export const createProject = (data) => request.post('/projects', data)

/**
 * 更新项目（管理员）
 */
export const updateProject = (id, data) => request.put(`/projects/${id}`, data)

/**
 * 删除项目（管理员）
 */
export const deleteProject = (id) => request.delete(`/projects/${id}`)

// ═══════════════════════════════════════════════════════════════
//  分类接口
// ═══════════════════════════════════════════════════════════════

/**
 * 获取所有分类
 */
export const getCategories = () => {
  return request.get('/categories').catch(() => mockResult(MOCK_CATEGORIES))
}

/**
 * 创建分类（管理员）
 */
export const createCategory = (data) => request.post('/categories', data)

/**
 * 更新分类（管理员）
 */
export const updateCategory = (id, data) => request.put(`/categories/${id}`, data)

/**
 * 删除分类（管理员）
 */
export const deleteCategory = (id) => request.delete(`/categories/${id}`)

// ═══════════════════════════════════════════════════════════════
//  管理员登录验证（调用 /projects 接口探测 Basic Auth）
// ═══════════════════════════════════════════════════════════════

/**
 * 尝试用 Basic Auth 登录，成功返回 true，失败 throw Error
 */
export const adminLogin = (username, password) => {
  const token = btoa(`${username}:${password}`)
  return request.get('/categories', {
    headers: { Authorization: `Basic ${token}` },
  }).then(() => token)
}
