import request from '@/utils/request'

/**
 * 用户活动相关API
 */
export const userActivityApi = {
  /**
   * 获取最近活动
   */
  getRecentActivities: (userId: number, limit: number = 10) => {
    return request({
      url: `/api/v1/user-activities/recent/${userId}`,
      method: 'get',
      params: { limit }
    })
  },

  /**
   * 获取活动统计
   */
  getActivityStatistics: (userId: number) => {
    return request({
      url: `/api/v1/user-activities/statistics/${userId}`,
      method: 'get'
    })
  },

  /**
   * 记录学习完成活动
   */
  recordLearningCompleted: (data: {
    userId: number
    vulnerabilityCode: string
    studyTime: number
    score: number
  }) => {
    return request({
      url: '/api/v1/user-activities/learning-completed',
      method: 'post',
      params: data
    })
  },

  /**
   * 记录测试通过活动
   */
  recordTestPassed: (data: {
    userId: number
    vulnerabilityCode: string
    score: number
    accuracy: number
  }) => {
    return request({
      url: '/api/v1/user-activities/test-passed',
      method: 'post',
      params: data
    })
  },

  /**
   * 记录挑战完成活动
   */
  recordChallengeCompleted: (data: {
    userId: number
    vulnerabilityCode: string
    score: number
    badge?: string
  }) => {
    return request({
      url: '/api/v1/user-activities/challenge-completed',
      method: 'post',
      params: data
    })
  }
}
