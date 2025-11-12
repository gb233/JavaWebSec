/**
 * A04 不安全设计相关类型定义
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */

export interface A04VulnerabilityInfo {
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

export interface A04DemoResult {
  success: boolean
  message: string
  data?: Record<string, any>
}

export interface A04AttackScenario {
  name: string
  description: string
  payload: string
  expectedResult: string
}

export const A04_ATTACK_SCENARIOS: A04AttackScenario[] = [
  {
    name: '业务逻辑绕过',
    description: '绕过业务逻辑验证',
    payload: 'bypass',
    expectedResult: '绕过成功'
  },
  {
    name: '权限检查绕过',
    description: '绕过权限检查机制',
    payload: 'admin',
    expectedResult: '权限提升成功'
  }
]

export const A04_DEFAULT_TEST_DATA = {
  action: 'transfer',
  target: 'user2'
}
