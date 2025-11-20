import request from '@/utils/request'

/**
 * 挑战场景API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const challengeApi = {
  /**
   * 获取挑战场景列表
   * @param difficultyLevel 难度等级筛选
   */
  getScenarios: (difficultyLevel?: string) => {
    return request({
      url: '/api/v1/challenge-scenarios',
      method: 'get',
      params: { difficultyLevel }
    })
  },

  /**
   * 获取挑战场景详情
   * @param id 场景ID
   */
  getScenario: (id: number) => {
    return request({
      url: `/api/v1/challenge-scenarios/${id}`,
      method: 'get'
    })
  },

  /**
   * 开始挑战
   * @param id 场景ID
   */
  startChallenge: (id: number) => {
    return request({
      url: `/api/v1/challenge-scenarios/${id}/start`,
      method: 'post'
    })
  },

  /**
   * 执行挑战步骤
   * @param id 场景ID
   * @param step 步骤名称
   * @param params 步骤参数
   */
  executeStep: (id: number, step: string, params: Record<string, unknown>) => {
    return request({
      url: `/api/v1/challenge-scenarios/${id}/execute`,
      method: 'post',
      data: { step, params }
    })
  },

  /**
   * 获取挑战进度
   * @param id 场景ID
   */
  getProgress: (id: number) => {
    return request({
      url: `/api/v1/challenge-scenarios/${id}/progress`,
      method: 'get'
    })
  },

  /**
   * 重置挑战
   * @param id 场景ID
   */
  resetChallenge: (id: number) => {
    return request({
      url: `/api/v1/challenge-scenarios/${id}/reset`,
      method: 'post'
    })
  }
}