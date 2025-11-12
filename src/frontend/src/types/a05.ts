/**
 * A05 安全配置错误相关类型定义
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */

export interface A05VulnerabilityInfo {
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

export interface A05DemoResult {
  success: boolean
  message: string
  data?: Record<string, any>
}

export interface A05AttackScenario {
  name: string
  description: string
  payload: string
  expectedResult: string
}

export const A05_ATTACK_SCENARIOS: A05AttackScenario[] = [
  {
    name: '默认配置攻击',
    description: '利用默认配置漏洞',
    payload: 'default',
    expectedResult: '配置绕过成功'
  },
  {
    name: 'CORS配置错误',
    description: '利用CORS配置错误',
    payload: '*',
    expectedResult: '跨域请求成功'
  }
]

export const A05_DEFAULT_TEST_DATA = {
  configType: 'cors',
  value: '*'
}
