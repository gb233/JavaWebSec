import { ref, computed } from 'vue'
import { createProgressTrackingService, ProgressTrackingService } from '@/services/progressTrackingService'

/**
 * 进度跟踪组合式函数
 */
export function useProgressTracking(userId: number) {
  const progressService = ref<ProgressTrackingService | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 初始化进度跟踪服务
  const initProgressService = () => {
    if (!progressService.value) {
      progressService.value = createProgressTrackingService(userId)
    }
  }

  // 记录页面访问
  const recordPageVisit = async (
    vulnerabilityCode: string,
    pageType: string,
    duration: number
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordPageVisit(vulnerabilityCode, pageType, duration)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录页面访问失败'
    } finally {
      loading.value = false
    }
  }

  // 记录用户交互
  const recordUserInteraction = async (
    vulnerabilityCode: string,
    interactionType: string,
    interactionData: Record<string, any>
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordUserInteraction(vulnerabilityCode, interactionType, interactionData)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录用户交互失败'
    } finally {
      loading.value = false
    }
  }

  // 记录演示执行
  const recordDemoExecution = async (
    vulnerabilityCode: string,
    demoType: string,
    executionData: Record<string, any>
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordDemoExecution(vulnerabilityCode, demoType, executionData)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录演示执行失败'
    } finally {
      loading.value = false
    }
  }

  // 记录学习完成
  const recordLearningCompleted = async (
    vulnerabilityCode: string,
    studyTime: number,
    score: number
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordLearningCompleted(vulnerabilityCode, studyTime, score)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录学习完成失败'
    } finally {
      loading.value = false
    }
  }

  // 记录测试完成
  const recordTestCompleted = async (
    vulnerabilityCode: string,
    score: number,
    accuracy: number
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordTestCompleted(vulnerabilityCode, score, accuracy)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录测试完成失败'
    } finally {
      loading.value = false
    }
  }

  // 记录挑战完成
  const recordChallengeCompleted = async (
    vulnerabilityCode: string,
    score: number,
    badge?: string
  ) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.recordChallengeCompleted(vulnerabilityCode, score, badge)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '记录挑战完成失败'
    } finally {
      loading.value = false
    }
  }

  // 更新学习时长
  const updateStudyTime = async (additionalTime: number) => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.updateStudyTime(additionalTime)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '更新学习时长失败'
    } finally {
      loading.value = false
    }
  }

  // 更新连续学习天数
  const updateStreak = async () => {
    loading.value = true
    error.value = null

    try {
      initProgressService()
      await progressService.value!.updateStreak()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '更新连续学习天数失败'
    } finally {
      loading.value = false
    }
  }

  // 检查学习完成状态
  const isLearningCompleted = async (vulnerabilityCode: string): Promise<boolean> => {
    try {
      initProgressService()
      return await progressService.value!.isLearningCompleted(vulnerabilityCode)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '检查学习完成状态失败'
      return false
    }
  }

  // 检查测试通过状态
  const isTestPassed = async (vulnerabilityCode: string): Promise<boolean> => {
    try {
      initProgressService()
      return await progressService.value!.isTestPassed(vulnerabilityCode)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '检查测试通过状态失败'
      return false
    }
  }

  // 检查挑战完成状态
  const isChallengeCompleted = async (vulnerabilityCode: string): Promise<boolean> => {
    try {
      initProgressService()
      return await progressService.value!.isChallengeCompleted(vulnerabilityCode)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '检查挑战完成状态失败'
      return false
    }
  }

  // 获取最近活动
  const getRecentActivities = async (limit: number = 10) => {
    try {
      initProgressService()
      return await progressService.value!.getRecentActivities(limit)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取最近活动失败'
      return []
    }
  }

  // 获取活动统计
  const getActivityStatistics = async () => {
    try {
      initProgressService()
      return await progressService.value!.getActivityStatistics()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取活动统计失败'
      return {}
    }
  }

  // 计算属性
  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)
  const errorMessage = computed(() => error.value)

  return {
    // 状态
    isLoading,
    hasError,
    errorMessage,

    // 方法
    recordPageVisit,
    recordUserInteraction,
    recordDemoExecution,
    recordLearningCompleted,
    recordTestCompleted,
    recordChallengeCompleted,
    updateStudyTime,
    updateStreak,
    isLearningCompleted,
    isTestPassed,
    isChallengeCompleted,
    getRecentActivities,
    getActivityStatistics
  }
}
