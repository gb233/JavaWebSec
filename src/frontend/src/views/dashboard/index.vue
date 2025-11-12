<template>
  <div class="dashboard-container">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <h1 class="page-title">
        {{ $t('dashboard.title') }}
      </h1>
      <p class="page-subtitle">
        {{ $t('dashboard.welcome', { name: authStore.userDisplayName }) }}
      </p>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <ElCard class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon learning">
            <ElIcon :size="32">
              <Reading />
            </ElIcon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ userStats?.completedVulnerabilities || 0 }}
            </div>
            <div class="stat-label">
              {{ $t('dashboard.learnedVulnerabilities') }}
            </div>
          </div>
        </div>
      </ElCard>

      <ElCard class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon test">
            <ElIcon :size="32">
              <EditPen />
            </ElIcon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ userStats?.passedTests || 0 }}
            </div>
            <div class="stat-label">
              {{ $t('dashboard.passedTests') }}
            </div>
          </div>
        </div>
      </ElCard>

      <ElCard class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon challenge">
            <ElIcon :size="32">
              <Trophy />
            </ElIcon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ userStats?.completedChallenges || 0 }}
            </div>
            <div class="stat-label">
              {{ $t('dashboard.completedChallenges') }}
            </div>
          </div>
        </div>
      </ElCard>

      <ElCard class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon points">
            <ElIcon :size="32">
              <Star />
            </ElIcon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ userStats?.totalPoints || 0 }}
            </div>
            <div class="stat-label">
              {{ $t('dashboard.totalPoints') }}
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <!-- 主要内容区域 -->
    <ElRow :gutter="24" class="dashboard-content">
      <!-- 左侧内容 -->
      <ElCol :lg="16" :md="24">
        <!-- 学习进度卡片 -->
        <ElCard class="progress-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <ElIcon><DataLine /></ElIcon>
              <span>{{ $t('dashboard.learningProgress') }}</span>
            </div>
          </template>

          <div class="progress-content">
            <div class="progress-item">
              <div class="progress-label">
                {{ $t('dashboard.owaspProgress') }}
              </div>
              <ElProgress
                :percentage="owaspProgress"
                :stroke-width="12"
                :text-inside="true"
                class="progress-bar"
              />
            </div>

            <div class="progress-item">
              <div class="progress-label">
                {{ $t('dashboard.overallProgress') }}
              </div>
              <ElProgress
                :percentage="totalProgress"
                :stroke-width="12"
                :text-inside="true"
                status="success"
                class="progress-bar"
              />
            </div>
          </div>
        </ElCard>

        <!-- 最近活动卡片 -->
        <ElCard class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <ElIcon><Clock /></ElIcon>
              <span>{{ $t('dashboard.recentActivity') }}</span>
            </div>
          </template>

      <div class="activity-list">
        <div
          v-if="displayActivities.length === 0"
          class="activity-empty"
        >
          <ElIcon :size="48" color="#dcdfe6">
            <Clock />
          </ElIcon>
          <p>{{ $t('dashboard.noRecentActivity') }}</p>
        </div>
        <div
          v-for="(activity, index) in displayActivities"
          v-else
          :key="activity.timestamp || index"
          class="activity-item"
        >
          <div class="activity-icon" :class="activity.type">
            <ElIcon>
              <component :is="activity.iconComponent" />
            </ElIcon>
          </div>
          <div class="activity-content">
            <div class="activity-title">
              {{ activity.title }}
            </div>
            <div class="activity-time">
              {{ activity.timeLabel }}
            </div>
          </div>
        </div>
      </div>
        </ElCard>
      </ElCol>

      <!-- 右侧内容 -->
      <ElCol :lg="8" :md="24">
        <!-- 快速导航卡片 -->
        <ElCard class="quick-nav-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <ElIcon><Compass /></ElIcon>
              <span>{{ $t('dashboard.quickNavigation') }}</span>
            </div>
          </template>

          <div class="quick-nav-grid">
            <div class="nav-item" @click="$router.push('/knowledge/center')">
              <ElIcon><Reading /></ElIcon>
              <span>{{ $t('dashboard.startLearning') }}</span>
            </div>
            <div class="nav-item" @click="$router.push('/test/categories')">
              <ElIcon><EditPen /></ElIcon>
              <span>{{ $t('dashboard.knowledgeTest') }}</span>
            </div>
            <div class="nav-item" @click="$router.push('/challenge/list')">
              <ElIcon><Trophy /></ElIcon>
              <span>{{ $t('dashboard.challengeMode') }}</span>
            </div>
            <div class="nav-item" @click="$router.push('/knowledge/center')">
              <ElIcon><Monitor /></ElIcon>
              <span>{{ $t('dashboard.vulnerabilityDemo') }}</span>
            </div>
          </div>
        </ElCard>

        <!-- 学习日历卡片 -->
        <ElCard class="calendar-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <ElIcon><Calendar /></ElIcon>
              <span>{{ $t('dashboard.learningCalendar') }}</span>
            </div>
          </template>

          <div class="calendar-content">
            <div class="streak-info">
              <div class="streak-number">
                {{ userStats?.currentStreak || 0 }}
              </div>
              <div class="streak-label">
                {{ $t('dashboard.consecutiveDays') }}
              </div>
            </div>

            <div class="calendar-placeholder">
              <ElIcon :size="48" color="#dcdfe6">
                <Calendar />
              </ElIcon>
              <p>{{ $t('dashboard.calendarComingSoon') }}</p>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  Reading,
  EditPen,
  Trophy,
  Star,
  DataLine,
  Clock,
  Compass,
  Monitor,
  Calendar
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { useUserStore } from '@/stores/modules/user'
import { ElMessage } from 'element-plus'
import { fetchDashboardOverview } from '@/api/dashboard'
import type { DashboardOverview, DashboardActivity, DashboardHighlight } from '@/types/api'
import { isSuccessResponse } from '@/utils/api-helpers'

