<template>
  <div class="user-profile">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><User /></el-icon>
          {{ $t('profile.title') }}
        </h1>
        <p class="page-description">{{ $t('profile.description') }}</p>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧个人信息 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <span>{{ $t('profile.basicInfo') }}</span>
          </template>
          
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="80" :src="userInfo.avatarUrl">
                {{ userInfo.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-info">
                <h3 class="username">{{ userInfo.username }}</h3>
                <p class="user-email">{{ userInfo.email }}</p>
                <el-tag :type="getUserLevelTag(userInfo.level)">
                  {{ getUserLevelName(userInfo.level) }}
                </el-tag>
              </div>
            </div>
            
            <div class="profile-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.totalStudyTime || 0 }}</div>
                <div class="stat-label">{{ $t('profile.studyTime') }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.completedVulnerabilities || 0 }}</div>
                <div class="stat-label">{{ $t('profile.completedVulnerabilities') }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.totalPoints || 0 }}</div>
                <div class="stat-label">{{ $t('profile.totalPoints') }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 学习成就 -->
        <el-card class="achievements-card">
          <template #header>
            <span>学习成就</span>
          </template>
          
          <div class="achievements-list">
            <div 
              v-for="achievement in achievements" 
              :key="achievement.id"
              class="achievement-item"
              :class="{ 'earned': achievement.earned }"
            >
              <div class="achievement-icon">
                <el-icon><component :is="achievement.icon" /></el-icon>
              </div>
              <div class="achievement-info">
                <div class="achievement-name">{{ achievement.name }}</div>
                <div class="achievement-desc">{{ achievement.description }}</div>
              </div>
              <div class="achievement-status">
                <el-tag v-if="achievement.earned" type="success" size="small">已获得</el-tag>
                <el-tag v-else type="info" size="small">未获得</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="16">
        <!-- 学习进度 -->
        <el-card class="progress-card">
          <template #header>
            <span>学习进度</span>
          </template>
          
          <div class="progress-content">
            <div class="progress-overview">
              <div class="progress-item">
                <div class="progress-label">总体进度</div>
                <el-progress 
                  :percentage="Math.round(overallProgress)" 
                  :stroke-width="8"
                  :show-text="true"
                />
              </div>
              <div class="progress-item">
                <div class="progress-label">本周学习</div>
                <el-progress 
                  :percentage="Math.round(weeklyProgress)" 
                  :stroke-width="8"
                  :show-text="true"
                />
              </div>
            </div>
            
            <div class="category-progress">
              <h4>分类进度</h4>
              <div class="category-list">
                <div 
                  v-for="category in categoryProgress" 
                  :key="category.code"
                  class="category-item"
                >
                  <div class="category-info">
                    <span class="category-name">{{ category.name }}</span>
                    <span class="category-count">{{ category.completed }}/{{ category.total }}</span>
                  </div>
                  <el-progress 
                    :percentage="Math.round(category.percentage)" 
                    :stroke-width="6"
                    :show-text="false"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 最近活动 -->
        <el-card class="activity-card">
          <template #header>
            <span>最近活动</span>
          </template>
          
          <el-timeline>
            <el-timeline-item
              v-for="activity in recentActivities"
              :key="activity.id"
              :timestamp="formatDate(activity.timestamp)"
              :type="getActivityType(activity.type)"
            >
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-desc">{{ activity.description }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <!-- 学习统计图表 -->
        <el-card class="chart-card">
          <template #header>
            <span>学习统计</span>
          </template>
          
          <div class="chart-content">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="学习时长" name="studyTime">
                <div ref="studyTimeChart" class="chart-container"></div>
              </el-tab-pane>
              <el-tab-pane label="测试成绩" name="testScores">
                <div ref="testScoresChart" class="chart-container"></div>
              </el-tab-pane>
              <el-tab-pane label="挑战进度" name="challengeProgress">
                <div ref="challengeChart" class="chart-container"></div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { TagProps, TimelineItemProps } from 'element-plus'
import { User, Trophy, Star, Aim, Clock } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 响应式数据
const userInfo = ref({
  id: 1,
  username: 'security_learner',
  email: 'learner@example.com',
  avatarUrl: '',
  level: 'intermediate'
})

const userStats = ref({
  totalStudyTime: 0,
  completedVulnerabilities: 0,
  totalPoints: 0
})

const overallProgress = ref(0)
const weeklyProgress = ref(0)

const categoryProgress = ref([
  { code: 'sql_injection', name: 'SQL注入', completed: 3, total: 5, percentage: 60 },
  { code: 'xss', name: '跨站脚本攻击', completed: 2, total: 4, percentage: 50 },
  { code: 'csrf', name: '跨站请求伪造', completed: 1, total: 3, percentage: 33 },
  { code: 'authentication', name: '身份认证', completed: 4, total: 6, percentage: 67 }
])

const achievements = ref([
  {
    id: 1,
    name: '初学者',
    description: '完成第一个漏洞学习',
    icon: 'Star',
    earned: true
  },
  {
    id: 2,
    name: '安全达人',
    description: '完成10个漏洞学习',
    icon: 'Trophy',
    earned: true
  },
  {
    id: 3,
    name: '测试高手',
    description: '测试成绩达到90分以上',
    icon: 'Aim',
    earned: false
  },
  {
    id: 4,
    name: '挑战者',
    description: '完成5个挑战任务',
    icon: 'Clock',
    earned: false
  }
])

const recentActivities = ref([
  {
    id: 1,
    type: 'study',
    title: '学习了SQL注入漏洞',
    description: '完成了SQL注入基础知识的理论学习',
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000)
  },
  {
    id: 2,
    type: 'test',
    title: '完成了安全测试',
    description: '测试成绩：85分',
    timestamp: new Date(Date.now() - 5 * 60 * 60 * 1000)
  },
  {
    id: 3,
    type: 'challenge',
    title: '挑战任务完成',
    description: '成功完成XSS挑战任务',
    timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000)
  }
])

