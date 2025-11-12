import request from '@/utils/request'

/**
 * A04 不安全设计API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const a04Api = {
  /**
   * 演示不安全的业务逻辑
   * @param action 操作类型
   * @param target 目标
   */
  demonstrateVulnerable: (action: string, target: string) => {
    return request({
      url: '/api/v1/demo/A04/vulnerable',
      method: 'get',
      params: { action, target }
    })
  },

  /**
   * 演示安全的业务逻辑
   * @param action 操作类型
   * @param target 目标
   */
  demonstrateSecure: (action: string, target: string) => {
    return request({
      url: '/api/v1/demo/A04/secure',
      method: 'get',
      params: { action, target }
    })
  },

  /**
   * 获取流程图数据
   * @param type 流程图类型
   */
  getFlowchart: (type: string) => {
    return request({
      url: '/api/v1/demo/A04/flowchart',
      method: 'get',
      params: { type }
    })
  }
}
