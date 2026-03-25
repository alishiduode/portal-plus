import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 8000,
  headers: { 'Content-Type': 'application/json' },
})

// ── 请求拦截器 ──────────────────────────────────────────────────
request.interceptors.request.use(
  config => {
    const adminToken = localStorage.getItem('adminToken')
    if (adminToken && !config.headers.Authorization) {
      config.headers.Authorization = `Basic ${adminToken}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ── 响应拦截器 ──────────────────────────────────────────────────
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 只对管理员写操作弹错；读操作由各 API 函数自行 catch 降级
      if (response.config?.method !== 'get') {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    const status = error.response?.status
    // 401/403 只在写操作时提示，读操作降级静默处理
    if (status === 401 || status === 403) {
      if (error.config?.method !== 'get') {
        ElMessage.error(status === 401 ? '请先登录管理员账户' : '权限不足')
      }
    } else if (status >= 500) {
      ElMessage.error('服务器异常，已切换为离线数据')
    }
    // 网络不通（CORS / 无后端）时静默 reject，由上层 .catch() 降级
    return Promise.reject(error)
  }
)

export default request
