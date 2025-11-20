import { computed } from 'vue'
import testApi, { type TestQuestion } from '@/api/test'

// 测试模式配置
export const testModeConfigs = {
  realtime: {
    allowNavigation: true,
    showImmediateFeedback: true,
    allowRetry: true,
    timeLimit: null,
    feedbackType: 'detailed',
    modeName: '实时反馈模式',
    description: '逐题实时反馈，适合学习巩固'
  },
  exam: {
    allowNavigation: false,
    showImmediateFeedback: false,
    allowRetry: false,
    timeLimit: 1800, // 30分钟
    feedbackType: 'none',
    modeName: '考试模式',
    description: '完整答题后统一分析，适合能力测试'
  },
  random: {
    allowNavigation: true,
    showImmediateFeedback: true,
    allowRetry: true,
    timeLimit: null,
    feedbackType: 'simple',
    modeName: '随机综合模式',
    description: '全类型随机出题，适合综合练习'
  }
}

// 答案结果类型
export interface AnswerResult {
  id: number
  sessionId: number
  questionId: number
  questionIndex: number
  userAnswer: string
  isCorrect: boolean
  score: number
  correctAnswer: string
  explanation: string
  showImmediately: boolean
  allowRetry: boolean
  feedbackType: string
}

// 核心测试模式逻辑
export const useTestMode = (modeCode: string) => {
  const config = testModeConfigs[modeCode as keyof typeof testModeConfigs] || testModeConfigs.realtime

  const processAnswer = async (sessionCode: string, question: TestQuestion, answer: string): Promise<AnswerResult> => {
    try {
      // 调用现有API
      const response = await testApi.submitAnswer(sessionCode, question.id, answer)

      if (response.success && response.data) {
        const result = response.data

        // 根据模式配置调整返回结果
        return {
          id: result.id || 0,
          sessionId: result.sessionId || 0,
          questionId: result.questionId || question.id,
          questionIndex: result.questionIndex || 0,
          userAnswer: result.userAnswer || answer,
          isCorrect: result.isCorrect || false,
          score: result.score || 0,
          correctAnswer: result.correctAnswer || '',
          explanation: result.explanation || '',
          showImmediately: config.showImmediateFeedback,
          allowRetry: config.allowRetry,
          feedbackType: config.feedbackType
        }
      } else {
        throw new Error(response.message || '提交答案失败')
      }
    } catch (error) {
      console.error('处理答案失败:', error)
      throw error
    }
  }

  return {
    config,
    processAnswer
  }
}

// 获取模式显示名称
export const getModeDisplayName = (modeCode: string): string => {
  const config = testModeConfigs[modeCode as keyof typeof testModeConfigs]
  return config?.modeName || '未知模式'
}

// 获取模式配置
export const getModeConfig = (modeCode: string) => {
  return testModeConfigs[modeCode as keyof typeof testModeConfigs] || testModeConfigs.realtime
}

// 检查是否允许导航
export const isNavigationAllowed = (modeCode: string): boolean => {
  const config = getModeConfig(modeCode)
  return config.allowNavigation
}

// 检查是否显示即时反馈
export const isImmediateFeedbackEnabled = (modeCode: string): boolean => {
  const config = getModeConfig(modeCode)
  return config.showImmediateFeedback
}

// 检查是否允许重试
export const isRetryAllowed = (modeCode: string): boolean => {
  const config = getModeConfig(modeCode)
  return config.allowRetry
}

// 获取时间限制
export const getTimeLimit = (modeCode: string): number | null => {
  const config = getModeConfig(modeCode)
  return config.timeLimit
}

// 获取反馈类型
export const getFeedbackType = (modeCode: string): string => {
  const config = getModeConfig(modeCode)
  return config.feedbackType
}
