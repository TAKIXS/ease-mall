import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例（所有请求都从这里发）
const request = axios.create({
  baseURL: '/api',        // 所有请求自动加上 /api 前缀 → 被 Vite 代理转发到后端
  timeout: 10000          // 10秒超时
})

// ★ 请求拦截器：每次请求自动带上 Token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ★ 响应拦截器：统一处理错误
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data  // 直接返回 { code, message, data }
  },
  error => {
    ElMessage.error('网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
