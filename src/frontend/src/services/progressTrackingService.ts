import { userStatsApi } from '@/api/userStats'
import { learningProgressApi } from '@/api/learningProgress'
import { testProgressApi } from '@/api/testProgress'
import { challengeProgressApi } from '@/api/challengeProgress'
import { userActivityApi } from '@/api/userActivity'

/**
 * 进度跟踪服务
 * 统一管理用户学习、测试、挑战进度的跟踪和更新
 */
export class ProgressTrackingService {
  private userId: number

  constructor(userId: number) {
    this.userId = userId
  }

  /**
   * 记录页面访问
   */
  async recordPageVisit(vulnerabilityCode: string, pageType: string, duration: number) {
    try {
      await learningProgressApi.recordPageVisit({
        userId: this.userId,
        vulnerabilityCode,
        pageType,
        duration
      })
    } catch (error) {
      console.error('记录页面访问失败:', error)
    }
  }

  /**
   * 记录用户交互
   */
  async recordUserInteraction(
    vulnerabilityCode: string,
    interactionType: string,
    interactionData: Record<string, any>
  ) {
    try {
      await learningProgressApi.recordUserInteraction({
        userId: this.userId,
        vulnerabilityCode,
        interactionType,
        interactionData
      })
    } catch (error) {
      console.error('记录用户交互失败:', error)
    }
  }

  /**
   * 记录演示执行
   */
  async recordDemoExecution(
    vulnerabilityCode: string,
    demoType: string,
    executionData: Record<string, any>
  ) {
    try {
      await learningProgressApi.recordDemoExecution({
        userId: this.userId,
        vulnerabilityCode,
        demoType,
        executionData
      })
    } catch (error) {
      console.error('记录演示执行失败:', error)
    }
  }

  /**
   * 记录学习完成（所有操作都是关键操作，必须全部成功）
   */
  async recordLearningCompleted(
    vulnerabilityCode: string,
    studyTime: number,
    score: number
  ) {
    // 第一步：更新用户统计（关键操作，必须成功）
    try {
      await userStatsApi.updateVulnerabilityStats({
        userId: this.userId,
        vulnerabilityCode,
        studyTime,
        points: score
      })
      console.log('用户统计更新成功')
    } catch (error: any) {
      console.error('更新用户统计失败:', error?.message || error)
      // 统计更新失败必须抛出异常，这是系统功能
      throw new Error(`更新用户统计失败: ${error?.message || '未知错误'}`)
    }

    // 第二步：记录活动（关键操作，必须成功）
    try {
      await userActivityApi.recordLearningCompleted({
        userId: this.userId,
        vulnerabilityCode,
        studyTime,
        score
      })
      console.log('学习活动记录成功')
    } catch (error: any) {
      console.error('记录学习活动失败:', error?.message || error)
      // 活动记录失败必须抛出异常，这是系统功能
      throw new Error(`记录学习活动失败: ${error?.message || '未知错误'}`)
    }

    // 所有操作都成功
    console.log('记录学习完成成功: 统计更新和活动记录都成功')
  }

  /**
   * 记录测试完成
   */
  async recordTestCompleted(
    vulnerabilityCode: string,
    score: number,
    accuracy: number
  ) {
    try {
      const passed = score >= 70 && accuracy >= 0.7

      // 更新用户统计
      await userStatsApi.updateTestStats({
        userId: this.userId,
        testId: 0, // 暂时使用0，实际应该传入真实的testId
        passed,
        score,
        points: score
      })

      // 记录测试完成
      await testProgressApi.recordTestCompletion({
        userId: this.userId,
        vulnerabilityCode,
        score,
        accuracy
      })

      // 记录活动
      if (passed) {
        await userActivityApi.recordTestPassed({
          userId: this.userId,
          vulnerabilityCode,
          score,
          accuracy
        })
      }
    } catch (error) {
      console.error('记录测试完成失败:', error)
    }
  }

  /**
   * 记录挑战完成
   */
  async recordChallengeCompleted(
    vulnerabilityCode: string,
    score: number,
    badge?: string
  ) {
    try {
      // 更新用户统计
      await userStatsApi.updateChallengeStats({
        userId: this.userId,
        challengeId: 0, // 暂时使用0，实际应该传入真实的challengeId
        completed: true,
        points: score,
        badge
      })

      // 记录挑战完成
      await challengeProgressApi.recordChallengeCompletion({
        userId: this.userId,
        vulnerabilityCode,
        score,
        badge
      })

      // 记录活动
      await userActivityApi.recordChallengeCompleted({
        userId: this.userId,
        vulnerabilityCode,
        score,
        badge
      })
    } catch (error) {
      console.error('记录挑战完成失败:', error)
    }
  }

  /**
   * 更新学习时长
   */
  async updateStudyTime(additionalTime: number) {
    try {
      await userStatsApi.updateStudyTimeStats({
        userId: this.userId,
        additionalTime
      })
    } catch (error) {
      console.error('更新学习时长失败:', error)
    }
  }

  /**
   * 更新连续学习天数
   */
  async updateStreak() {
    try {
      await userStatsApi.updateStreakStats({
        userId: this.userId
      })
    } catch (error) {
      console.error('更新连续学习天数失败:', error)
    }
  }

  /**
   * 检查学习完成状态
   */
  async isLearningCompleted(vulnerabilityCode: string): Promise<boolean> {
    try {
      const response = await learningProgressApi.isLearningCompleted(this.userId, vulnerabilityCode)
      return response.data || false
    } catch (error) {
      console.error('检查学习完成状态失败:', error)
      return false
    }
  }

  /**
   * 检查测试通过状态
   */
  async isTestPassed(vulnerabilityCode: string): Promise<boolean> {
    try {
      const response = await testProgressApi.isTestPassed(this.userId, vulnerabilityCode)
      return response.data || false
    } catch (error) {
      console.error('检查测试通过状态失败:', error)
      return false
    }
  }

  /**
   * 检查挑战完成状态
   */
  async isChallengeCompleted(vulnerabilityCode: string): Promise<boolean> {
    try {
      const response = await challengeProgressApi.isChallengeCompleted(this.userId, vulnerabilityCode)
      return response.data || false
    } catch (error) {
      console.error('检查挑战完成状态失败:', error)
      return false
    }
  }

  /**
   * 获取最近活动
   */
  async getRecentActivities(limit: number = 10) {
    try {
      const response = await userActivityApi.getRecentActivities(this.userId, limit)
      return response.data || []
    } catch (error) {
      console.error('获取最近活动失败:', error)
      return []
    }
  }

  /**
   * 获取活动统计
   */
  async getActivityStatistics() {
    try {
      const response = await userActivityApi.getActivityStatistics(this.userId)
      return response.data || {}
    } catch (error) {
      console.error('获取活动统计失败:', error)
      return {}
    }
  }
}

/**
 * 创建进度跟踪服务实例
 */
export function createProgressTrackingService(userId: number): ProgressTrackingService {
  return new ProgressTrackingService(userId)
}
