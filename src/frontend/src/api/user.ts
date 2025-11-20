import request from '@/utils/request'
import type { ApiResult, UserInfo, PageResult } from '@/types/api'

// ================================
// 用户管理相关接口类型定义
// ================================

export interface UserUpdateData {
  email?: string
  fullName?: string
  bio?: string
  avatarUrl?: string
  // 用户配置文件字段
  skillLevel?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'
  learningGoals?: string
  professionalBackground?: string
  yearsOfExperience?: number
  birthDate?: string
  gender?: 'MALE' | 'FEMALE' | 'OTHER'
  country?: string
  city?: string
  timezone?: string
  preferredLanguage?: string
  emailNotifications?: boolean
  learningReminders?: boolean
}

export interface PasswordChangeData {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export interface EmailVerificationData {
  verificationCode: string
}

export interface UserSearchParams {
  keyword?: string
  username?: string
  email?: string
  userRole?: string
  userStatus?: string
  page?: number
  size?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

export interface UserStats {
  totalUsers: number
  activeUsers: number
  todayRegistrations: number
  verifiedUsers: number
  lockedUsers: number
}

export interface RankingUser extends UserInfo {
  rank?: number
}

// ================================
// 用户管理API函数
// ================================

/**
 * 获取当前用户个人信息
 */
export const getCurrentUserProfile = async (): Promise<ApiResult<UserInfo>> =>
  request({
    url: '/api/v1/users/profile',
    method: 'get'
  })

/**
 * 更新当前用户个人信息
 */
export const updateCurrentUserProfile = async (
  userData: UserUpdateData
): Promise<ApiResult<UserInfo>> =>
  request({
    url: '/api/v1/users/profile',
    method: 'put',
    data: userData
  })

/**
 * 修改当前用户密码
 */
export const changePassword = async (
  passwordData: PasswordChangeData
): Promise<ApiResult<string>> =>
  request({
    url: '/api/v1/users/password',
    method: 'put',
    data: passwordData
  })

/**
 * 验证邮箱
 */
export const verifyEmail = async (
  verificationData: EmailVerificationData
): Promise<ApiResult<string>> =>
  request({
    url: '/api/v1/users/verify-email',
    method: 'post',
    data: verificationData
  })

/**
 * 根据ID获取用户公开信息
 */
export const getUserById = async (userId: number): Promise<ApiResult<UserInfo>> =>
  request({
    url: `/api/v1/users/${userId}`,
    method: 'get'
  })

/**
 * 分页查询用户列表
 */
export const getUsers = async (
  params: UserSearchParams = {}
): Promise<ApiResult<PageResult<UserInfo>>> => {
  const queryParams = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      queryParams.append(key, value.toString())
    }
  })

  const queryString = queryParams.toString()
  const url = queryString ? `/api/v1/users?${queryString}` : '/api/v1/users'

  return request({
    url,
    method: 'get'
  })
}

/**
 * 搜索用户
 */
export const searchUsers = async (
  keyword: string,
  page = 0,
  size = 20
): Promise<ApiResult<PageResult<UserInfo>>> =>
  request({
    url: '/api/v1/users/search',
    method: 'get',
    params: {
      keyword,
      page,
      size
    }
  })

/**
 * 获取用户排行榜
 */
export const getUserRanking = async (
  type: 'points' | 'studytime' | 'vulnerabilities' | 'streak' = 'points',
  limit = 10
): Promise<ApiResult<RankingUser[]>> =>
  request({
    url: '/api/v1/users/ranking',
    method: 'get',
    params: { type, limit }
  })

/**
 * 获取用户统计信息
 */
export const getUserStats = async (): Promise<ApiResult<UserStats>> =>
  request({
    url: '/api/v1/users/stats',
    method: 'get'
  })

// ================================
// 头像上传相关函数
// ================================

/**
 * 上传用户头像
 */
export const uploadAvatar = async (file: File): Promise<ApiResult<{ url: string }>> => {
  const formData = new FormData()
  formData.append('avatar', file)

  return request({
    url: '/api/v1/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ================================
// 辅助函数
// ================================

/**
 * 格式化学习时间（分钟转换为易读格式）
 */
export const formatStudyTime = (minutes: number): string => {
  if (minutes < 60) {
    return `${minutes} 分钟`
  } else if (minutes < 1440) {
    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60
    return remainingMinutes > 0 ? `${hours} 小时 ${remainingMinutes} 分钟` : `${hours} 小时`
  } else {
    const days = Math.floor(minutes / 1440)
    const remainingHours = Math.floor((minutes % 1440) / 60)
    return remainingHours > 0 ? `${days} 天 ${remainingHours} 小时` : `${days} 天`
  }
}

/**
 * 格式化用户角色
 */
export const formatUserRole = (role: string): string => {
  const roleMap: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生'
  }
  return roleMap[role] || role
}

/**
 * 格式化用户状态
 */
export const formatUserStatus = (status: string): string => {
  const statusMap: Record<string, string> = {
    ACTIVE: '正常',
    INACTIVE: '未激活',
    SUSPENDED: '已暂停',
    BANNED: '已封禁'
  }
  return statusMap[status] || status
}

/**
 * 格式化技能水平
 */
export const formatSkillLevel = (level: string): string => {
  const levelMap: Record<string, string> = {
    BEGINNER: '初学者',
    INTERMEDIATE: '中级',
    ADVANCED: '高级',
    EXPERT: '专家'
  }
  return levelMap[level] || level
}

/**
 * 获取技能水平颜色
 */
type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

export const getSkillLevelColor = (level: string): TagType => {
  const colorMap: Record<string, TagType> = {
    BEGINNER: 'success',
    INTERMEDIATE: 'warning',
    ADVANCED: 'danger',
    EXPERT: 'primary'
  }
  return colorMap[level] || 'info'
}

/**
 * 获取用户状态颜色
 */
export const getUserStatusColor = (status: string): TagType => {
  const colorMap: Record<string, TagType> = {
    ACTIVE: 'success',
    INACTIVE: 'warning',
    SUSPENDED: 'danger',
    BANNED: 'danger'
  }
  return colorMap[status] || 'info'
}

/**
 * 计算用户等级（根据积分）
 */
export const calculateUserLevel = (points: number): { level: number; progress: number; nextLevelPoints: number } => {
  const levelThresholds = [0, 100, 300, 600, 1000, 1500, 2200, 3000, 4000, 5500, 7500, 10000]

  let level = 1
  for (let i = levelThresholds.length - 1; i >= 0; i--) {
    if (points >= levelThresholds[i]) {
      level = i + 1
      break
    }
  }

  const currentLevelPoints = levelThresholds[level - 1] || 0
  const nextLevelPoints = levelThresholds[level] || levelThresholds[levelThresholds.length - 1]
  const progress = nextLevelPoints > currentLevelPoints
    ? Math.round(((points - currentLevelPoints) / (nextLevelPoints - currentLevelPoints)) * 100)
    : 100

  return {
    level,
    progress,
    nextLevelPoints
  }
}
