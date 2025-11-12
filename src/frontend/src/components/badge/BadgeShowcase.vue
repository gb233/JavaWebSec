<template>
  <div class="badge-showcase">
    <div class="showcase-header">
      <h2>徽章展示</h2>
      <div class="stats">
        <span class="total-badges">总计 {{ totalBadges }} 个徽章</span>
        <span class="earned-badges">已获得 {{ earnedBadges }} 个</span>
      </div>
    </div>
    
    <!-- 视图模式切换 -->
    <div class="view-mode">
      <button 
        :class="['view-btn', { active: viewMode === 'all' }]"
        @click="viewMode = 'all'"
      >
        🌟 全部徽章
      </button>
      <button 
        :class="['view-btn', { active: viewMode === 'earned' }]"
        @click="viewMode = 'earned'"
      >
        ✅ 我的徽章
      </button>
      <button 
        :class="['view-btn', { active: viewMode === 'progress' }]"
        @click="viewMode = 'progress'"
      >
        📊 进行中
      </button>
    </div>
    
    <div class="category-tabs">
      <button 
        v-for="category in categories" 
        :key="category.code"
        :class="['tab', { active: activeCategory === category.code }]"
        @click="activeCategory = category.code"
      >
        {{ category.icon }} {{ category.name }}
      </button>
    </div>
    
    <div class="badge-grid">
      <div 
        v-for="badge in filteredBadges" 
        :key="badge.id"
        :class="['badge-card', getBadgeCardClass(badge)]"
        @click="showBadgeDetail(badge)"
      >
        <div class="badge-icon">{{ badge.badgeIcon }}</div>
        <div class="badge-name">{{ badge.badgeName }}</div>
        <div class="badge-description">{{ badge.badgeDescription }}</div>
        <div class="badge-rarity" :class="badge.badgeRarity.toLowerCase()">
          {{ getRarityText(badge.badgeRarity) }}
        </div>
        <div class="badge-points" v-if="badge.pointsReward">
          +{{ badge.pointsReward }} 积分
        </div>
        
        <!-- 徽章状态指示器 -->
        <div class="badge-status">
          <div v-if="badge.earned" class="status-earned">✓ 已获得</div>
          <div v-else-if="badge.progress" class="status-progress">
            📊 {{ Math.round(badge.progress.progressPercentage) }}%
          </div>
          <div v-else class="status-locked">🔒 未开始</div>
        </div>
        
        <!-- 进度条（仅在进行中时显示） -->
        <div v-if="badge.progress && !badge.earned" class="progress-bar">
          <div 
            class="progress-fill" 
            :style="{ width: badge.progress.progressPercentage + '%' }"
          ></div>
        </div>
      </div>
    </div>
    
    <div class="pagination" v-if="totalPages > 1">
      <button 
        v-for="page in totalPages" 
        :key="page"
        :class="['page-btn', { active: currentPage === page }]"
        @click="currentPage = page"
      >
        {{ page }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { badgeApi } from '@/api/badgeApi'
import { badgeProgressApi } from '@/api/badgeProgressApi'

interface Badge {
  id: number
  badgeCode: string
  badgeName: string
  badgeDescription: string
  badgeIcon: string
  badgeCategory: string
  badgeRarity: string
  pointsReward: number
  earned?: boolean
  progress?: BadgeProgress
}

interface BadgeProgress {
  id: number
  badgeId: number
  currentProgress: number
  targetProgress: number
  progressPercentage: number
  isCompleted: boolean
}

interface Category {
  code: string
  name: string
  icon: string
}

const badges = ref<Badge[]>([])
const userBadges = ref<number[]>([])
const badgeProgress = ref<BadgeProgress[]>([])
const activeCategory = ref('ALL')
const viewMode = ref('all')
const currentPage = ref(1)
const pageSize = 12

const categories = ref<Category[]>([
  { code: 'ALL', name: '全部', icon: '🏆' },
  { code: 'LEARNING', name: '学习类', icon: '📚' },
  { code: 'TEST', name: '测试类', icon: '📝' },
  { code: 'CHALLENGE', name: '挑战类', icon: '🏆' },
  { code: 'SPECIAL', name: '特殊类', icon: '⭐' }
])

const filteredBadges = computed(() => {
  let filtered = badges.value
  
  // 按分类筛选
  if (activeCategory.value !== 'ALL') {
    filtered = filtered.filter(badge => badge.badgeCategory === activeCategory.value)
  }
  
  // 先标记已获得的徽章和进度
  filtered = filtered.map(badge => {
    const earned = userBadges.value.includes(badge.id)
    const progress = badgeProgress.value.find(p => p.badgeId === badge.id)
    
    return {
      ...badge,
      earned,
      progress
    }
  })
  
  // 然后按视图模式筛选
  if (viewMode.value === 'earned') {
    filtered = filtered.filter(badge => badge.earned)
  } else if (viewMode.value === 'progress') {
    filtered = filtered.filter(badge => badge.progress && !badge.earned)
  }
  
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filtered.slice(start, end)
})

const totalBadges = computed(() => badges.value.length)
const earnedBadges = computed(() => userBadges.value.length)
const totalPages = computed(() => {
  let filtered = badges.value
  
  // 按分类筛选
  if (activeCategory.value !== 'ALL') {
    filtered = filtered.filter(badge => badge.badgeCategory === activeCategory.value)
  }
  
  // 按视图模式筛选
  if (viewMode.value === 'earned') {
    filtered = filtered.filter(badge => userBadges.value.includes(badge.id))
  } else if (viewMode.value === 'progress') {
    filtered = filtered.filter(badge => {
      const progress = badgeProgress.value.find(p => p.badgeId === badge.id)
      return progress && !userBadges.value.includes(badge.id)
    })
  }
  
  return Math.ceil(filtered.length / pageSize)
})

const getBadgeCardClass = (badge: Badge) => {
  if (badge.earned) return 'earned'
  if (badge.progress) return 'progress'
  return 'locked'
}

const getRarityText = (rarity: string) => {
  const rarityMap: Record<string, string> = {
    'COMMON': '普通',
    'RARE': '稀有',
    'EPIC': '史诗',
    'LEGENDARY': '传说'
  }
  return rarityMap[rarity] || rarity
}

const showBadgeDetail = (badge: Badge) => {
  // TODO: 显示徽章详情弹窗
  console.log('显示徽章详情:', badge)
}

const loadBadges = async () => {
  try {
    const response = await badgeApi.getAllBadges()
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    if (responseData && responseData.code === 200 && responseData.data) {
      badges.value = responseData.data
    } else {
      console.error('加载徽章失败:', responseData)
    }
  } catch (error) {
    console.error('加载徽章失败:', error)
  }
}

const loadUserBadges = async () => {
  try {
    const response = await badgeApi.getUserBadges()
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    if (responseData && responseData.code === 200 && responseData.data) {
      userBadges.value = responseData.data.map((ub: any) => ub.badgeId)
    } else {
      console.error('加载用户徽章失败:', responseData)
    }
  } catch (error) {
    console.error('加载用户徽章失败:', error)
  }
}

const loadBadgeProgress = async () => {
  try {
    const response = await badgeProgressApi.getUserBadgeProgressWithDetails()
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    if (responseData && responseData.code === 200 && responseData.data) {
      badgeProgress.value = responseData.data
    } else {
      console.error('加载徽章进度失败:', responseData)
    }
  } catch (error) {
    console.error('加载徽章进度失败:', error)
  }
}

onMounted(() => {
  loadBadges()
  loadUserBadges()
  loadBadgeProgress()
})
</script>

<style scoped>
.badge-showcase {
  padding: 20px;
}

.showcase-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.showcase-header h2 {
  margin: 0;
  color: #333;
}

.stats {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.view-mode {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  justify-content: center;
}

.view-btn {
  padding: 8px 16px;
  border: 2px solid #e9ecef;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  font-weight: 500;
}

.view-btn:hover {
  border-color: #007bff;
  background: #f8f9fa;
}

.view-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

.category-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: center;
}

.tab {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
}

.tab:hover {
  background: #f5f5f5;
}

.tab.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

.badge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.badge-card {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.badge-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.badge-card.earned {
  border-color: #28a745;
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
}

.badge-card.progress {
  border-color: #ffc107;
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
}

.badge-card.locked {
  border-color: #6c757d;
  background: #f8f9fa;
  opacity: 0.7;
}

.badge-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.badge-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.badge-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.4;
}

.badge-rarity {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: bold;
  margin-bottom: 8px;
}

.badge-rarity.common {
  background: #e9ecef;
  color: #495057;
}

.badge-rarity.rare {
  background: #d1ecf1;
  color: #0c5460;
}

.badge-rarity.epic {
  background: #f8d7da;
  color: #721c24;
}

.badge-rarity.legendary {
  background: #fff3cd;
  color: #856404;
}

.badge-points {
  color: #28a745;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 8px;
}

.badge-status {
  margin-bottom: 8px;
}

.status-earned {
  color: #28a745;
  font-weight: bold;
  font-size: 12px;
}

.status-progress {
  color: #ffc107;
  font-weight: bold;
  font-size: 12px;
}

.status-locked {
  color: #6c757d;
  font-size: 12px;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: #e9ecef;
  border-radius: 2px;
  overflow: hidden;
  margin-top: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffc107 0%, #ff8c00 100%);
  transition: width 0.3s ease;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 30px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover {
  background: #f5f5f5;
}

.page-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}
</style>