import request from '@/utils/request'

/**
 * A06 易受攻击组件API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const a06Api = {
  /**
   * 演示不安全的组件使用
   * @param componentType 组件类型
   * @param version 版本
   */
  demonstrateVulnerable: (componentType: string, version: string) => {
    return request({
      url: '/api/v1/demo/A06/vulnerable',
      method: 'get',
      params: { componentType, version }
    })
  },

  /**
   * 演示安全的组件使用
   * @param componentType 组件类型
   * @param version 版本
   */
  demonstrateSecure: (componentType: string, version: string) => {
    return request({
      url: '/api/v1/demo/A06/secure',
      method: 'get',
      params: { componentType, version }
    })
  },

  /**
   * 获取流程图数据
   * @param type 流程图类型
   */
  getFlowchart: (type: string) => {
    return request({
      url: '/api/v1/demo/A06/flowchart',
      method: 'get',
      params: { type }
    })
  }
}
