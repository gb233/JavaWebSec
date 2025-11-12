<template>
  <div class="test-mode-selector">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Document /></el-icon>
          知识测试
        </h1>
        <p class="page-description">选择适合您的测试模式，开始安全知识测试</p>
      </div>
    </div>

    <!-- 模式选择卡片 -->
    <div class="mode-cards">
      <el-row :gutter="24">
        <el-col :span="8">
          <el-card class="mode-card" @click="selectMode('realtime')">
            <div class="mode-content">
              <div class="mode-icon realtime">
                <el-icon><Clock /></el-icon>
              </div>
              <h3 class="mode-title">实时反馈模式</h3>
              <p class="mode-description">逐题实时反馈，适合学习巩固</p>
              <div class="mode-features">
                <el-tag type="success" size="small">即时反馈</el-tag>
                <el-tag type="info" size="small">学习模式</el-tag>
                <el-tag type="warning" size="small">进度保存</el-tag>
              </div>
              <div class="mode-stats">
                <span class="stat-item">全量题目</span>
                <span class="stat-item">实时反馈</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="mode-card" @click="selectMode('exam')">
            <div class="mode-content">
              <div class="mode-icon exam">
                <el-icon><Document /></el-icon>
              </div>
              <h3 class="mode-title">考试模式</h3>
              <p class="mode-description">完整答题后统一分析，适合能力测试</p>
              <div class="mode-features">
                <el-tag type="danger" size="small">批量提交</el-tag>
                <el-tag type="primary" size="small">考试模式</el-tag>
                <el-tag type="success" size="small">结果分析</el-tag>
              </div>
              <div class="mode-stats">
                <span class="stat-item">全量题目</span>
                <span class="stat-item">考试模式</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="mode-card" @click="selectMode('random')">
            <div class="mode-content">
              <div class="mode-icon random">
                <el-icon><QuestionFilled /></el-icon>
              </div>
              <h3 class="mode-title">随机综合模式</h3>
              <p class="mode-description">全类型随机出题，适合综合练习</p>
              <div class="mode-features">
                <el-tag type="warning" size="small">随机出题</el-tag>
                <el-tag type="info" size="small">综合练习</el-tag>
                <el-tag type="success" size="small">跨类型</el-tag>
              </div>
              <div class="mode-stats">
                <span class="stat-item">全量题目</span>
                <span class="stat-item">随机综合</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近测试记录 -->
    <div class="recent-records" v-if="recentRecords.length > 0">
      <h3 class="section-title">最近测试记录</h3>
      <el-card>
        <el-table :data="recentRecords" style="width: 100%">
          <el-table-column prop="categoryName" label="测试分类" width="120" />
          <el-table-column prop="modeName" label="测试模式" width="120" />
          <el-table-column prop="score" label="得分" width="80" />
          <el-table-column prop="completionRate" label="完成率" width="100">
            <template #default="{ row }">
              <el-progress :percentage="row.completionRate" :show-text="false" />
              <span style="margin-left: 8px">{{ row.completionRate }}%</span>
            </template>
          </el-table-column>
          <el-table-column prop="completedAt" label="完成时间" width="160" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewResult(row.id)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, Clock, QuestionFilled } from '@element-plus/icons-vue'
import testApi from '@/api/test'
import { isSuccessResponse } from '@/utils/api-helpers'

const router = useRouter()

// 响应式数据
const recentRecords = ref<any[]>([])

// 生命周期
onMounted(() => {
  loadRecentRecords()
})

// 加载最近测试记录
const loadRecentRecords = async () => {
  try {
    const response = await testApi.getUserTestRecords({ page: 0, size: 5 })
    if (isSuccessResponse(response) && response.data) {
      recentRecords.value = Array.isArray(response.data.content) ? response.data.content : []
    }
  } catch (error) {
    console.error('加载最近测试记录失败:', error)
  }
}

// 选择测试模式
const selectMode = (modeCode: string) => {
  ElMessage.success(`已选择${getModeName(modeCode)}`)
  router.push({
    name: 'TestCategories',
    query: { mode: modeCode }
  })
}

// 获取模式名称
const getModeName = (modeCode: string) => {
  const modeNames: Record<string, string> = {
    'realtime': '实时反馈模式',
    'exam': '考试模式',
    'random': '随机综合模式'
  }
  return modeNames[modeCode] || '未知模式'
}

// 查看测试结果
const viewResult = (recordId: number) => {
  router.push({
    name: 'TestResult',
    params: { recordId: recordId.toString() }
  })
}
</script>

<style lang="scss" scoped>
.test-mode-selector {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  
  .header-content {
    .page-title {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 12px;
      
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

.mode-cards {
  margin-bottom: 40px;
  
  .mode-card {
    cursor: pointer;
    transition: all 0.3s ease;
    border: 2px solid transparent;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
      border-color: #3b82f6;
    }
    
    .mode-content {
      text-align: center;
      padding: 24px;
      
      .mode-icon {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 20px;
        font-size: 32px;
        color: white;
        
        &.realtime {
          background: linear-gradient(135deg, #10b981, #059669);
        }
        
        &.exam {
          background: linear-gradient(135deg, #ef4444, #dc2626);
        }
        
        &.random {
          background: linear-gradient(135deg, #f59e0b, #d97706);
        }
      }
      
      .mode-title {
        font-size: 20px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 12px;
      }
      
      .mode-description {
        font-size: 14px;
        color: #6b7280;
        margin-bottom: 20px;
        line-height: 1.5;
      }
      
      .mode-features {
        margin-bottom: 20px;
        
        .el-tag {
          margin: 0 4px 8px 0;
        }
      }
      
      .mode-stats {
        display: flex;
        justify-content: center;
        gap: 20px;
        
        .stat-item {
          font-size: 14px;
          color: #6b7280;
          font-weight: 500;
        }
      }
    }
  }
}

.recent-records {
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 16px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .test-mode-selector {
    padding: 16px;
  }
  
  .mode-cards {
    .el-col {
      margin-bottom: 16px;
    }
  }
}
</style>

