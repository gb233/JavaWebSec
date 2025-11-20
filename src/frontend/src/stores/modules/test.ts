import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { TestSession, TestQuestion, UserTestRecord, TestStats } from '@/api/test'

export const useTestStore = defineStore('test', () => {
  // 当前测试会话
  const currentSession = ref<TestSession | null>(null)
  const currentQuestions = ref<TestQuestion[]>([])
  const currentAnswers = ref<Record<number, string>>({})

  // 测试记录
  const testRecords = ref<UserTestRecord[]>([])
  const testStats = ref<TestStats | null>(null)

  // 加载状态
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 计算属性
  const isTestActive = computed(() => currentSession.value?.status === 'active')
  const currentQuestionIndex = computed(() => currentSession.value?.currentQuestionIndex || 0)
  const totalQuestions = computed(() => currentSession.value?.totalQuestions || 0)
  const answeredQuestions = computed(() => currentSession.value?.answeredQuestions || 0)
  const progress = computed(() => {
    if (totalQuestions.value === 0) return 0
    return Math.round((answeredQuestions.value / totalQuestions.value) * 100)
  })

  // 设置当前测试会话
  const setCurrentSession = (session: TestSession, questions: TestQuestion[]) => {
    currentSession.value = session
    currentQuestions.value = questions
    currentAnswers.value = {}
  }

  // 清除当前测试会话
  const clearCurrentSession = () => {
    currentSession.value = null
    currentQuestions.value = []
    currentAnswers.value = {}
  }

  // 更新答案
  const updateAnswer = (questionId: number, answer: string) => {
    currentAnswers.value[questionId] = answer
  }

  // 获取答案
  const getAnswer = (questionId: number) => {
    return currentAnswers.value[questionId] || ''
  }

  // 设置测试记录
  const setTestRecords = (records: UserTestRecord[]) => {
    testRecords.value = records
  }

  // 设置测试统计
  const setTestStats = (stats: TestStats) => {
    testStats.value = stats
  }

  // 设置加载状态
  const setLoading = (state: boolean) => {
    loading.value = state
  }

  // 设置错误
  const setError = (message: string | null) => {
    error.value = message
  }

  return {
    // 状态
    currentSession,
    currentQuestions,
    currentAnswers,
    testRecords,
    testStats,
    loading,
    error,

    // 计算属性
    isTestActive,
    currentQuestionIndex,
    totalQuestions,
    answeredQuestions,
    progress,

    // 方法
    setCurrentSession,
    clearCurrentSession,
    updateAnswer,
    getAnswer,
    setTestRecords,
    setTestStats,
    setLoading,
    setError
  }
}, {
  persist: {
    key: 'test-store',
    storage: localStorage,
    paths: ['currentSession', 'currentQuestions', 'currentAnswers']
  }
})