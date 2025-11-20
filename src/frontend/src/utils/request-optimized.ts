import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type InternalAxiosRequestConfig
} from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { getToken } from '@/utils/auth'

// 请求去重缓存 - 相同请求在pending时不重复发送
const pendingRequests = new Map<string, Promise<any>>()

// 响应缓存 - GET请求结果缓存
const responseCache = new Map<string, { data: any; timestamp: number }>()
const CACHE_DURATION = 5 * 60 * 1000 // 5分钟缓存

// 生成请求唯一标识
const generateRequestKey = (config: InternalAxiosRequestConfig): string => {
  const { method, url, params, data } = config
  return `${method?.toUpperCase()}_${url}_${JSON.stringify(params)}_${JSON.stringify(data)}`
}

// 创建axios实例
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const normalizedBaseURL = baseURL.replace(/\/api\/?$/, '')
const service: AxiosInstance = axios.create({
  baseURL: normalizedBaseURL,
  timeout: 15000, // 增加超时时间到15秒
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token
    const token = getToken()
    if (token) {
      if (!config.headers) {
        config.headers = {}
      }
      const tokenType = localStorage.getItem('token_type') || 'Bearer'
      const normalizedType = tokenType.trim() || 'Bearer'
      config.headers.Authorization = `${normalizedType} ${token}`.trim()
    }

    // GET请求检查缓存
    if (config.method?.toLowerCase() === 'get') {
      const cacheKey = generateRequestKey(config)
      const cached = responseCache.get(cacheKey)
      if (cached && Date.now() - cached.timestamp < CACHE_DURATION) {
        // 返回缓存的响应
        return Promise.reject({
          __CACHED__: true,
          data: cached.data
        }) as any
      }
    }

    // 请求去重 - 相同请求在pending时复用
    const requestKey = generateRequestKey(config)
    const pendingRequest = pendingRequests.get(requestKey)
    if (pendingRequest) {
      return pendingRequest as any
    }

    // 创建新请求
    const requestPromise = Promise.resolve(config)
    pendingRequests.set(requestKey, requestPromise)

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data, config } = response

    // 清除pending请求
    const requestKey = generateRequestKey(config as InternalAxiosRequestConfig)
    pendingRequests.delete(requestKey)

    // GET请求缓存响应
    if (config.method?.toLowerCase() === 'get' && data.code === 200) {
      responseCache.set(requestKey, {
        data,
        timestamp: Date.now()
      })
    }

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
    // 处理缓存响应
    if (error.__CACHED__) {
      return Promise.resolve(error.data)
    }

    // 清除pending请求
    if (error.config) {
      const requestKey = generateRequestKey(error.config)
      pendingRequests.delete(requestKey)
    }

    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          ElMessage.error('认证失败，请重新登录')
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

// 清除缓存工具函数
export const clearCache = (url?: string) => {
  if (url) {
    // 清除特定URL的缓存
    for (const [key] of responseCache) {
      if (key.includes(url)) {
        responseCache.delete(key)
      }
    }
  } else {
    // 清除所有缓存
    responseCache.clear()
  }
}

export { service as request }
export default service


