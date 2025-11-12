import request from '@/utils/request'

/**
 * A05 安全配置错误API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const a05Api = {
  /**
   * 演示不安全的配置
   * @param configType 配置类型
   * @param value 配置值
   */
  demonstrateVulnerable: (configType: string, value: string) => {
    return request({
      url: '/api/v1/demo/A05/vulnerable',
      method: 'get',
      params: { configType, value }
    })
  },

  /**
   * 演示安全的配置
   * @param configType 配置类型
   * @param value 配置值
   */
  demonstrateSecure: (configType: string, value: string) => {
    return request({
      url: '/api/v1/demo/A05/secure',
      method: 'get',
      params: { configType, value }
    })
  },

  /**
   * 获取流程图数据
   * @param type 流程图类型
   */
  getFlowchart: (type: string) => {
    return request({
      url: '/api/v1/demo/A05/flowchart',
      method: 'get',
      params: { type }
    })
  }
}
