/**
 * A01 越权访问相关类型定义
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */

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

export interface A01DemoResult {
  success: boolean
  message: string
  data?: Record<string, any>
}

export interface A01AttackScenario {
  name: string
  description: string
  payload: string
  expectedResult: string
}

export const A01_ATTACK_SCENARIOS: A01AttackScenario[] = [
  {
    name: '水平越权',
    description: '访问同级别其他用户的资源',
    payload: 'user2',
    expectedResult: '访问成功'
  },
  {
    name: '垂直越权',
    description: '获取更高级别的权限',
    payload: 'admin',
    expectedResult: '权限提升成功'
  }
]

export const A01_DEFAULT_TEST_DATA = {
  resourceId: '1',
  action: 'read'
}
