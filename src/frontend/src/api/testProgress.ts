import request from '@/utils/request'

/**
 * 测试进度相关API
 */
export const testProgressApi = {
  /**
   * 检查测试通过状态
   */
  isTestPassed: (userId: number, vulnerabilityCode: string) => {
    return request({
      url: `/api/v1/test-progress/passed/${userId}/${vulnerabilityCode}`,
      method: 'get'
    })
  },

  /**
   * 获取测试完成条件
   */
  getTestCriteria: (vulnerabilityCode: string) => {
    return request({
      url: `/api/v1/test-progress/criteria/${vulnerabilityCode}`,
      method: 'get'
    })
  },

  /**
   * 记录测试完成
   */
  recordTestCompletion: (data: {
    userId: number
    vulnerabilityCode: string
    score: number
    accuracy: number
  }) => {
    return request({
      url: '/api/v1/test-progress/record',
      method: 'post',
      params: data
    })
  }
}