const { t: $t } = useI18n()
const authStore = useAuthStore()
const userStore = useUserStore()

const overviewData = ref<DashboardOverview | null>(null)
const activityList = ref<DashboardActivity[]>([])
const highlights = ref<DashboardHighlight[]>([])

const iconComponentMap: Record<string, any> = {
  Reading,
  EditPen,
  Trophy,
  Monitor,
  DataLine,
  Clock,
  Star
}

const userStats = computed(() => {
  if (overviewData.value?.userStats) {
    return overviewData.value.userStats
  }
  return (
    authStore.userStats || {
      completedVulnerabilities: 0,
      passedTests: 0,
      completedChallenges: 0,
      earnedBadges: 0,
      totalPoints: 0,
      totalStudyTime: 0,
      currentStreak: 0,
      longestStreak: 0
    }
  )
})

// 计算学习进度
const owaspProgress = computed(() => {
  const completed = userStats.value?.completedVulnerabilities || 0
  return Math.min(Math.round((completed / 10) * 100), 100)
})

const totalProgress = computed(() => {
  const total = (userStats.value?.completedVulnerabilities || 0) +
                 (userStats.value?.passedTests || 0) +
                 (userStats.value?.completedChallenges || 0)
  return Math.min(Math.round((total / 50) * 100), 100)
})

// 最近活动数据
const displayActivities = computed(() => {
  if (!activityList.value.length) {
    return []
  }
  return activityList.value.map(item => {
    // 生成国际化title
    let title = item.title
    if (!title) {
      if (item.activityKey) {
        // 使用国际化key
        title = $t(item.activityKey)
      } else if (item.testName) {
        // 根据testName和isPassed动态生成
        const key = item.isPassed 
          ? 'dashboard.activity.testPassed' 
          : 'dashboard.activity.testCompleted'
        title = $t(key, { testName: item.testName })
      }
    }
    
    // 生成国际化timeLabel
    let timeLabel = item.timeAgo
    if (!timeLabel && item.timestamp) {
      // 根据时间戳计算相对时间
      const now = new Date()
      const timestamp = new Date(item.timestamp)
      const diffMs = now.getTime() - timestamp.getTime()
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
      const diffDays = Math.floor(diffHours / 24)
      
      if (diffHours < 1) {
        timeLabel = $t('dashboard.activity.justNow')
      } else if (diffHours < 24) {
        timeLabel = $t('dashboard.activity.hoursAgo', { hours: diffHours })
      } else if (diffDays === 1) {
        timeLabel = $t('dashboard.activity.oneDayAgo')
      } else {
        timeLabel = $t('dashboard.activity.daysAgo', { days: diffDays })
      }
    } else if (!timeLabel) {
      timeLabel = $t('dashboard.activity.justNow')
    }
    
    return {
      ...item,
      title,
      iconComponent: iconComponentMap[item.icon] || Reading,
      timeLabel
    }
  })
})

