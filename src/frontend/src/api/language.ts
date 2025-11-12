import request from '@/utils/request'

/**
 * 语言偏好接口
 */
export interface LanguagePreference {
  id?: number
  userId?: number
  languageCode: string
  languageName: string
  displayName: string
  isActive?: boolean
}

/**
 * 语言API
 */
export const languageApi = {
  /**
   * 获取当前用户语言偏好
   */
  getCurrentLanguage: (): Promise<ApiResponse<LanguagePreference>> => {
    return request({
      url: '/api/v1/language/current',
      method: 'get'
    })
  },

  /**
   * 设置用户语言偏好
   */
  setLanguage: (languageCode: string): Promise<ApiResponse<LanguagePreference>> => {
    return request({
      url: '/api/v1/language/set',
      method: 'post',
      data: { languageCode }
    })
  },

  /**
   * 获取支持的语言列表
   */
  getSupportedLanguages: (): Promise<ApiResponse<LanguagePreference[]>> => {
    return request({
      url: '/api/v1/language/supported',
      method: 'get'
    })
  },

  /**
   * 获取默认语言
   */
  getDefaultLanguage: (): Promise<ApiResponse<string>> => {
    return request({
      url: '/api/v1/language/default',
      method: 'get'
    })
  }
}
