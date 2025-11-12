<template>
  <div class="test-records">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Document /></el-icon>
          测试记录
        </h1>
        <p class="page-description">查看您的所有测试记录和成绩</p>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="分类">
            <el-select 
              v-model="filterForm.categoryCode" 
              placeholder="选择分类" 
              clearable
              style="width: 200px"
            >
              <el-option label="全部" value="" />
              <el-option label="越权访问" value="A01" />
              <el-option label="加密失败" value="A02" />
              <el-option label="注入攻击" value="A03" />
              <el-option label="不安全设计" value="A04" />
              <el-option label="安全配置错误" value="A05" />
              <el-option label="易受攻击组件" value="A06" />
              <el-option label="身份认证失败" value="A07" />
              <el-option label="软件和数据完整性故障" value="A08" />
              <el-option label="安全日志记录和监控失败" value="A09" />
              <el-option label="服务端请求伪造" value="A10" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="filterForm.status" 
              placeholder="选择状态" 
              clearable
              style="width: 150px"
            >
              <el-option label="全部" value="" />
              <el-option label="通过" value="passed" />
              <el-option label="未通过" value="failed" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadRecords" :loading="loading">
              <el-icon><Search /></el-icon>
              筛选
            </el-button>
            <el-button @click="resetFilter">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 测试记录列表 -->
    <div class="records-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <span class="card-title">测试记录</span>
              <div v-if="currentFilters.length > 0" class="filter-tags">
                <el-tag 
                  v-for="(filter, index) in currentFilters" 
                  :key="index"
                  size="small"
                  type="info"
                  closable
                  @close="clearFilter(index)"
                >
                  {{ filter }}
                </el-tag>
              </div>
            </div>
            <el-button text @click="loadRecords" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>

        <el-table :data="records" v-loading="loading" style="width: 100%">
          <el-table-column label="测试名称" width="200">
            <template #default="{ row }">
              {{ getTestName(row) }}
            </template>
          </el-table-column>
          <el-table-column label="分类" width="120">
            <template #default="{ row }">
              {{ getCategoryName(row.categoryCode) }}
            </template>
          </el-table-column>
          <el-table-column label="分数" width="100">
            <template #default="{ row }">
              <el-tag :type="getScoreTagType(row.completionRate)">
                {{ Math.round(row.completionRate || 0) }}%
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="正确" width="80">
            <template #default="{ row }">
              {{ row.correctCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="错误" width="80">
            <template #default="{ row }">
              {{ (row.totalQuestions || 0) - (row.correctCount || 0) }}
            </template>
          </el-table-column>
          <el-table-column label="用时" width="120">
            <template #default="{ row }">
              {{ formatTime(row.timeSpent) }}
            </template>
          </el-table-column>
          <el-table-column label="测试时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.startedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button text @click="viewDetail(row.id)">查看详情</el-button>
              <el-button 
                text 
                @click="retakeTest(row)" 
                v-if="(row.completionRate || 0) < 60"
              >
                重测
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, Refresh, Search } from '@element-plus/icons-vue'
import testApi, { type UserTestRecord } from '@/api/test'
import { isSuccessResponse } from '@/utils/api-helpers'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const records = ref<UserTestRecord[]>([])

// 筛选表单
const filterForm = reactive({
  categoryCode: '',
  status: ''
})

// 当前筛选状态显示
const currentFilters = computed(() => {
  const filters = []
  if (filterForm.categoryCode) {
    filters.push(`分类: ${getCategoryName(filterForm.categoryCode)}`)
  }
  if (filterForm.status) {
    const statusText = filterForm.status === 'passed' ? '通过' : '未通过'
    filters.push(`状态: ${statusText}`)
  }
  return filters
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 生命周期
onMounted(() => {
  loadRecords()
})

// 加载测试记录
const loadRecords = async () => {
  try {
    loading.value = true
    const params = {
      categoryCode: filterForm.categoryCode || undefined,
      page: pagination.page - 1,
      size: pagination.size
    }
    
    const response = await testApi.getUserTestRecords(params)
    if (isSuccessResponse(response) && response.data) {
      const pageData = response.data
      records.value = Array.isArray(pageData.content) ? [...pageData.content] : []
      pagination.total = pageData.totalElements ?? records.value.length
      
      // 应用状态筛选
      if (filterForm.status) {
        records.value = records.value.filter(record => {
          if (filterForm.status === 'passed') {
            return (record.completionRate || 0) >= 60
          } else if (filterForm.status === 'failed') {
            return (record.completionRate || 0) < 60
          }
          return true
        })
        pagination.total = records.value.length
      }
    } else {
      ElMessage.error(response?.message || '加载测试记录失败')
    }
  } catch (error) {
    console.error('加载测试记录失败:', error)
    ElMessage.error('加载测试记录失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  filterForm.categoryCode = ''
  filterForm.status = ''
  pagination.page = 1
  loadRecords()
}

// 清除单个筛选条件
const clearFilter = (index: number) => {
  if (index === 0 && filterForm.categoryCode) {
    filterForm.categoryCode = ''
  } else if (index === 1 && filterForm.status) {
    filterForm.status = ''
  } else if (filterForm.categoryCode && !filterForm.status) {
    filterForm.categoryCode = ''
  } else if (!filterForm.categoryCode && filterForm.status) {
    filterForm.status = ''
  }
  pagination.page = 1
  loadRecords()
}

// 查看详情
const viewDetail = (recordId: number) => {
  router.push({
    name: 'TestResult',
    params: { recordId: recordId.toString() }
  })
}

// 重新测试
const retakeTest = (record: UserTestRecord) => {
  router.push({
    name: 'TestCategories'
  })
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
  const categoryMap: Record<string, string> = {
    'A01': '越权访问',
    'A02': '加密失败',
    'A03': '注入攻击',
    'A04': '不安全设计',
    'A05': '安全配置错误',
    'A06': '易受攻击组件',
    'A07': '身份认证失败',
    'A08': '软件和数据完整性故障',
    'A09': '安全日志记录和监控失败',
    'A10': '服务端请求伪造'
  }
  return categoryMap[categoryCode] || categoryCode
}

// 获取模式名称
const getModeName = (modeCode: string) => {
  const modeMap: Record<string, string> = {
    'realtime': '实时反馈',
    'exam': '考试模式',
    'random': '随机综合'
  }
  return modeMap[modeCode] || modeCode
}

// 获取分数标签类型
const getScoreTagType = (score: number) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadRecords()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadRecords()
}

// 格式化时间
const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}
</script>

<style lang="scss" scoped>
.test-records {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      text-align: center;
      
      .page-title {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
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

  .filter-section {
    margin-bottom: 24px;
  }

  .records-section {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .card-title {
          font-weight: 600;
          font-size: 16px;
          color: #303133;
        }
        
        .filter-tags {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
      }
    }

    .pagination-wrapper {
      margin-top: 24px;
      text-align: center;
    }
  }
}
</style>