onMounted(async () => {
  try {
    await userStore.fetchCurrentUserProfile()
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
  await loadDashboardOverview()
})

const loadDashboardOverview = async () => {
  try {
    const response = await fetchDashboardOverview()
    if (isSuccessResponse(response) && response.data) {
      overviewData.value = response.data
      activityList.value = Array.isArray(response.data.recentActivities)
        ? response.data.recentActivities
        : []
      // 移除示例数据，如果没有真实数据就显示空列表
      highlights.value = Array.isArray(response.data.highlights)
        ? response.data.highlights
        : []
    } else {
      throw new Error(response?.message || '获取仪表盘数据失败')
    }
  } catch (error) {
    console.error('获取仪表盘概览失败:', error)
    ElMessage.error('获取仪表盘数据失败')
    // 移除示例数据，失败时也显示空列表
    activityList.value = []
    highlights.value = []
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 24px;

  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: #606266;
    margin: 0;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 24px;

  .stat-card {
    border-radius: 12px;
    border: 1px solid #ebeef5;

    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;

        &.learning {
          background: linear-gradient(135deg, #409eff, #36a3f7);
        }

        &.test {
          background: linear-gradient(135deg, #67c23a, #5daf34);
        }

        &.challenge {
          background: linear-gradient(135deg, #e6a23c, #cf9236);
        }

        &.points {
          background: linear-gradient(135deg, #f56c6c, #dd6161);
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          line-height: 1;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
}

.dashboard-content {
  .el-col {
    margin-bottom: 24px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.progress-card {
  margin-bottom: 24px;
  border-radius: 12px;

  .progress-content {
    .progress-item {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .progress-label {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }

      .progress-bar {
        margin-bottom: 8px;
      }
    }
  }
}

.activity-card {
  border-radius: 12px;

  .activity-list {
    .activity-empty {
      text-align: center;
      padding: 40px 20px;
      color: #909399;

      .el-icon {
        margin-bottom: 12px;
      }

      p {
        margin: 0;
        font-size: 14px;
      }
    }

    .activity-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px 0;
      border-bottom: 1px solid #f0f2f5;

      &:last-child {
        border-bottom: none;
      }

      .activity-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;

        &.learning {
          background: #409eff;
        }

        &.test {
          background: #67c23a;
        }

        &.challenge {
          background: #e6a23c;
        }
      }

      .activity-content {
        flex: 1;

        .activity-title {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }

        .activity-time {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

.quick-nav-card {
  margin-bottom: 24px;
  border-radius: 12px;

  .quick-nav-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .nav-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        background: #f0f9ff;
        transform: translateY(-2px);
      }

      .el-icon {
        font-size: 24px;
        color: #409eff;
        margin-bottom: 8px;
      }

      span {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.calendar-card {
  border-radius: 12px;

  .calendar-content {
    .streak-info {
      text-align: center;
      margin-bottom: 24px;

      .streak-number {
        font-size: 36px;
        font-weight: 700;
        color: #67c23a;
        line-height: 1;
      }

      .streak-label {
        font-size: 14px;
        color: #606266;
        margin-top: 4px;
      }
    }

    .calendar-placeholder {
      text-align: center;
      padding: 20px;
      color: #909399;

      p {
        margin: 8px 0 0 0;
        font-size: 14px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .quick-nav-grid {
    grid-template-columns: 1fr !important;
  }
}
</style>
