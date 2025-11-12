<template>
  <div class="badge-progress">
    <div class="progress-header">
      <h2>徽章进度</h2>
      <div class="stats">
        <span>已完成 {{ completedCount }} / {{ totalCount }}</span>
      </div>
    </div>
    
    <div class="category-filter">
      <button 
        v-for="category in categories" 
        :key="category.code"
        :class="['filter-btn', { active: activeCategory === category.code }]"
        @click="activeCategory = category.code"
      >
        {{ category.icon }} {{ category.name }}
      </button>
    </div>
    
    <div class="progress-list">
      <div 
        v-for="progress in filteredProgress" 
        :key="progress.id"
        class="progress-item"
      >
        <div class="progress-info">
          <div class="badge-icon">{{ progress.badgeIcon }}</div>
          <div class="badge-details">
            <div class="badge-name">{{ progress.badgeName }}</div>
            <div class="progress-text">
              {{ progress.currentProgress }} / {{ progress.targetProgress }}
            </div>
          </div>
        </div>
        
        <div class="progress-bar">
          <div 
            class="progress-fill" 
            :style="{ width: progress.progressPercentage + '%' }"
          ></div>
        </div>
        
        <div class="progress-percentage">
          {{ Math.round(progress.progressPercentage) }}%
        </div>
        
        <div class="progress-status" v-if="progress.isCompleted">
          <span class="completed">✓ 已完成</span>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-if="filteredProgress.length === 0">
      <div class="empty-icon">📊</div>
      <div class="empty-text">暂无徽章进度</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { badgeProgressApi } from '@/api/badgeProgressApi'

interface BadgeProgress {
  id: number
  userId: number
  badgeId: number
  currentProgress: number
  targetProgress: number
  progressPercentage: number
  isCompleted: boolean
  badgeCode?: string
  badgeName?: string
  badgeDescription?: string
  badgeIcon?: string
  badgeCategory?: string
  badgeRarity?: string
  pointsReward?: number
}

interface Category {
  code: string
  name: string
  icon: string
}

const progressList = ref<BadgeProgress[]>([])
const activeCategory = ref('ALL')

const categories = ref<Category[]>([
  { code: 'ALL', name: '全部', icon: '📊' },
  { code: 'LEARNING', name: '学习类', icon: '📚' },
  { code: 'TEST', name: '测试类', icon: '📝' },
  { code: 'CHALLENGE', name: '挑战类', icon: '🏆' },
  { code: 'SPECIAL', name: '特殊类', icon: '⭐' }
])

const filteredProgress = computed(() => {
  let filtered = progressList.value
  
  if (activeCategory.value !== 'ALL') {
    filtered = filtered.filter(progress => progress.badgeCategory === activeCategory.value)
  }
  
  return filtered
})

const totalCount = computed(() => progressList.value.length)
const completedCount = computed(() => 
  progressList.value.filter(progress => progress.isCompleted).length
)

const loadProgress = async () => {
  try {
    const response = await badgeProgressApi.getUserBadgeProgressWithDetails()
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    if (responseData && responseData.code === 200 && responseData.data) {
      progressList.value = responseData.data
    } else {
      console.error('加载徽章进度失败:', responseData)
    }
  } catch (error) {
    console.error('加载徽章进度失败:', error)
  }
}

onMounted(() => {
  loadProgress()
})
</script>

<style scoped>
.badge-progress {
  padding: 20px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-header h2 {
  margin: 0;
  color: #333;
}

.stats {
  color: #666;
  font-size: 14px;
}

.category-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
}

.filter-btn:hover {
  background: #f5f5f5;
}

.filter-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.progress-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-item {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
}

.badge-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.badge-details {
  flex: 1;
}

.badge-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.progress-text {
  font-size: 12px;
  color: #666;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  min-width: 100px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #007bff, #0056b3);
  transition: width 0.3s ease;
}

.progress-percentage {
  min-width: 40px;
  text-align: right;
  font-size: 12px;
  color: #666;
  font-weight: 600;
}

.progress-status {
  min-width: 80px;
  text-align: center;
}

.completed {
  color: #28a745;
  font-weight: 600;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
}
</style>
