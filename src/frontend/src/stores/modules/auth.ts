import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserProfile, LoginResponse, UserLoginData } from '@/api/auth'
import type { RegisterForm } from '@/types/api'
import {
  login,
  register,
  getUserInfo,
  logout as logoutApi
} from '@/api/auth'
import { ElMessage } from 'element-plus'
import {
  getToken as getCachedToken,
  setToken as cacheToken,
  removeToken as clearCachedToken,
  setToken,
  setRefreshToken,
  removeRefreshToken
} from '@/utils/auth'

export const useAuthStore = defineStore('auth', () => {
  // ================================
  // 状态定义
  // ================================

  const user = ref<UserProfile | null>(null)
  const isLoggedIn = ref(false)
  const isLoading = ref(false)
  const loginError = ref('')
  const registerError = ref('')

  // ================================
  // 计算属性
  // ================================

  const userDisplayName = computed(() => {
    if (!user.value) return ''
    return user.value.fullName || user.value.username || '未知用户'
  })

  const userAvatar = computed(() => {
    return user.value?.avatarUrl || user.value?.avatar || '/default-avatar.png'
  })

  const userRole = computed(() => {
    return user.value?.userRole || 'STUDENT'
  })

  const userRoleDisplay = computed(() => {
    return user.value?.userRoleDescription || '学生'
  })

  const isEmailVerified = computed(() => {
    return user.value?.isEmailVerified || false
  })

  const userStats = computed(() => {
    const profile = user.value?.profile
    if (!profile) return null

    return {
      totalPoints: profile.totalPoints,
      totalStudyTime: profile.totalStudyTime,
      completedVulnerabilities: profile.completedVulnerabilities,
      passedTests: profile.passedTests,
      completedChallenges: profile.completedChallenges,
      earnedBadges: profile.earnedBadges,
      currentStreak: profile.currentStreak,
      longestStreak: profile.longestStreak
    }
  })

  // ================================
  // 认证操作
  // ================================

  /**
   * 检查是否为新用户（基于用户创建时间）
   */
  const checkIfNewUser = (userData: UserProfile): boolean => {
    try {
      // 检查用户是否有创建时间信息
      if (userData.createdAt) {
        const createdAt = new Date(userData.createdAt)
        const now = new Date()
        const hoursDiff = (now.getTime() - createdAt.getTime()) / (1000 * 60 * 60)
        
        // 如果用户创建时间在24小时内，认为是新用户
        return hoursDiff <= 24
      }
      
      // 如果没有创建时间信息，检查是否是新注册的用户
      // 可以通过其他方式判断，比如检查用户是否完成了初始设置等
      return false
    } catch (error) {
      console.error('检查新用户状态失败:', error)
      return false
    }
  }

  /**
   * 初始化认证状态
   */
  const initializeAuth = async () => {
    try {
      isLoading.value = true

      // 检查本地存储的认证信息
      const token = localStorage.getItem('token') || getCachedToken()
      if (token) {
        try {
          // 暂时注释API调用，避免错误
          // const response = await getUserInfo()
          // if (response.code === 200) {
          //   user.value = response.data
          //   isLoggedIn.value = true
          // }

          // 临时调试：从本地存储恢复用户信息
          const userInfo = localStorage.getItem('user_info')
          if (userInfo) {
            try {
              user.value = JSON.parse(userInfo) as UserProfile
              isLoggedIn.value = true
              console.log('从本地存储恢复用户信息:', user.value)
            } catch (error) {
              console.warn('本地用户信息解析失败，已清理缓存', error)
              localStorage.removeItem('user_info')
            }
          }
        } catch (error) {
          console.error('恢复用户信息失败:', error)
          // 令牌无效，清除认证信息
          await performLogout()
        }
      } else {
        await clearAuthState()
      }
    } catch (error) {
      console.error('初始化认证状态失败:', error)
      await clearAuthState()
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 用户登录
   */
  const loginUser = async (loginData: UserLoginData): Promise<boolean> => {
    try {
      isLoading.value = true
      loginError.value = ''

      const response = await login(loginData)
      if (response.code === 200 && response.data) {
        const { accessToken, tokenType, expiresAt, refreshToken, user: userData } = response.data

        const token = accessToken
        if (!token) {
          throw new Error('登录响应缺少访问令牌，请联系管理员检查接口返回值')
        }

        // 保存认证信息 - 根据rememberMe设置token存储方式
        cacheToken(token)
        setToken(token, loginData.rememberMe || false)
        localStorage.setItem('token_type', tokenType || 'Bearer')
        if (typeof expiresAt === 'number') {
          localStorage.setItem('token_expires_at', String(expiresAt))
        } else {
          localStorage.removeItem('token_expires_at')
        }
        if (refreshToken) {
          setRefreshToken(refreshToken)
          localStorage.setItem('refresh_token', refreshToken)
        } else {
          removeRefreshToken()
          localStorage.removeItem('refresh_token')
        }
        localStorage.setItem('user_info', JSON.stringify(userData))

        user.value = userData
        isLoggedIn.value = true

        // 注意：不再使用前端判断来设置首次登录标记
        // 新手指引的显示完全由后端API /api/v1/guide/should-show 控制
        // 该API会检查数据库中的 hasCompletedInitialGuide 字段

        ElMessage.success('登录成功')
        return true
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error: any) {
      console.error('登录失败:', error)

      const errorMessage = error.response?.data?.message || '登录失败，请检查用户名和密码'
      loginError.value = errorMessage
      ElMessage.error(errorMessage)

      return false
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 用户注册
   */
  const registerUser = async (registrationData: RegisterForm): Promise<boolean> => {
    try {
      isLoading.value = true
      registerError.value = ''

      const response = await register(registrationData)
      if (response.code === 200) {
        ElMessage.success('注册成功！请登录您的账户。')
        return true
      } else {
        throw new Error(response.message || '注册失败')
      }
    } catch (error: any) {
      console.error('注册失败:', error)

      const errorMessage = error.response?.data?.message || '注册失败，请稍后重试'
      registerError.value = errorMessage
      ElMessage.error(errorMessage)

      return false
    } finally {
      isLoading.value = false
    }
  }

  const clearAuthState = async () => {
    user.value = null
    isLoggedIn.value = false
    loginError.value = ''
    registerError.value = ''

    clearCachedToken()
    removeRefreshToken()
    localStorage.removeItem('token')
    localStorage.removeItem('token_type')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('token_expires_at')
    localStorage.removeItem('user_info')
  }

  const performLogout = async () => {
    try {
      if (localStorage.getItem('token')) {
        await logoutApi()
      }
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      await clearAuthState()
      // 跳转到登录页面
      window.location.href = '/login'
    }
  }

  /**
   * 更新用户信息
   */
  const updateUser = (updatedUser: UserProfile) => {
    user.value = updatedUser
    // 同步更新本地存储
    localStorage.setItem('user_info', JSON.stringify(updatedUser))
  }

  /**
   * 刷新用户信息
   */
  const refreshUserInfo = async () => {
    try {
      // 这里可以调用获取当前用户信息的API
      // const response = await getCurrentUserProfile()
      // updateUser(response.data.data)
    } catch (error) {
      console.error('刷新用户信息失败:', error)
    }
  }

  // ================================
  // 权限检查
  // ================================

  /**
   * 检查用户是否有指定权限
   */
  const hasPermission = (permission: string): boolean => {
    if (!isLoggedIn.value || !user.value) return false

    // 在统一权限模式下，所有注册用户都有相同的权限
    const userPermissions = [
      'PERM_LEARN_VULNERABILITIES',
      'PERM_TAKE_TESTS',
      'PERM_COMPLETE_CHALLENGES',
      'PERM_VIEW_PROGRESS',
      'PERM_MANAGE_PROFILE'
    ]

    return userPermissions.includes(permission)
  }

  /**
   * 检查用户是否有指定角色
   */
  const hasRole = (role: string): boolean => {
    if (!isLoggedIn.value || !user.value) return false
    return user.value.userRole === role
  }

  /**
   * 检查是否为管理员
   */
  const isAdmin = computed(() => hasRole('ADMIN'))

  /**
   * 检查是否为教师
   */
  const isTeacher = computed(() => hasRole('TEACHER'))

  /**
   * 检查是否为学生
   */
  const isStudent = computed(() => hasRole('STUDENT'))

  // ================================
  // 错误处理
  // ================================

  /**
   * 清除错误信息
   */
  const clearErrors = () => {
    loginError.value = ''
    registerError.value = ''
  }

  // ================================
  // 返回store接口
  // ================================

  return {
    // 状态
    user,
    isLoggedIn,
    isLoading,
    loginError,
    registerError,

    // 计算属性
    userDisplayName,
    userAvatar,
    userRole,
    userRoleDisplay,
    isEmailVerified,
    userStats,
    isAdmin,
    isTeacher,
    isStudent,

    // 方法
    initializeAuth,
    loginUser,
    registerUser,
    logoutUser: performLogout,
    logout: performLogout,
    updateUser,
    refreshUserInfo,
    hasPermission,
    hasRole,
    clearErrors
  }
})

export default useAuthStore
