<template>
  <div class="knowledge-center">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <ElIcon class="title-icon"><Reading /></ElIcon>
          {{ $t('knowledge.title') }}
        </h1>
        <p class="page-description">{{ $t('knowledge.description') }}</p>
      </div>
    </div>

    <!-- 统计概览 -->
    <ElRow :gutter="24" class="stats-overview">
      <ElCol :xs="24" :sm="8" :md="6">
        <ElCard shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <ElIcon><Warning /></ElIcon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalVulnerabilities }}</div>
              <div class="stat-label">{{ $t('knowledge.vulnerabilityTypes') }}</div>
            </div>
          </div>
        </ElCard>
      </ElCol>
      <ElCol :xs="24" :sm="8" :md="6">
        <ElCard shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <ElIcon><Check /></ElIcon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ completedVulnerabilitiesCount }}</div>
              <div class="stat-label">{{ $t('knowledge.completed') }}</div>
            </div>
          </div>
        </ElCard>
      </ElCol>
      <ElCol :xs="24" :sm="8" :md="6">
        <ElCard shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon learning">
              <ElIcon><Clock /></ElIcon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStudyTime }}</div>
              <div class="stat-label">{{ $t('knowledge.studyTime') }}</div>
            </div>
          </div>
        </ElCard>
      </ElCol>
      <ElCol :xs="24" :sm="8" :md="6">
        <ElCard shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon progress">
              <ElIcon><DataLine /></ElIcon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ Math.round(completionRate) }}%</div>
              <div class="stat-label">{{ $t('knowledge.completionRate') }}</div>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <!-- 漏洞分类 -->
    <ElCard shadow="never" class="vulnerability-categories">
      <template #header>
        <div class="card-header">
          <span>{{ $t('knowledge.owaspTop10') }}</span>
          <ElTag type="info" size="small">{{ $t('knowledge.categories') }}</ElTag>
        </div>
      </template>

      <ElRow :gutter="20">
        <ElCol 
          v-for="category in vulnerabilityCategories" 
          :key="category.code"
          :xs="24" 
          :sm="12" 
          :lg="8"
        >
          <div class="category-card" @click="navigateToCategory(category.code)">
            <div class="category-header">
              <div class="category-icon" :class="category.severity">
                <ElIcon><component :is="category.icon" /></ElIcon>
              </div>
              <div class="category-info">
                <h3 class="category-title">{{ category.name }}</h3>
                <p class="category-description">{{ category.description }}</p>
              </div>
            </div>
            <div class="category-stats">
              <div class="stat-item">
                <span class="stat-label">{{ $t('knowledge.vulnerabilityCount') }}</span>
                <span class="stat-value">{{ category.vulnerabilityCount }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">{{ $t('knowledge.completionProgress') }}</span>
                <ElProgress 
                  :percentage="category.completionRate" 
                  :stroke-width="6"
                  :show-text="false"
                />
                <span class="stat-value">{{ category.completionRate }}%</span>
              </div>
            </div>
            <div class="category-actions">
              <ElButton type="primary" size="small" @click.stop="navigateToCategory(category.code)">
                {{ $t('knowledge.startLearning') }}
              </ElButton>
              <ElButton size="small" @click.stop="viewCategoryDetails(category.code)">
                {{ $t('knowledge.viewDetails') }}
              </ElButton>
            </div>
          </div>
        </ElCol>
      </ElRow>
    </ElCard>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { userStatsApi } from '@/api/userStats'
import { userActivityApi } from '@/api/userActivity'
import { badgeProgressApi } from '@/api/badgeProgressApi'
import { vulnerabilityProgressApi } from '@/api/vulnerabilityProgress'
import { isSuccessResponse } from '@/utils/api-helpers'
import { useAuthStore } from '@/stores/modules/auth'
import { 
  Reading, 
  Warning, 
  Check, 
  Clock, 
  DataLine, 
  Lock,
  Key,
  Document,
  Files,
  Monitor,
  Tools,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()

// 响应式数据
const totalVulnerabilities = ref(10)
const completedVulnerabilities = ref(0)
const totalStudyTime = ref(0)
const loading = ref(false)
const categoryCompletionRates = ref<Record<string, number>>({})

// 计算属性
const completionRate = computed(() => {
  return totalVulnerabilities.value > 0 
    ? (completedVulnerabilitiesCount.value / totalVulnerabilities.value) * 100 
    : 0
})

// 计算已完成数量（完成率为100%的分类数量）
const completedVulnerabilitiesCount = computed(() => {
  const categories = ['A01', 'A02', 'A03', 'A04', 'A05', 'A06', 'A07', 'A08', 'A09', 'A10']
  return categories.filter(category => getCategoryCompletionRate(category) === 100).length
})

// 漏洞分类数据
const vulnerabilityCategories = computed(() => [
  {
    code: 'A01',
    name: t('knowledge.a01.name'),
    description: t('knowledge.a01.description'),
    severity: 'critical',
    icon: 'Lock',
    vulnerabilityCount: 5,
    completionRate: getCategoryCompletionRate('A01')
  },
  {
    code: 'A02',
    name: t('knowledge.a02.name'),
    description: t('knowledge.a02.description'),
    severity: 'high',
    icon: 'Key',
    vulnerabilityCount: 3,
    completionRate: getCategoryCompletionRate('A02')
  },
  {
    code: 'A03',
    name: t('knowledge.a03.name'),
    description: t('knowledge.a03.description'),
    severity: 'critical',
    icon: 'Warning',
    vulnerabilityCount: 8,
    completionRate: getCategoryCompletionRate('A03')
  },
  {
    code: 'A04',
    name: t('knowledge.a04.name'),
    description: t('knowledge.a04.description'),
    severity: 'medium',
    icon: 'Document',
    vulnerabilityCount: 4,
    completionRate: getCategoryCompletionRate('A04')
  },
  {
    code: 'A05',
    name: t('knowledge.a05.name'),
    description: t('knowledge.a05.description'),
    severity: 'medium',
    icon: 'Setting',
    vulnerabilityCount: 3,
    completionRate: getCategoryCompletionRate('A05')
  },
  {
    code: 'A06',
    name: t('knowledge.a06.name'),
    description: t('knowledge.a06.description'),
    severity: 'high',
    icon: 'Files',
    vulnerabilityCount: 2,
    completionRate: getCategoryCompletionRate('A06')
  },
  {
    code: 'A07',
    name: t('knowledge.a07.name'),
    description: t('knowledge.a07.description'),
    severity: 'critical',
    icon: 'User',
    vulnerabilityCount: 4,
    completionRate: getCategoryCompletionRate('A07')
  },
  {
    code: 'A08',
    name: t('knowledge.a08.name'),
    description: t('knowledge.a08.description'),
    severity: 'high',
    icon: 'User',
    vulnerabilityCount: 2,
    completionRate: getCategoryCompletionRate('A08')
  },
  {
    code: 'A09',
    name: t('knowledge.a09.name'),
    description: t('knowledge.a09.description'),
    severity: 'medium',
    icon: 'Monitor',
    vulnerabilityCount: 3,
    completionRate: getCategoryCompletionRate('A09')
  },
  {
    code: 'A10',
    name: t('knowledge.a10.name'),
    description: t('knowledge.a10.description'),
    severity: 'high',
    icon: 'Tools',
    vulnerabilityCount: 2,
    completionRate: getCategoryCompletionRate('A10')
  }
])

// 方法
const navigateToCategory = (code: string) => {
  router.push(`/knowledge/category/${code}`)
}

const viewCategoryDetails = (code: string) => {
  router.push(`/knowledge/category/${code}`)
}

// 获取分类完成率的方法
const getCategoryCompletionRate = (categoryCode: string) => {
  // 从真实API获取分类完成率，而不是硬编码
  if (categoryCompletionRates.value && categoryCompletionRates.value[categoryCode]) {
    return categoryCompletionRates.value[categoryCode]
  }
  return 0
}

// 数据获取方法
const loadUserProgress = async () => {
  loading.value = true
  try {
    console.log('开始加载用户进度数据...')
    
    // 获取用户徽章进度统计
    console.log('调用徽章进度API...')
    const badgeStatsResponse = await badgeProgressApi.getUserBadgeProgressStats()
    console.log('徽章进度API响应:', badgeStatsResponse)
    
    console.log('检查徽章进度API响应:', badgeStatsResponse.data)
    console.log('isSuccessResponse结果:', isSuccessResponse(badgeStatsResponse.data))
    
    // 直接检查响应数据，不依赖isSuccessResponse
    if (badgeStatsResponse.data && badgeStatsResponse.data.data) {
      const stats = badgeStatsResponse.data.data
      console.log('徽章进度数据:', stats)
      completedVulnerabilities.value = stats.completedProgress || 0
      console.log('更新已完成数量:', completedVulnerabilities.value)
    } else {
      console.warn('徽章进度API响应失败或无数据:', badgeStatsResponse.data)
      completedVulnerabilities.value = 0
      console.log('使用默认已完成数量:', completedVulnerabilities.value)
    }

    // 获取用户活动统计
    const authStore = useAuthStore()
    
    if (!authStore.isLoggedIn || !authStore.user) {
      console.warn('用户未登录，无法加载进度数据')
      totalStudyTime.value = 0
      return
    }
    
    const currentUser = authStore.user
    console.log('当前用户信息:', currentUser)
    
    if (currentUser.id) {
      console.log('调用用户活动API，用户ID:', currentUser.id)
      const activityStatsResponse = await userActivityApi.getActivityStatistics(currentUser.id)
      console.log('用户活动API响应:', activityStatsResponse)
      
      console.log('检查用户活动API响应:', activityStatsResponse.data)
      console.log('isSuccessResponse结果:', isSuccessResponse(activityStatsResponse.data))
      
      // 直接检查响应数据，不依赖isSuccessResponse
      if (activityStatsResponse.data && activityStatsResponse.data.totalStudyTime !== undefined) {
        // 用户活动API直接返回数据对象
        const activityStats = activityStatsResponse.data
        console.log('用户活动数据:', activityStats)
        totalStudyTime.value = activityStats.totalStudyTime
        console.log('更新学习时长:', totalStudyTime.value)
      } else if (activityStatsResponse.data && activityStatsResponse.data.data) {
        // 如果数据包装在data字段中
        const activityStats = activityStatsResponse.data.data
        console.log('用户活动数据(包装):', activityStats)
        if (activityStats.totalStudyTime) {
          totalStudyTime.value = activityStats.totalStudyTime
          console.log('更新学习时长(包装):', totalStudyTime.value)
        }
      } else {
        console.warn('用户活动API响应失败或无数据:', activityStatsResponse.data)
        totalStudyTime.value = 0
        console.log('使用默认学习时长:', totalStudyTime.value)
      }
    } else {
      console.warn('没有找到用户ID，跳过用户活动API调用')
    }
    
    console.log('最终数据 - 已完成:', completedVulnerabilities.value, '学习时长:', totalStudyTime.value)
    
    // 加载分类完成率数据
    try {
      console.log('加载分类完成率数据...')
      const categoryRatesResponse = await vulnerabilityProgressApi.getCategoryCompletionRates()
      console.log('分类完成率API响应:', categoryRatesResponse)
      
      // 响应拦截器已经返回了data，所以categoryRatesResponse就是ApiResult对象
      // 检查code是否为200，或者直接使用data字段
      const responseData = categoryRatesResponse as any
      if (responseData && responseData.code === 200 && responseData.data) {
        categoryCompletionRates.value = responseData.data || {}
        console.log('更新分类完成率:', categoryCompletionRates.value)
      } else if (responseData && responseData.data) {
        // 如果code不是200但data存在，也尝试使用
        categoryCompletionRates.value = responseData.data || {}
        console.log('更新分类完成率(备用):', categoryCompletionRates.value)
      } else {
        console.warn('分类完成率API响应失败，使用默认值', categoryRatesResponse)
        categoryCompletionRates.value = {}
      }
    } catch (error) {
      console.error('获取分类完成率失败:', error)
      categoryCompletionRates.value = {}
    }
  } catch (error) {
    console.error('获取用户进度数据失败:', error)
    ElMessage.warning('获取学习进度数据失败，显示默认数据')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserProgress()
})

</script>

<style lang="scss" scoped>
.knowledge-center {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    text-align: center;
    margin-bottom: 32px;

    .header-content {
      .page-title {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        font-size: 32px;
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 12px;

        .title-icon {
          color: #409eff;
        }
      }

      .page-description {
        font-size: 16px;
        color: #7f8c8d;
        margin: 0;
      }
    }
  }

  .stats-overview {
    margin-bottom: 32px;

    .stat-card {
      border-radius: 12px;
      overflow: hidden;

      .stat-content {
        display: flex;
        align-items: center;
        padding: 20px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          background-color: #f0f9ff;
          color: #409eff;

          &.completed {
            background-color: #f0f9ff;
            color: #67c23a;
          }

          &.learning {
            background-color: #fdf6ec;
            color: #e6a23c;
          }

          &.progress {
            background-color: #f4f4f5;
            color: #909399;
          }

          .el-icon {
            font-size: 24px;
          }
        }

        .stat-info {
          flex: 1;

          .stat-number {
            font-size: 28px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: #7f8c8d;
          }
        }
      }
    }
  }

  .vulnerability-categories {
    margin-bottom: 32px;
    border-radius: 12px;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-weight: 600;
      font-size: 16px;
    }

    .category-card {
      background-color: #ffffff;
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 1px solid #e4e7ed;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        border-color: #409eff;
      }

      .category-header {
        display: flex;
        align-items: flex-start;
        margin-bottom: 16px;

        .category-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          &.critical {
            background-color: #fef0f0;
            color: #f56c6c;
          }

          &.high {
            background-color: #fdf6ec;
            color: #e6a23c;
          }

          &.medium {
            background-color: #f0f9ff;
            color: #409eff;
          }

          .el-icon {
            font-size: 20px;
          }
        }

        .category-info {
          flex: 1;

          .category-title {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
            margin: 0 0 8px 0;
          }

          .category-description {
            font-size: 14px;
            color: #7f8c8d;
            margin: 0;
            line-height: 1.5;
          }
        }
      }

      .category-stats {
        margin-bottom: 16px;

        .stat-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 8px;

          .stat-label {
            font-size: 14px;
            color: #7f8c8d;
          }

          .stat-value {
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }
        }
      }

      .category-actions {
        display: flex;
        gap: 8px;
      }
    }
  }

}

@media (max-width: 768px) {
  .knowledge-center {
    padding: 16px;

    .page-header {
      .header-content {
        .page-title {
          font-size: 24px;
        }
      }
    }

    .stats-overview {
      .stat-card {
        .stat-content {
          padding: 16px;

          .stat-icon {
            width: 48px;
            height: 48px;
            margin-right: 12px;

            .el-icon {
              font-size: 20px;
            }
          }

          .stat-info {
            .stat-number {
              font-size: 24px;
            }
          }
        }
      }
    }
  }
}
</style>
