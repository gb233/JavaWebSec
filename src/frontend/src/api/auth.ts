import request from '@/utils/request'
import type {
  LoginForm,
  RegisterForm,
  UserInfo,
  UserProfile,
  ApiResult,
  UserLoginData
} from '@/types/api'

export interface UserRegistrationData {
  username: string
  email: string
  password: string
  confirmPassword: string
  fullName?: string
}

export interface LoginResponse {
  accessToken?: string
  token?: string
  tokenType?: string
  expiresAt?: number
  refreshToken?: string | null
  user: UserInfo
  isFirstLogin?: boolean
}

export type { UserInfo, UserProfile, UserLoginData }

// 用户登录
export function login(data: LoginForm): Promise<ApiResult<LoginResponse>> {
  return request({
    url: '/api/v1/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data: RegisterForm): Promise<ApiResult<UserProfile>> {
  return request({
    url: '/api/v1/auth/register',
    method: 'post',
    data
  })
}

// 用户登出
export function logout(): Promise<ApiResult<null>> {
  return request({
    url: '/api/v1/auth/logout',
    method: 'post'
  })
}

// 获取用户信息
export function getUserInfo(): Promise<ApiResult<UserProfile>> {
  return request({
    url: '/api/v1/users/profile',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data: Partial<UserInfo>): Promise<ApiResult<UserInfo>> {
  return request({
    url: '/api/v1/users/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data: { oldPassword: string; newPassword: string }): Promise<ApiResult<null>> {
  return request({
    url: '/api/v1/users/change-password',
    method: 'post',
    data
  })
}

// 刷新token
export function refreshToken(): Promise<ApiResult<{ token: string }>> {
  return request({
    url: '/api/v1/auth/refresh',
    method: 'post'
  })
}

// 验证邮箱
export function verifyEmail(data: { email: string; code: string }): Promise<ApiResult<null>> {
  return request({
    url: '/api/v1/auth/verify-email',
    method: 'post',
    data
  })
}

// 发送验证码
export function sendVerificationCode(email: string): Promise<ApiResult<null>> {
  return request({
    url: '/api/v1/auth/send-code',
    method: 'post',
    data: { email }
  })
}

// 检查用户名可用性
export function checkUsernameAvailability(username: string): Promise<ApiResult<{ available: boolean }>> {
  return request({
    url: '/api/v1/auth/check-username',
    method: 'get',
    params: { username }
  })
}

// 检查邮箱可用性
export function checkEmailAvailability(email: string): Promise<ApiResult<{ available: boolean }>> {
  return request({
    url: '/api/v1/auth/check-email',
    method: 'get',
    params: { email }
  })
}

// 获取验证码
export function getCaptcha(): Promise<ApiResult<{ captchaId: string; captchaQuestion: string; expiryTime?: string; enabled?: string }>> {
  return request({
    url: '/api/v1/auth/captcha',
    method: 'get'
  })
}

// 获取防重放攻击nonce token
export function getNonce(): Promise<ApiResult<{ nonce: string; timestamp: string; expiryTime?: string; enabled?: string }>> {
  return request({
    url: '/api/v1/auth/nonce',
    method: 'get'
  })
}

// 获取服务器时间（用于时间同步）
export function getServerTime(): Promise<ApiResult<{ serverTime: number; timestamp: number; timezone: string }>> {
  return request({
    url: '/api/v1/auth/server-time',
    method: 'get'
  })
}
