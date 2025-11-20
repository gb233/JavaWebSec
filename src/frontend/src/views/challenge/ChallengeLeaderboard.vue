<!--
// 排行榜功能暂时注释掉 - 2025-01-15
<template>
  <div class="challenge-leaderboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <ElIcon class="header-icon" size="24">
          <Trophy />
        </ElIcon>
        <div class="header-text">
          <h1 class="page-title">挑战排行榜</h1>
          <p class="page-subtitle">看看谁是最强的安全挑战者</p>
        </div>
      </div>
    </div>

    <!-- 我的统计 -->
    <div class="my-stats-section">
      <ElCard class="stats-card">
        <template #header>
          <div class="card-header">
            <ElIcon><User /></ElIcon>
            <span>我的统计</span>
          </div>
        </template>

        <div class="stats-grid" v-loading="myStatsLoading">
          <div class="stat-item">
            <div class="stat-value">{{ myStats?.totalChallenges || 0 }}
</div>

            <div class="stat-label">
总挑战次数
</div>

          </div>
          <div class="stat-item">
            <div class="stat-value">{{ myStats?.successCount || 0 }}
</div>

            <div class="stat-label">
成功次数
</div>

          </div>
          <div class="stat-item">
            <div class="stat-value">{{ myStats?.totalScore || 0 }}
</div>

            <div class="stat-label">
总积分
</div>

          </div>
          <div class="stat-item">
            <div class="stat-value">{{ myStats?.successRatePercent || '0%' }}
</div>

            <div class="stat-label">
成功率
</div>
          </div>
        </div>
      </ElCard>
    </div>

    <!-- 排行榜 -->
    <div class="leaderboard-section">
      <ElCard class="leaderboard-card">
        <template #header>
          <div class="card-header">
            <ElIcon><Trophy /></ElIcon>
            <span>排行榜</span>
            <div class="header-actions">
              <ElSelect
                v-model="selectedCategory"
                placeholder="选择分类"
                @change="handleCategoryChange"
                style="width: 120px; margin-right: 10px;"
              >
                <ElOption label="全部" value="all" />
                <ElOption label="A01" value="A01" />
                <ElOption label="A02" value="A02" />
                <ElOption label="A03" value="A03" />
                <ElOption label="A04" value="A04" />
                <ElOption label="A05" value="A05" />
                <ElOption label="A06" value="A06" />
                <ElOption label="A07" value="A07" />
                <ElOption label="A08" value="A08" />
                <ElOption label="A09" value="A09" />
                <ElOption label="A10" value="A10" />
              </ElSelect>
              <ElButton @click="refreshLeaderboard" :loading="refreshing">
                <ElIcon><Refresh /></ElIcon>
                刷新
              </ElButton>
            </div>
          </div>
        </template>

        <div class="leaderboard-content" v-loading="leaderboardLoading">
          <!-- 排行榜表格 -->
          <ElTable
            :data="leaderboardData"
            stripe
            style="width: 100%"
            empty-text="暂无排行榜数据"
          >
            <ElTableColumn prop="rank" label="排名" width="80" align="center">
              <template #default="{ row, $index }">
                <div class="rank-cell">
                  <span v-if="row.rank" class="rank-number">{{ row.rank }}</span>
                  <span v-else class="rank-number">{{ $index + 1 }}</span>
                </div>
              </template>
            </ElTableColumn>

            <ElTableColumn prop="displayName" label="用户" min-width="120">
              <template #default="{ row }">
                <div class="user-cell">
                  <ElAvatar
                    :src="row.avatarUrl"
                    :size="32"
                    class="user-avatar"
                  >
                    {{ row.username?.charAt(0)?.toUpperCase() }}
                  </ElAvatar>
                  <div class="user-info">
                    <div class="username">{{ row.displayName || row.username }}</div>
                    <div class="user-id">ID: {{ row.userId }}</div>
                  </div>
                </div>
              </template>
            </ElTableColumn>

            <ElTableColumn prop="totalScore" label="总积分" width="100" align="center">
              <template #default="{ row }">
                <ElTag type="primary" size="small">{{ row.totalScore }}</ElTag>
              </template>
            </ElTableColumn>

            <ElTableColumn prop="successRate" label="成功率" width="100" align="center">
              <template #default="{ row }">
                <ElTag
                  :type="getSuccessRateType(row.successRate)"
                  size="small"
                >
                  {{ row.successRatePercent || (row.successRate + '%') }}
                </ElTag>
              </template>
            </ElTableColumn>

            <ElTableColumn prop="totalChallenges" label="挑战次数" width="100" align="center">
              <template #default="{ row }">
                <span class="challenge-count">{{ row.totalChallenges }}</span>
              </template>
            </ElTableColumn>

            <ElTableColumn prop="lastChallengeAt" label="最后挑战" width="120" align="center">
              <template #default="{ row }">
                <span v-if="row.lastChallengeAt" class="last-challenge">
                  {{ formatDate(row.lastChallengeAt) }}
                </span>
                <span v-else class="no-challenge">-</span>
              </template>
            </ElTableColumn>
          </ElTable>

          <!-- 分页 -->
          <div class="pagination-container" v-if="totalPages > 1">
            <ElPagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="totalElements"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Trophy, User, Refresh } from '@element-plus/icons-vue'
