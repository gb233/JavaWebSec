/**
 * A02 加密失败相关类型定义
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */

export interface A02VulnerabilityInfo {
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

export interface A02DemoResult {
  success: boolean
  message: string
  data?: Record<string, any>
}

export interface A02AttackScenario {
  name: string
  description: string
  payload: string
  expectedResult: string
}

export const A02_ATTACK_SCENARIOS: A02AttackScenario[] = [
  {
    name: '弱密码攻击',
    description: '使用弱密码进行身份验证',
    payload: 'admin',
    expectedResult: '认证成功'
  },
  {
    name: 'SQL注入绕过',
    description: '通过SQL注入绕过身份验证',
    payload: 'admin\' -- ',
    expectedResult: '认证成功'
  },
  {
    name: '空密码绕过',
    description: '使用空密码绕过验证',
    payload: '',
    expectedResult: '认证成功'
  }
]

export const A02_DEFAULT_TEST_DATA = {
  username: 'admin',
  password: 'admin'
}
