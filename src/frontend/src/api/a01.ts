import { request } from '@/utils/request'

export interface A01Response {
  success: boolean
  message: string
  data?: Record<string, unknown>
}

export interface A01VulnerabilityInfo {
  category: string
  subType: string
  description: string
  recommendations: string[]
  attackVector: string[]
  harmScenarios: string[]
  supportedParameters: string[]
  attackPayloads: string[]
  defensePayloads: string[]
}

export interface A01AttackType {
  id: string
  name: string
  description: string
  examples: string[]
}

/**
 * A01 越权访问专用API接口
 * 提供A01特定的便捷方法
 */
export const a01Api = {
  /**
   * 演示A01越权访问攻击
   * @param attackType 攻击类型 (horizontal-order, vertical-order, horizontal-user, vertical-user)
   * @param resourceId 资源ID
   */
  demonstrateVulnerable: (attackType: string, resourceId: string): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/vulnerable',
      method: 'get',
      params: {
        attackType,
        resourceId
      }
    })
  },

  /**
   * 演示A01越权访问防护
   * @param attackType 攻击类型
   * @param resourceId 资源ID
   */
  demonstrateSecure: (attackType: string, resourceId: string): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/secure',
      method: 'get',
      params: {
        attackType,
        resourceId
      }
    })
  },

  /**
   * 获取A01漏洞信息
   */
  getVulnerabilityInfo: (): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/info',
      method: 'get'
    })
  },

  /**
   * 获取A01支持的攻击类型
   */
  getSupportedAttackTypes: (): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/attack-types',
      method: 'get'
    })
  },

  /**
   * 获取A01攻击载荷
   * @param attackType 攻击类型
   */
  getAttackPayloads: (attackType: string): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/attack-payloads',
      method: 'get',
      params: {
        attackType
      }
    })
  },

  /**
   * 获取A01防护载荷
   * @param attackType 攻击类型
   */
  getDefensePayloads: (attackType: string): Promise<A01Response> => {
    return request({
      url: '/api/v1/demo/A01/defense-payloads',
      method: 'get',
      params: {
        attackType
      }
    })
  }
}
