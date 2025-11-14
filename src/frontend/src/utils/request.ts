import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type InternalAxiosRequestConfig
} from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { getToken } from '@/utils/auth'

// 创建axios实例
// 注意：baseURL不应该包含/api，因为API路径已经以/api开头
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
// 确保baseURL不以/api结尾，避免重复
const normalizedBaseURL = baseURL.replace(/\/api\/?$/, '')
const service: AxiosInstance = axios.create({
  baseURL: normalizedBaseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token
    const token = getToken()
    console.log('请求拦截器 - Token:', token ? '存在' : '不存在')
    if (token) {
      if (!config.headers) {
        config.headers = {}
      }
      const tokenType = localStorage.getItem('token_type') || 'Bearer'
      const normalizedType = tokenType.trim() || 'Bearer'
      config.headers.Authorization = `${normalizedType} ${token}`.trim()
      console.log('请求拦截器 - Authorization:', config.headers.Authorization)
    } else {
      console.warn('请求拦截器 - 没有找到token')
    }

    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response

    // 如果响应成功，直接返回数据
    if (data.success !== false && data.code === 200) {
      return data
    }

    // 处理业务错误
    if (data.code && data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }

    return data
  },
  error => {
    console.error('响应错误:', error)

    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          ElMessage.error('认证失败，请重新登录')
          // 清除本地存储的认证信息
          const authStore = useAuthStore()
          authStore.logout()
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export { service as request }
export default service
