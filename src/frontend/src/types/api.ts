/**
 * 统一API响应格式
 */
export interface ApiResult<T = any> {
  code: number
  message: string
  data?: T
  path?: string
  timestamp: number
  error?: boolean
  success?: boolean
}

/**
 * 兼容旧接口命名
 */
export type ApiResponse<T = any> = ApiResult<T>

/**
 * 分页响应格式
 */
export interface PageResult<T = any> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 攻击日志记录
 */
export interface AttackLogRecord {
  id: number
  userId?: number
  username?: string
  vulnerabilityId?: number
  module: string
  attackType: string
  attackPayload: string
  requestMethod: string
  requestUrl: string
  requestHeaders?: string
  requestBody?: string
  responseStatus?: number
  responseHeaders?: string
  responseBody?: string
  successful: boolean
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  sourceIp: string
  userAgent?: string
  executionTime?: number
  errorMessage?: string
  sessionId?: string
  traceId?: string
  createdAt: string
}

/**
 * 攻击日志统计
 */
export interface AttackLogModuleStat {
  module: string
  totalCount: number
  successCount: number
  failureCount: number
}

export interface AttackLogStats {
  totalCount: number
  successCount: number
  failureCount: number
  modules: AttackLogModuleStat[]
}

/**
 * 仪表盘概览
 */
export interface DashboardUserStats {
  completedVulnerabilities: number
  passedTests: number
  completedChallenges: number
  earnedBadges: number
  totalPoints: number
  totalStudyTime: number
  currentStreak: number
  longestStreak: number
}

export interface DashboardActivity {
  type: string
  title?: string
  icon: string
  timeAgo?: string
  timestamp?: string
  // 国际化支持字段
  activityKey?: string // 国际化key（用于默认活动）
  testName?: string // 测试名称（用于动态生成title）
  isPassed?: boolean // 是否通过（用于动态生成title）
}

export interface DashboardQuickLink {
  title: string
  description: string
  route: string
  icon: string
}

export interface DashboardHighlight {
  id: number
  title: string
  subtitle?: string
  categoryCode?: string
  difficultyLevel?: string
  estimatedTime?: number
  viewCount?: number
}

export interface DashboardOverview {
  userStats: DashboardUserStats
  recentActivities: DashboardActivity[]
  quickLinks: DashboardQuickLink[]
  highlights: DashboardHighlight[]
}

/**
 * 用户信息
 */
export interface User {
  id: number
  username: string
  email: string
  fullName?: string
  avatar?: string
  role: string
  status: string
  createdAt: string
  lastLoginAt?: string
}

/**
 * 用户统计信息
 */
export interface UserStats {
  totalUsers: number
  activeUsers: number
  todayRegistrations: number
  verifiedUsers: number
  lockedUsers: number
}

/**
 * 漏洞演示结果
 */
export interface VulnerabilityResult {
  success: boolean
  data?: any
  error?: string
  executionTime?: number
}

/**
 * 安全修复建议
 */
export interface SecurityAdvice {
  title: string
  description: string
  codeExample: string
  severity: 'high' | 'medium' | 'low'
}

/**
 * 用户登录表单
 */
export interface LoginForm {
  loginIdentifier: string
  password: string
  rememberMe?: boolean
  captchaId?: string
  captchaAnswer?: string
  nonce?: string
  timestamp?: string
}

/**
 * 用户注册表单
 */
export interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
  fullName?: string
  bio?: string
  agreeToTerms: boolean
  captchaId?: string
  captchaAnswer?: string
  nonce?: string
  timestamp?: string
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  email: string
  fullName?: string
  avatar?: string
  avatarUrl?: string
  bio?: string
  phone?: string
  userRole: string
  userRoleDescription: string
  userStatus: string
  isEmailVerified: boolean
  createdAt: string
  lastLoginAt?: string
  roles?: string[]
  permissions?: string[]
  profile?: {
    totalPoints: number
    totalStudyTime: number
    completedVulnerabilities: number
    passedTests: number
    completedChallenges: number
    earnedBadges: number
    currentStreak: number
    longestStreak: number
    skillLevel?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'
    learningGoals?: string
    professionalBackground?: string
    yearsOfExperience?: number
    country?: string
    city?: string
    timezone?: string
    preferredLanguage?: string
    emailNotifications?: boolean
    learningReminders?: boolean
  }
}

/**
 * 用户详细档案
 */
export type UserProfile = UserInfo

/**
 * 登录数据
 */
export type UserLoginData = LoginForm
