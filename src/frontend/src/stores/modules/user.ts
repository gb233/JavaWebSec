import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login, logout, getUserInfo, updateUserInfo } from '@/api/auth'
import {
  getCurrentUserProfile,
  updateCurrentUserProfile,
  changePassword as changeUserPassword,
  verifyEmail as verifyUserEmailApi,
  getUserStats
} from '@/api/user'
import { getToken, setToken as cacheToken, removeToken } from '@/utils/auth'
import type {
  UserProfile,
  UserLoginData,
  UserStats
} from '@/types/api'
import type {
  UserUpdateData,
  PasswordChangeData,
  EmailVerificationData
} from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserProfile | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])
  const avatar = ref<string>('')
  const name = ref<string>('')
  const email = ref<string>('')
  const phone = ref<string>('')
  const userStats = ref<UserStats | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)
  const loginTime = ref<number>(0)
  const lastActiveTime = ref<number>(0)
  const isUpdating = ref(false)
  const isStatsLoading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!userInfo.value)
  const userDisplayName = computed(() => {
    if (!userInfo.value) return '未知用户'
    return userInfo.value.fullName || userInfo.value.username
  })
  const userRole = computed(() => userInfo.value?.userRole || roles.value[0] || 'user')
  const isAdmin = computed(() => userRole.value.toLowerCase() === 'admin' || roles.value.includes('admin'))
  const isTeacher = computed(() => userRole.value.toLowerCase() === 'teacher' || roles.value.includes('teacher'))
  const isStudent = computed(() => userRole.value.toLowerCase() === 'student' || roles.value.includes('student'))

  // 方法
  const setAuthToken = (tokenValue: string, tokenType = 'Bearer') => {
    token.value = tokenValue
    if (tokenValue) {
      cacheToken(tokenValue)
      localStorage.setItem('token', tokenValue)
      localStorage.setItem('token_type', tokenType)
      isLoggedIn.value = true
    }
  }

  const setUserInfo = (info: UserProfile | null) => {
    userInfo.value = info

    if (info) {
      name.value = info.fullName || info.username || ''
      email.value = info.email || ''
      phone.value = info.phone || ''
      avatar.value = info.avatarUrl || info.avatar || ''
      roles.value = info.roles || []
      permissions.value = info.permissions || []
      localStorage.setItem('user-info', JSON.stringify(info))
      isLoggedIn.value = true
    } else {
      name.value = ''
      email.value = ''
      phone.value = ''
      avatar.value = ''
      roles.value = []
      permissions.value = []
      localStorage.removeItem('user-info')
      isLoggedIn.value = false
    }
  }

  const setRoles = (roleList: string[]) => {
    roles.value = roleList
  }

  const setPermissions = (permissionList: string[]) => {
    permissions.value = permissionList
  }

  const setAvatar = (avatarUrl: string) => {
    avatar.value = avatarUrl
    if (userInfo.value) {
      userInfo.value.avatar = avatarUrl
      userInfo.value.avatarUrl = avatarUrl
      localStorage.setItem('user-info', JSON.stringify(userInfo.value))
    }
  }

  const setName = (userName: string) => {
    name.value = userName
    if (userInfo.value) {
      userInfo.value.fullName = userName
      localStorage.setItem('user-info', JSON.stringify(userInfo.value))
    }
  }

  const setEmail = (userEmail: string) => {
    email.value = userEmail
    if (userInfo.value) {
      userInfo.value.email = userEmail
      localStorage.setItem('user-info', JSON.stringify(userInfo.value))
    }
  }

  const setPhone = (userPhone: string) => {
    phone.value = userPhone
    if (userInfo.value) {
      userInfo.value.phone = userPhone
      localStorage.setItem('user-info', JSON.stringify(userInfo.value))
    }
  }

  // 恢复本地缓存的用户信息
  const storedUser = localStorage.getItem('user-info')
  if (storedUser) {
    try {
      setUserInfo(JSON.parse(storedUser) as UserProfile)
    } catch (error) {
      console.warn('恢复本地用户信息失败，已清理缓存:', error)
      localStorage.removeItem('user-info')
    }
  }

  const storedStats = localStorage.getItem('user-stats')
  if (storedStats) {
    try {
      userStats.value = JSON.parse(storedStats) as UserStats
    } catch (error) {
      console.warn('恢复用户统计缓存失败，已清理缓存:', error)
      localStorage.removeItem('user-stats')
    }
  }

  // 登录
  const loginUser = async (loginForm: UserLoginData) => {
    try {
      const response = await login(loginForm)
      if (response.code === 200 && response.data) {
        const {
          accessToken,
          token: tokenValue,
          tokenType,
          expiresAt,
          refreshToken,
          user
        } = response.data

        const resolvedToken = accessToken || tokenValue
        if (!resolvedToken) {
          throw new Error('登录响应缺少访问令牌，请联系管理员检查接口返回值')
        }

        const resolvedTokenType = tokenType || 'Bearer'

        setAuthToken(resolvedToken, resolvedTokenType)
        if (typeof expiresAt === 'number') {
          localStorage.setItem('token_expires_at', String(expiresAt))
        } else {
          localStorage.removeItem('token_expires_at')
        }
        if (refreshToken) {
          localStorage.setItem('refresh_token', refreshToken)
        } else {
          localStorage.removeItem('refresh_token')
        }
        setUserInfo(user)
        loginTime.value = Date.now()
        lastActiveTime.value = Date.now()
        return response
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 登出
  const logoutUser = async () => {
    try {
      if (token.value) {
        await logout()
      }
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除本地状态
      token.value = ''
      userInfo.value = null
      roles.value = []
      permissions.value = []
      avatar.value = ''
      name.value = ''
      email.value = ''
      phone.value = ''
      isLoggedIn.value = false
      loginTime.value = 0
      lastActiveTime.value = 0
      userStats.value = null
      isUpdating.value = false
      isStatsLoading.value = false

      // 清除本地存储
      removeToken()
      localStorage.removeItem('token')
      localStorage.removeItem('token_type')
      localStorage.removeItem('token_expires_at')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('user-info')
      localStorage.removeItem('user-roles')
      localStorage.removeItem('user-permissions')
      localStorage.removeItem('user-stats')
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      if (!token.value) {
        throw new Error('未登录')
      }

      const response = await getUserInfo()
      if (response.code === 200 && response.data) {
        setUserInfo(response.data as UserProfile)
        lastActiveTime.value = Date.now()
        return response.data
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，清除登录状态
      await logoutUser()
      throw error
    }
  }

  // 更新用户信息
  const updateUser = async (updateData: Partial<UserProfile>) => {
    try {
      const response = await updateUserInfo(updateData)
      if (response.code === 200 && response.data) {
        setUserInfo(response.data as UserProfile)
        return response.data
      } else {
        throw new Error(response.message || '更新用户信息失败')
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  const fetchCurrentUserProfile = async () => {
    try {
      isUpdating.value = true
      const response = await getCurrentUserProfile()
      if (response.code === 200 && response.data) {
        setUserInfo(response.data as UserProfile)
        
        // 同时更新authStore的用户信息，保持数据同步
        const { useAuthStore } = await import('./auth')
        const authStore = useAuthStore()
        authStore.updateUser(response.data as UserProfile)
        
        return response.data
      }
      throw new Error(response.message || '获取个人信息失败')
    } catch (error) {
      console.error('获取个人档案失败:', error)
      throw error
    } finally {
      isUpdating.value = false
    }
  }

  const updateUserProfile = async (updateData: Partial<UserUpdateData>) => {
    try {
      isUpdating.value = true
      const response = await updateCurrentUserProfile(updateData as UserUpdateData)
      if (response.code === 200 && response.data) {
        setUserInfo(response.data as UserProfile)
        
        // 同时更新authStore的用户信息
        const { useAuthStore } = await import('./auth')
        const authStore = useAuthStore()
        authStore.updateUser(response.data as UserProfile)
        
        return true
      }
      // 显示具体的错误信息
      ElMessage.error(response.message || '更新个人信息失败')
      return false
    } catch (error: any) {
      console.error('更新个人信息失败:', error)
      // 显示错误信息给用户
      ElMessage.error(error.message || '更新个人信息失败')
      return false
    } finally {
      isUpdating.value = false
    }
  }

  const verifyUserEmail = async (verificationCode: string) => {
    try {
      isUpdating.value = true
      const payload: EmailVerificationData = { verificationCode }
      const response = await verifyUserEmailApi(payload)
      if (response.code === 200) {
        await fetchCurrentUserProfile()
        return true
      }
      throw new Error(response.message || '邮箱验证失败')
    } catch (error) {
      console.error('邮箱验证失败:', error)
      return false
    } finally {
      isUpdating.value = false
    }
  }

  const updatePassword = async (passwordData: PasswordChangeData) => {
    try {
      isUpdating.value = true
      const response = await changeUserPassword(passwordData)
      if (response.code === 200) {
        ElMessage.success('密码修改成功')
        return true
      }
      ElMessage.error(response.message || '修改密码失败')
      return false
    } catch (error: any) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改密码失败')
      return false
    } finally {
      isUpdating.value = false
    }
  }

  const fetchUserStats = async () => {
    try {
      isStatsLoading.value = true
      const response = await getUserStats()
      if (response.code === 200 && response.data) {
        userStats.value = response.data
        localStorage.setItem('user-stats', JSON.stringify(response.data))
        return response.data
      }
      throw new Error(response.message || '获取用户统计失败')
    } catch (error) {
      console.error('获取用户统计失败:', error)
      throw error
    } finally {
      isStatsLoading.value = false
    }
  }

  // 检查登录状态
  const checkLoginStatus = async () => {
    try {
      if (token.value) {
        await fetchUserInfo()
      }
    } catch (error) {
      console.error('检查登录状态失败:', error)
      // 清除无效的登录状态
      await logoutUser()
    }
  }

  // 检查权限
  const hasPermission = (permission: string) => {
    return permissions.value.includes(permission)
  }

  const hasRole = (role: string) => {
    return roles.value.includes(role)
  }

  const hasAnyRole = (roleList: string[]) => {
    return roleList.some(role => roles.value.includes(role))
  }

  const hasAllRoles = (roleList: string[]) => {
    return roleList.every(role => roles.value.includes(role))
  }

  // 更新活跃时间
  const updateActiveTime = () => {
    lastActiveTime.value = Date.now()
  }

  // 检查会话是否过期
  const isSessionExpired = () => {
    if (!loginTime.value) return true

    const now = Date.now()
    const sessionTimeout = 24 * 60 * 60 * 1000 // 24小时

    return (now - loginTime.value) > sessionTimeout
  }

  // 检查是否长时间未活跃
  const isInactive = () => {
    if (!lastActiveTime.value) return true

    const now = Date.now()
    const inactiveTimeout = 30 * 60 * 1000 // 30分钟

    return (now - lastActiveTime.value) > inactiveTimeout
  }

  // 重置用户状态
  const resetUserState = () => {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    avatar.value = ''
    name.value = ''
    email.value = ''
    phone.value = ''
    isLoggedIn.value = false
    loginTime.value = 0
    lastActiveTime.value = 0
    userStats.value = null
    isUpdating.value = false
    isStatsLoading.value = false
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    permissions,
    avatar,
    name,
    email,
    phone,
    isLoggedIn,
    loginTime,
    lastActiveTime,
    userStats,
    isUpdating,
    isStatsLoading,

    // 计算属性
    isAuthenticated,
    userDisplayName,
    userRole,
    isAdmin,
    isTeacher,
    isStudent,

    // 方法
    setToken: setAuthToken,
    setUserInfo,
    setRoles,
    setPermissions,
    setAvatar,
    setName,
    setEmail,
    setPhone,
    loginUser,
    logoutUser,
    fetchUserInfo,
    updateUser,
    fetchCurrentUserProfile,
    updateUserProfile,
    checkLoginStatus,
    hasPermission,
    hasRole,
    hasAnyRole,
    hasAllRoles,
    updateActiveTime,
    isSessionExpired,
    isInactive,
    resetUserState,
    verifyUserEmail,
    updatePassword,
    fetchUserStats
  }
}, {
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'userInfo', 'roles', 'permissions', 'avatar', 'name', 'email', 'phone', 'isLoggedIn', 'loginTime', 'lastActiveTime', 'userStats']
  }
})
