/**
 * A06 易受攻击组件相关类型定义
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */

export interface A06VulnerabilityInfo {
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

export interface A06DemoResult {
  success: boolean
  message: string
  data?: Record<string, any>
}

export interface A06AttackScenario {
  name: string
  description: string
  payload: string
  expectedResult: string
}

export const A06_ATTACK_SCENARIOS: A06AttackScenario[] = [
  {
    name: '过时组件攻击',
    description: '利用过时组件的已知漏洞',
    payload: 'vulnerable',
    expectedResult: '漏洞利用成功'
  },
  {
    name: '组件版本攻击',
    description: '利用特定版本的组件漏洞',
    payload: '1.0.0',
    expectedResult: '版本漏洞利用成功'
  }
]

export const A06_DEFAULT_TEST_DATA = {
  componentType: 'library',
  version: '1.0.0'
}
