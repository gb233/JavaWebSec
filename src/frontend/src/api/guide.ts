import request from '@/utils/request'

/**
 * 指引步骤接口
 */
export interface GuideStep {
  id: number
  stepKey: string
  title: string
  description: string
  targetElement: string
  position: string
  orderIndex: number
  isActive: boolean
  guideVersion: string
}

/**
 * 用户指引偏好接口
 */
export interface UserGuidePreference {
  id: number
  userId: number
  hasCompletedInitialGuide: boolean
  guideVersion: string
  lastGuideShownAt?: string
  autoShowGuide: boolean
}

/**
 * 指引API
 */
export const guideApi = {
  /**
   * 获取用户指引偏好
   */
  getUserGuidePreference: (): Promise<ApiResponse<UserGuidePreference>> => {
    return request({
      url: '/api/v1/guide/preference',
      method: 'get'
    })
  },

  /**
   * 检查是否需要显示指引
   */
  shouldShowGuide: (): Promise<ApiResponse<boolean>> => {
    return request({
      url: '/api/v1/guide/should-show',
      method: 'get'
    })
  },

  /**
   * 获取指引步骤
   */
  getGuideSteps: (): Promise<ApiResponse<GuideStep[]>> => {
    return request({
      url: '/api/v1/guide/steps',
      method: 'get'
    })
  },

  /**
   * 标记指引完成
   */
  markGuideCompleted: (): Promise<ApiResponse<void>> => {
    return request({
      url: '/api/v1/guide/complete',
      method: 'post'
    })
  },

  /**
   * 更新指引显示时间
   */
  updateGuideShownTime: (): Promise<ApiResponse<void>> => {
    return request({
      url: '/api/v1/guide/update-shown-time',
      method: 'post'
    })
  },

  /**
   * 设置自动显示指引
   */
  setAutoShowGuide: (autoShow: boolean): Promise<ApiResponse<void>> => {
    return request({
      url: '/api/v1/guide/auto-show',
      method: 'post',
      params: { autoShow }
    })
  },

  /**
   * 重置用户指引
   */
  resetUserGuide: (): Promise<ApiResponse<void>> => {
    return request({
      url: '/api/v1/guide/reset',
      method: 'post'
    })
  }
}