const activeTab = ref('studyTime')

// 生命周期
onMounted(() => {
  loadUserData()
  nextTick(() => {
    initCharts()
  })
})

// 加载用户数据
const loadUserData = async () => {
  try {
    // 这里应该调用API获取用户数据
    // 暂时使用模拟数据
    userStats.value = {
      totalStudyTime: 1200,
      completedVulnerabilities: 15,
      totalPoints: 850
    }
    
    overallProgress.value = 65
    weeklyProgress.value = 80
  } catch (error) {
    console.error('加载用户数据失败:', error)
    ElMessage.error('加载用户数据失败')
  }
}

// 获取用户等级标签类型
const getUserLevelTag = (level: string): TagProps['type'] => {
  const tagMap: Record<string, TagProps['type']> = {
    beginner: 'info',
    intermediate: 'success',
    advanced: 'warning',
    expert: 'danger'
  }
  return tagMap[level] ?? 'info'
}

// 获取用户等级名称
const getUserLevelName = (level: string) => {
  const nameMap: Record<string, string> = {
    beginner: '初学者',
    intermediate: '中级',
    advanced: '高级',
    expert: '专家'
  }
  return nameMap[level] || '未知'
}

// 获取活动类型
const getActivityType = (type: string): TimelineItemProps['type'] => {
  const typeMap: Record<string, TimelineItemProps['type']> = {
    study: 'primary',
    test: 'success',
    challenge: 'warning'
  }
  return typeMap[type] ?? 'info'
}

// 格式化日期
const formatDate = (date: Date) => {
  return date.toLocaleString()
}

// 初始化图表
const initCharts = () => {
  initStudyTimeChart()
  initTestScoresChart()
  initChallengeChart()
}

// 学习时长图表
const initStudyTimeChart = () => {
  const chartDom = document.querySelector('.chart-container')
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom as HTMLElement)
  const option = {
    title: {
      text: '最近7天学习时长',
      left: 'center'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '分钟'
    },
    series: [{
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'line',
      smooth: true,
      areaStyle: {}
    }]
  }
  myChart.setOption(option)
}

// 测试成绩图表
const initTestScoresChart = () => {
  // 类似实现
}

// 挑战进度图表
const initChallengeChart = () => {
  // 类似实现
}
</script>

<style lang="scss" scoped>
.user-profile {
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

  .profile-card {
    margin-bottom: 24px;
    
    .profile-content {
      .avatar-section {
        display: flex;
        align-items: center;
        margin-bottom: 24px;
        
        .user-info {
          margin-left: 16px;
          
          .username {
            font-size: 20px;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 4px;
          }
          
          .user-email {
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 8px;
          }
        }
      }
      
      .profile-stats {
        display: flex;
        justify-content: space-around;
        
        .stat-item {
          text-align: center;
          
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 12px;
            color: #6b7280;
          }
        }
      }
    }
  }

  .achievements-card {
    .achievements-list {
      .achievement-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f3f4f6;
        
        &:last-child {
          border-bottom: none;
        }
        
        &.earned {
          .achievement-icon {
            color: #16a34a;
          }
        }
        
        .achievement-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background: #f3f4f6;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          color: #6b7280;
        }
        
        .achievement-info {
          flex: 1;
          margin-right: 12px;
          
          .achievement-name {
            font-size: 14px;
            font-weight: 500;
            color: #1f2937;
            margin-bottom: 2px;
          }
          
          .achievement-desc {
            font-size: 12px;
            color: #6b7280;
          }
        }
      }
    }
  }

  .progress-card,
  .activity-card,
  .chart-card {
    margin-bottom: 24px;
    
    .progress-content {
      .progress-overview {
        margin-bottom: 24px;
        
        .progress-item {
          margin-bottom: 16px;
          
          .progress-label {
            font-size: 14px;
            color: #374151;
            margin-bottom: 8px;
          }
        }
      }
      
      .category-progress {
        h4 {
          font-size: 16px;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 16px;
        }
        
        .category-list {
          .category-item {
            margin-bottom: 12px;
            
            .category-info {
              display: flex;
              justify-content: space-between;
              margin-bottom: 4px;
              
              .category-name {
                font-size: 14px;
                color: #374151;
              }
              
              .category-count {
                font-size: 12px;
                color: #6b7280;
              }
            }
          }
        }
      }
    }
    
    .activity-content {
      .activity-title {
        font-size: 14px;
        font-weight: 500;
        color: #1f2937;
        margin-bottom: 4px;
      }
      
      .activity-desc {
        font-size: 12px;
        color: #6b7280;
      }
    }
    
    .chart-content {
      .chart-container {
        width: 100%;
        height: 300px;
      }
    }
  }
}
</style>
