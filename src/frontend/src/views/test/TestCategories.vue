<template>
  <div class="test-categories">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Document /></el-icon>
          {{ $t('test.title') }}
        </h1>
        <p class="page-description">{{ $t('test.description') }}</p>
      </div>
    </div>

    <!-- 测试模式选择 -->
    <div class="test-mode-selector">
      <div class="mode-selector-header">
        <h2 class="mode-title">{{ $t('test.selectMode') }}</h2>
        <p class="mode-description">{{ $t('test.selectModeDescription') }}</p>
      </div>
      <div class="mode-options">
        <el-radio-group v-model="selectedMode" @change="onModeChange" class="mode-radio-group">
          <el-radio-button label="realtime" class="mode-radio">
            <div class="mode-option">
              <el-icon class="mode-icon"><Clock /></el-icon>
              <div class="mode-info">
                <div class="mode-name">{{ $t('test.realtimeMode') }}</div>
                <div class="mode-desc">{{ $t('test.realtimeModeDesc') }}</div>
              </div>
            </div>
          </el-radio-button>
          <el-radio-button label="exam" class="mode-radio">
            <div class="mode-option">
              <el-icon class="mode-icon"><Trophy /></el-icon>
              <div class="mode-info">
                <div class="mode-name">{{ $t('test.examMode') }}</div>
                <div class="mode-desc">{{ $t('test.examModeDesc') }}</div>
              </div>
            </div>
          </el-radio-button>
          <el-radio-button label="random" class="mode-radio">
            <div class="mode-option">
              <el-icon class="mode-icon"><QuestionFilled /></el-icon>
              <div class="mode-info">
                <div class="mode-name">{{ $t('test.randomMode') }}</div>
                <div class="mode-desc">{{ $t('test.randomModeDesc') }}</div>
              </div>
            </div>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon total">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.totalTests || 0 }}</div>
                <div class="stat-label">{{ $t('test.totalTests') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon passed">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.passedTests || 0 }}</div>
                <div class="stat-label">{{ $t('test.passedTests') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon average">
                <el-icon><DataLine /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ Math.round((userStats as any).averageAccuracy || 0) }}%</div>
                <div class="stat-label">{{ $t('test.averageScore') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon best">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ Math.round((userStats as any).averageAccuracy || 0) }}%</div>
                <div class="stat-label">{{ $t('test.averageGrade') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 测试分类 -->
    <div class="categories-section">
      <h2 class="section-title">{{ $t('test.selectCategory') }}</h2>
      <div class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.code"
          class="category-card"
          @click="startTest(category)"
        >
          <div class="category-icon">
            <el-icon><component :is="category.icon" /></el-icon>
          </div>
          <div class="category-content">
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-description">{{ category.description }}</p>
            <div class="category-stats">
              <span class="stat-item">
                <el-icon><QuestionFilled /></el-icon>
                {{ category.questionCount }} 题
              </span>
              <span class="stat-item">
                <el-icon><Clock /></el-icon>
                {{ category.timeLimit }} 分钟
              </span>
            </div>
          </div>
          <div class="category-action">
            <el-button type="primary" size="large" :loading="loading">
              {{ $t('test.startTest') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近测试记录 -->
    <div class="recent-tests">
      <div class="section-header">
        <h2 class="section-title">最近测试记录</h2>
        <el-button text @click="viewAllRecords">查看全部</el-button>
      </div>
      <el-table :data="recentRecords" style="width: 100%">
        <el-table-column label="测试名称">
          <template #default="{ row }">
            {{ getTestName(row) }}
          </template>
        </el-table-column>
        <el-table-column label="分类">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryCode) }}
          </template>
        </el-table-column>
        <el-table-column prop="completionRate" label="分数">
          <template #default="{ row }">
            <el-tag :type="row.completionRate >= 60 ? 'success' : 'danger'">
              {{ Math.round(row.completionRate || 0) }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="timeSpent" label="用时">
          <template #default="{ row }">
            {{ formatTime(row.timeSpent || 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="startedAt" label="测试时间">
          <template #default="{ row }">
            {{ formatDate(row.startedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button text @click="viewTestDetail(row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, Check, DataLine, Trophy, QuestionFilled, Clock } from '@element-plus/icons-vue'
import testApi, {
  type UserTestRecord,
  type TestStats
} from '@/api/test'
import { useTestStore } from '@/stores/modules/test'
import { isSuccessResponse } from '@/utils/api-helpers'

const router = useRouter()
const route = useRoute()
const testStore = useTestStore()

// 响应式数据
const loading = ref(false)
const selectedMode = ref(route.query.mode as string || 'realtime') // 从路由参数获取模式，默认为实时反馈模式
const defaultStats: TestStats = {
  totalTests: 0,
  passedTests: 0,
  averageScore: 0,
  totalTimeSpent: 0,
  categoryStats: {},
  recentTests: []
}
const userStats = ref<TestStats>({ ...defaultStats })
const recentRecords = ref<UserTestRecord[]>([])

// 测试模式切换处理
const onModeChange = (mode: string | number | boolean | undefined) => {
  if (typeof mode === 'string') {
    selectedMode.value = mode
    ElMessage.success(`已切换到${getModeName(mode)}`)
  }
}

// 获取模式名称
const getModeName = (mode: string) => {
  const modeNames: Record<string, string> = {
    'realtime': '实时反馈模式',
    'exam': '考试模式',
    'random': '随机综合模式'
  }
  return modeNames[mode] || mode
}

// 测试分类数据
const categories = ref([
  {
    code: 'A01',
    name: '越权访问',
    description: '测试越权访问漏洞相关知识',
    icon: 'Warning',
    questionCount: 38,
    timeLimit: 30
  },
  {
    code: 'A02',
    name: '加密失败',
    description: '测试加密失败漏洞相关知识',
    icon: 'Lock',
    questionCount: 63,
    timeLimit: 30
  },
  {
    code: 'A03',
    name: '注入漏洞',
    description: '测试注入漏洞相关知识',
    icon: 'Warning',
    questionCount: 50,
    timeLimit: 30
  },
  {
    code: 'A04',
    name: '不安全设计',
    description: '测试不安全设计漏洞相关知识',
    icon: 'Document',
    questionCount: 50,
    timeLimit: 30
  },
  {
    code: 'A05',
    name: '安全配置错误',
    description: '测试安全配置错误相关知识',
    icon: 'Setting',
    questionCount: 30,
    timeLimit: 30
  },
  {
    code: 'A06',
    name: '易受攻击组件',
    description: '测试易受攻击组件相关知识',
    icon: 'Box',
    questionCount: 30,
    timeLimit: 30
  },
  {
    code: 'A07',
    name: '身份识别失败',
    description: '测试身份识别失败相关知识',
    icon: 'User',
    questionCount: 25,
    timeLimit: 30
  },
  {
    code: 'A08',
    name: '软件和数据完整性失败',
    description: '测试软件和数据完整性失败相关知识',
    icon: 'Document',
    questionCount: 25,
    timeLimit: 30
  },
  {
    code: 'A09',
    name: '安全日志和监控失败',
    description: '测试安全日志和监控失败相关知识',
    icon: 'Document',
    questionCount: 25,
    timeLimit: 30
  },
  {
    code: 'A10',
    name: '服务端请求伪造',
    description: '测试服务端请求伪造相关知识',
    icon: 'Connection',
    questionCount: 20,
    timeLimit: 30
  }
])

// 生命周期
onMounted(() => {
  loadUserStats()
  loadRecentRecords()
  loadQuestionStatistics() // 加载题目统计
})

// 加载题目统计
const loadQuestionStatistics = async () => {
  try {
    // 为每个分类加载题目统计
    const statisticsPromises = categories.value.map(async (category) => {
      try {
        const response = await testApi.getCategoryStatistics(category.code)
        if (isSuccessResponse(response) && response.data) {
          const totalCount = response.data.totalCount || 0
          category.questionCount = totalCount
          console.log(`${category.code} 题目数量: ${totalCount}`)
        }
      } catch (error) {
        console.error(`加载${category.code}题目统计失败:`, error)
        // 保持默认值
      }
    })
    
    await Promise.all(statisticsPromises)
  } catch (error) {
    console.error('加载题目统计失败:', error)
  }
}

// 加载用户统计
const loadUserStats = async () => {
  try {
    const response = await testApi.getUserTestStatistics()
    if (isSuccessResponse(response) && response.data) {
      userStats.value = response.data
    } else {
      userStats.value = { ...defaultStats }
    }
  } catch (error) {
    console.error('加载用户统计失败:', error)
    userStats.value = { ...defaultStats }
  }
}

// 加载最近测试记录
const loadRecentRecords = async () => {
  try {
    const response = await testApi.getUserTestRecords({ page: 0, size: 5 })
    if (isSuccessResponse(response) && response.data) {
      recentRecords.value = Array.isArray(response.data.content) ? response.data.content : []
    } else {
      recentRecords.value = []
    }
  } catch (error) {
    console.error('加载最近测试记录失败:', error)
    recentRecords.value = []
  }
}

// 开始测试
const startTest = async (category: any) => {
  try {
    loading.value = true
    // 使用当前选择的测试模式
    const modeCode = selectedMode.value
    const response = await testApi.startTestSession(modeCode, category.code)

    if (isSuccessResponse(response) && response.data) {
      const session = response.data
      if (session) {
        // 获取测试题目
        const questionsResponse = await testApi.getTestQuestions(session.sessionCode)
        if (isSuccessResponse(questionsResponse) && questionsResponse.data) {
          testStore.setCurrentSession(session, questionsResponse.data)
          ElMessage.success('测试已开始')
          router.push({
            name: 'TestExam',
            params: { 
              categoryId: category.code
            },
            query: {
              mode: modeCode
            }
          })
        } else {
          ElMessage.error('获取测试题目失败')
        }
      } else {
        ElMessage.error('返回的测试数据不完整')
      }
    } else {
      ElMessage.error(response?.message || '开始测试失败')
    }
  } catch (error: any) {
    console.error('开始测试失败:', error)
    ElMessage.error(error.message || '开始测试失败')
  } finally {
    loading.value = false
  }
}

// 查看测试详情
const viewTestDetail = (testRecordId: number) => {
  router.push({
    name: 'TestResult',
    params: { recordId: testRecordId.toString() }
  })
}

// 查看所有记录
const viewAllRecords = () => {
  router.push({ name: 'TestRecords' })
}

// 格式化时间
const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '未知时间'
  try {
    // 处理后端返回的日期格式 "2025-10-01 22:54:14"
    const date = new Date(dateString.replace(' ', 'T'))
    if (isNaN(date.getTime())) {
      return '无效时间'
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '格式错误'
  }
}

// 获取测试名称
const getTestName = (record: UserTestRecord) => {
  const categoryName = getCategoryName(record.categoryCode || '')
  const modeName = getModeName(record.modeCode || 'realtime')
  return `${categoryName} - ${modeName}`
}

// 获取分类名称
const getCategoryName = (categoryCode: string) => {
  if (!categoryCode) return '未知分类'
  const category = categories.value.find(cat => cat.code === categoryCode)
  return category ? category.name : categoryCode
}
</script>

<style lang="scss" scoped>
.test-categories {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    margin-bottom: 32px;
    
    .header-content {
      text-align: center;
      
      .page-title {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 32px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
        
        .title-icon {
          margin-right: 12px;
          color: #3b82f6;
        }
      }
      
      .page-description {
        font-size: 16px;
        color: #6b7280;
        margin: 0;
      }
    }
  }

  .test-mode-selector {
    margin-bottom: 32px;
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .mode-selector-header {
      text-align: center;
      margin-bottom: 24px;

      .mode-title {
        font-size: 20px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
      }

      .mode-description {
        font-size: 14px;
        color: #6b7280;
        margin: 0;
      }
    }

    .mode-options {
      .mode-radio-group {
        display: flex;
        gap: 16px;
        justify-content: center;

        .mode-radio {
          flex: 1;
          max-width: 300px;

          .mode-option {
            display: flex;
            align-items: center;
            padding: 16px;
            text-align: left;

            .mode-icon {
              font-size: 24px;
              margin-right: 12px;
              color: #3b82f6;
            }

            .mode-info {
              .mode-name {
                font-size: 16px;
                font-weight: 600;
                color: #1f2937;
                margin-bottom: 4px;
              }

              .mode-desc {
                font-size: 12px;
                color: #6b7280;
                line-height: 1.4;
              }
            }
          }
        }
      }
    }
  }

  .stats-cards {
    margin-bottom: 40px;
    
    .stat-card {
      border: none;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      
      .stat-content {
        display: flex;
        align-items: center;
        
        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          
          &.total {
            background: #dbeafe;
            color: #3b82f6;
          }
          
          &.passed {
            background: #dcfce7;
            color: #16a34a;
          }
          
          &.average {
            background: #fef3c7;
            color: #d97706;
          }
          
          &.best {
            background: #fce7f3;
            color: #be185d;
          }
        }
        
        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #1f2937;
            line-height: 1;
          }
          
          .stat-label {
            font-size: 14px;
            color: #6b7280;
            margin-top: 4px;
          }
        }
      }
    }
  }

  .categories-section {
    margin-bottom: 40px;
    
    .section-title {
      font-size: 24px;
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 24px;
    }
    
    .categories-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
      gap: 24px;
      
      .category-card {
        background: white;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        cursor: pointer;
        transition: all 0.3s ease;
        border: 2px solid transparent;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          border-color: #3b82f6;
        }
        
        .category-icon {
          width: 48px;
          height: 48px;
          background: #dbeafe;
          color: #3b82f6;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 16px;
          font-size: 20px;
        }
        
        .category-content {
          margin-bottom: 20px;
          
          .category-name {
            font-size: 18px;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 8px;
          }
          
          .category-description {
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 12px;
          }
          
          .category-stats {
            display: flex;
            gap: 16px;
            
            .stat-item {
              display: flex;
              align-items: center;
              font-size: 12px;
              color: #6b7280;
              
              .el-icon {
                margin-right: 4px;
              }
            }
          }
        }
        
        .category-action {
          text-align: center;
        }
      }
    }
  }

  .recent-tests {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      .section-title {
        font-size: 20px;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
      }
    }
  }
}
</style>
