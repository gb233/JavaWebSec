import request from '@/utils/request'

/**
 * A02 加密失败API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const a02Api = {
  /**
   * 演示不安全的加密实现
   * @param username 用户名
   * @param password 密码
   */
  demonstrateVulnerable: (username: string, password: string) => {
    return request({
      url: '/api/v1/demo/A02/vulnerable',
      method: 'get',
      params: { username, password }
    })
  },

  /**
   * 演示安全的加密实现
   * @param username 用户名
   * @param password 密码
   */
  demonstrateSecure: (username: string, password: string) => {
    return request({
      url: '/api/v1/demo/A02/secure',
      method: 'get',
      params: { username, password }
    })
  },

  /**
   * 获取流程图数据
   * @param type 流程图类型
   */
  getFlowchart: (type: string) => {
    return request({
      url: '/api/v1/demo/A02/flowchart',
      method: 'get',
      params: { type }
    })
  }
}