import { leaderboardApi, type LeaderboardEntry, type UserChallengeStats } from '@/api/leaderboardApi'

// 响应式数据
const selectedCategory = ref('all')
const currentPage = ref(1)
const pageSize = ref(20)
const leaderboardData = ref<LeaderboardEntry[]>([])
const myStats = ref<UserChallengeStats | null>(null)
const leaderboardLoading = ref(false)
const myStatsLoading = ref(false)
const refreshing = ref(false)

// 计算属性
const totalElements = ref(0)
const totalPages = computed(() => Math.ceil(totalElements.value / pageSize.value))

// 获取我的统计
const loadMyStats = async () => {
  try {
    myStatsLoading.value = true
    const response = await leaderboardApi.getMyStats(selectedCategory.value === 'all' ? undefined : selectedCategory.value)
    if (response.success) {
      myStats.value = response.data
    }
  } catch (error: any) {
    console.error('获取我的统计失败:', error)
    ElMessage.error('获取我的统计失败')
  } finally {
    myStatsLoading.value = false
  }
}

// 获取排行榜数据
const loadLeaderboard = async () => {
  try {
    leaderboardLoading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: 'totalScore',
      sortDir: 'desc' as const
    }

    let response
    if (selectedCategory.value === 'all') {
      response = await leaderboardApi.getOverallLeaderboard(params)
    } else {
      response = await leaderboardApi.getCategoryLeaderboard(selectedCategory.value, params)
    }

    if (response.success && response.data) {
      leaderboardData.value = response.data.content || []
      totalElements.value = response.data.totalElements || 0
    }
  } catch (error: any) {
    console.error('获取排行榜失败:', error)
    ElMessage.error('获取排行榜失败')
  } finally {
    leaderboardLoading.value = false
  }
}

// 刷新排行榜
const refreshLeaderboard = async () => {
  try {
    refreshing.value = true
    await leaderboardApi.refreshLeaderboard(selectedCategory.value === 'all' ? undefined : selectedCategory.value)
    ElMessage.success('排行榜已刷新')
    await loadLeaderboard()
    await loadMyStats()
  } catch (error: any) {
    console.error('刷新排行榜失败:', error)
    ElMessage.error('刷新排行榜失败')
  } finally {
    refreshing.value = false
  }
}

// 处理分类变化
const handleCategoryChange = () => {
  currentPage.value = 1
  loadLeaderboard()
  loadMyStats()
}

// 处理页面大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadLeaderboard()
}

// 处理页面变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadLeaderboard()
}

// 获取成功率标签类型
const getSuccessRateType = (successRate: number) => {
  if (successRate >= 80) return 'success'
  if (successRate >= 60) return 'warning'
  return 'danger'
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadMyStats()
  loadLeaderboard()
})
</script>

<style scoped>
.challenge-leaderboard {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  color: #409eff;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #909399;
}

.my-stats-section {
  margin-bottom: 24px;
}

.stats-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.leaderboard-section {
  margin-bottom: 24px;
}

.leaderboard-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rank-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.rank-number {
  font-weight: 600;
  font-size: 16px;
  color: #409eff;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.username {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.user-id {
  font-size: 12px;
  color: #909399;
}

.challenge-count {
  font-weight: 500;
  color: #606266;
}

.last-challenge {
  font-size: 12px;
  color: #909399;
}

.no-challenge {
  color: #c0c4cc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .challenge-leaderboard {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .header-actions .el-select,
  .header-actions .el-button {
    width: 100%;
  }
}
</style>
-->