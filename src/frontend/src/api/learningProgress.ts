import request from '@/utils/request'

/**
 * 学习进度相关API
 */
export const learningProgressApi = {
  /**
   * 检查学习完成状态
   */
  isLearningCompleted: (userId: number, vulnerabilityCode: string) => {
    return request({
      url: `/api/v1/learning-progress/completed/${userId}/${vulnerabilityCode}`,
      method: 'get'
    })
  },

  /**
   * 记录页面访问
   */
  recordPageVisit: (data: {
    userId: number
    vulnerabilityCode: string
    pageType: string
    duration: number
  }) => {
    return request({
      url: '/api/v1/learning-progress/page-visit',
      method: 'post',
      params: data
    })
  },

  /**
   * 记录用户交互
   */
  recordUserInteraction: (data: {
    userId: number
    vulnerabilityCode: string
    interactionType: string
    interactionData: Record<string, any>
  }) => {
    return request({
      url: '/api/v1/learning-progress/interaction',
      method: 'post',
      params: {
        userId: data.userId,
        vulnerabilityCode: data.vulnerabilityCode,
        interactionType: data.interactionType
      },
      data: data.interactionData
    })
  },

  /**
   * 记录演示执行
   */
  recordDemoExecution: (data: {
    userId: number
    vulnerabilityCode: string
    demoType: string
    executionData: Record<string, any>
  }) => {
    return request({
      url: '/api/v1/learning-progress/demo-execution',
      method: 'post',
      params: {
        userId: data.userId,
        vulnerabilityCode: data.vulnerabilityCode,
        demoType: data.demoType
      },
      data: data.executionData
    })
  }
}
