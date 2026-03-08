import request from './request'

// ===== 项目接口 =====

/**
 * 获取所有项目
 */
export const getProjects = (params) => {
  return request.get('/projects', { params })
}

/**
 * 获取项目详情
 */
export const getProjectById = (id) => {
  return request.get(`/projects/${id}`)
}

/**
 * 访问项目（记录并获取跳转URL）
 */
export const visitProject = (id) => {
  return request.post(`/projects/${id}/visit`)
}

/**
 * 获取统计数据
 */
export const getStats = () => {
  return request.get('/projects/stats')
}

/**
 * 创建项目（管理员）
 */
export const createProject = (data) => {
  return request.post('/projects', data)
}

/**
 * 更新项目（管理员）
 */
export const updateProject = (id, data) => {
  return request.put(`/projects/${id}`, data)
}

/**
 * 删除项目（管理员）
 */
export const deleteProject = (id) => {
  return request.delete(`/projects/${id}`)
}

// ===== 分类接口 =====

/**
 * 获取所有分类
 */
export const getCategories = () => {
  return request.get('/categories')
}

/**
 * 创建分类（管理员）
 */
export const createCategory = (data) => {
  return request.post('/categories', data)
}

/**
 * 更新分类（管理员）
 */
export const updateCategory = (id, data) => {
  return request.put(`/categories/${id}`, data)
}

/**
 * 删除分类（管理员）
 */
export const deleteCategory = (id) => {
  return request.delete(`/categories/${id}`)
}
