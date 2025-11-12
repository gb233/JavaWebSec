import request from '@/utils/request'

/**
 * 挑战进度相关API
 */
export const challengeProgressApi = {
  /**
   * 检查挑战完成状态
   */
  isChallengeCompleted: (userId: number, vulnerabilityCode: string) => {
    return request({
      url: `/api/v1/challenge-progress/completed/${userId}/${vulnerabilityCode}`,
      method: 'get'
    })
  },

  /**
   * 获取挑战完成条件
   */
  getChallengeCriteria: (vulnerabilityCode: string) => {
    return request({
      url: `/api/v1/challenge-progress/criteria/${vulnerabilityCode}`,
      method: 'get'
    })
  },

  /**
   * 记录挑战完成
   */
  recordChallengeCompletion: (data: {
    userId: number
    vulnerabilityCode: string
    score: number
    badge?: string
  }) => {
    return request({
      url: '/api/v1/challenge-progress/record',
      method: 'post',
      params: data
    })
  }
}
