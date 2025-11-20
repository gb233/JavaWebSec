<template>
  <div class="badge-stats">
    <div class="stats-header">
      <h2>徽章统计</h2>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          🏆
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stats.totalBadges || 0 }}
          </div>
          <div class="stat-label">
            总徽章数
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          📚
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stats.learningBadges || 0 }}
          </div>
          <div class="stat-label">
            学习类徽章
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          📝
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stats.testBadges || 0 }}
          </div>
          <div class="stat-label">
            测试类徽章
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          🏆
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stats.challengeBadges || 0 }}
          </div>
          <div class="stat-label">
            挑战类徽章
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          ⭐
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stats.specialBadges || 0 }}
          </div>
          <div class="stat-label">
            特殊类徽章
          </div>
        </div>
      </div>
    </div>

    <div v-if="recentBadges.length > 0" class="recent-badges">
      <h3>最近获得的徽章</h3>
      <div class="recent-list">
        <div
          v-for="badge in recentBadges"
          :key="badge.id"
          class="recent-item"
        >
          <div class="badge-icon">
            {{ badge.badgeIcon }}
          </div>
          <div class="badge-info">
            <div class="badge-name">
              {{ badge.badgeName }}
            </div>
            <div class="earned-time">
              {{ formatTime(badge.earnedAt) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="progressStats" class="progress-summary">
      <h3>进度概览</h3>
      <div class="progress-cards">
        <div class="progress-card">
          <div class="progress-label">
            进行中
          </div>
          <div class="progress-value">
            {{ progressStats.totalProgress || 0 }}
          </div>
        </div>
        <div class="progress-card">
          <div class="progress-label">
            已完成
          </div>
          <div class="progress-value">
            {{ progressStats.completedProgress || 0 }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { badgeApi } from '@/api/badgeApi'

interface BadgeStats {
  totalBadges: number
  learningBadges: number
  testBadges: number
  challengeBadges: number
  specialBadges: number
  recentBadges: any[]
  progressStats: any
}

interface RecentBadge {
  id: number
  userId: number
  badgeId: number
  earnedAt: string
  isDisplayed: boolean
  createdAt: string
  badgeCode: string
  badgeName: string
  badgeDescription: string
  badgeIcon: string
  badgeCategory: string
  badgeRarity: string
  pointsReward: number
}

const stats = ref<BadgeStats>({
  totalBadges: 0,
  learningBadges: 0,
  testBadges: 0,
  challengeBadges: 0,
  specialBadges: 0,
  recentBadges: [],
  progressStats: null
})

const recentBadges = ref<RecentBadge[]>([])
const progressStats = ref<any>(null)

const formatTime = (timeString: string) => {
  const time = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - time.getTime()

  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    return `${days}天前`
  }
}

const loadStats = async () => {
  try {
    const response = await badgeApi.getUserBadgeStats()
    if (response.success) {
      stats.value = response.data
      recentBadges.value = response.data.recentBadges || []
      progressStats.value = response.data.progressStats
    }
  } catch (error) {
    console.error('加载徽章统计失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.badge-stats {
  padding: 20px;
}

.stats-header h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.recent-badges {
  margin-bottom: 30px;
}

.recent-badges h3 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 16px;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recent-item {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.badge-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.badge-info {
  flex: 1;
}

.badge-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.earned-time {
  font-size: 12px;
  color: #666;
}

.progress-summary h3 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 16px;
}

.progress-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.progress-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  text-align: center;
}

.progress-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.progress-value {
  font-size: 20px;
  font-weight: 700;
  color: #007bff;
}
</style>
