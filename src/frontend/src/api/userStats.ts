import request from '@/utils/request'

/**
 * 用户统计相关API
 */
export const userStatsApi = {
  /**
   * 更新漏洞学习统计
   */
  updateVulnerabilityStats: (data: {
    userId: number
    vulnerabilityCode: string
    studyTime: number
    points: number
  }) => {
    return request({
      url: '/api/v1/user-stats/vulnerability',
      method: 'post',
      params: data
    })
  },

  /**
   * 更新测试统计
   */
  updateTestStats: (data: {
    userId: number
    testId: number
    passed: boolean
    score: number
    points: number
  }) => {
    return request({
      url: '/api/v1/user-stats/test',
      method: 'post',
      params: data
    })
  },

  /**
   * 更新挑战统计
   */
  updateChallengeStats: (data: {
    userId: number
    challengeId: number
    completed: boolean
    points: number
    badge?: string
  }) => {
    return request({
      url: '/api/v1/user-stats/challenge',
      method: 'post',
      params: data
    })
  },

  /**
   * 更新学习时长
   */
  updateStudyTimeStats: (data: {
    userId: number
    additionalTime: number
  }) => {
    return request({
      url: '/api/v1/user-stats/study-time',
      method: 'post',
      params: data
    })
  },

  /**
   * 更新连续学习天数
   */
  updateStreakStats: (data: {
    userId: number
  }) => {
    return request({
      url: '/api/v1/user-stats/streak',
      method: 'post',
      params: data
    })
  }
}
