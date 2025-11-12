<template>
  <div class="test-result">
    <!-- 结果头部 -->
    <div class="result-header">
      <div class="result-icon" :class="resultClass">
        <el-icon v-if="testRecord.isPassed">
          <Trophy />
        </el-icon>
        <el-icon v-else>
          <Warning />
        </el-icon>
      </div>
      <div class="result-content">
        <h1 class="result-title">{{ testRecord.isPassed ? '恭喜通过！' : '继续努力！' }}</h1>
        <p class="result-description">
          {{ testRecord.isPassed ? '您已成功通过测试' : '测试未通过，请继续学习' }}
        </p>
      </div>
    </div>

    <!-- 成绩统计 -->
    <div class="score-section">
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="score-card">
            <div class="score-value">{{ Math.round(testRecord.percentage) }}%</div>
            <div class="score-label">总分</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="score-card">
            <div class="score-value">{{ testRecord.correctAnswers }}</div>
            <div class="score-label">正确题数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="score-card">
            <div class="score-value">{{ testRecord.wrongAnswers }}</div>
            <div class="score-label">错误题数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="score-card">
            <div class="score-value">{{ formatTime(testRecord.timeTaken) }}</div>
            <div class="score-label">用时</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 答题详情 -->
    <div class="answer-details">
      <h2 class="section-title">答题详情</h2>
      <div class="answers-list">
        <div 
          v-for="(detail, index) in testRecord.answerDetails" 
          :key="detail.id"
          class="answer-item"
          :class="{ 'correct': detail.isCorrect, 'wrong': !detail.isCorrect }"
        >
          <div class="answer-header">
            <div class="question-number">第 {{ index + 1 }} 题</div>
            <div class="answer-status">
              <el-tag :type="detail.isCorrect ? 'success' : 'danger'">
                {{ detail.isCorrect ? '正确' : '错误' }}
              </el-tag>
            </div>
          </div>
          
          <div class="question-text">
            {{ detail.questionText || '题目内容' }}
          </div>
          
          <div class="answer-content">
            <div class="user-answer">
              <span class="label">您的答案：</span>
              <span class="value">{{ formatUserAnswer(detail.userAnswer) }}</span>
            </div>
            <div class="correct-answer">
              <span class="label">正确答案：</span>
              <span class="value">{{ detail.correctAnswer }}</span>
            </div>
            <div class="explanation" v-if="detail.explanation">
              <span class="label">解析：</span>
              <span class="value">{{ detail.explanation }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="result-actions">
      <el-button size="large" @click="goToCategories">
        返回测试分类
      </el-button>
      <el-button 
        type="primary" 
        size="large" 
        @click="retakeTest"
        v-if="!testRecord.isPassed"
      >
        重新测试
      </el-button>
      <el-button 
        type="success" 
        size="large" 
        @click="viewWrongQuestions"
        v-if="testRecord.wrongAnswers > 0"
      >
        查看错题
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Trophy, Warning } from '@element-plus/icons-vue'
import testApi, { type TestRecord } from '@/api/test'
import { isSuccessResponse } from '@/utils/api-helpers'

const route = useRoute()
const router = useRouter()

// 响应式数据
const defaultRecord: TestRecord = {
  id: 0,
  userId: 0,
  testName: '安全知识测试',
  categoryCode: '',
  categoryName: '',
  totalQuestions: 0,
  correctAnswers: 0,
  wrongAnswers: 0,
  score: 0,
  maxScore: 0,
  percentage: 0,
  timeTaken: 0,
  timeLimit: 0,
  isPassed: false,
  passThreshold: 60,
  startedAt: '',
  completedAt: '',
  createdAt: '',
  answerDetails: []
}

const testRecord = ref<TestRecord>({ ...defaultRecord })
const loading = ref(false)

// 计算属性
const resultClass = computed(() => {
  return testRecord.value.isPassed ? 'passed' : 'failed'
})

// 生命周期
onMounted(() => {
  const recordId = route.params.recordId as string
  if (recordId) {
    loadTestResult(parseInt(recordId))
  }
})

// 加载测试结果
const loadTestResult = async (testRecordId: number) => {
  try {
    loading.value = true
    const response = await testApi.getTestRecordDetail(testRecordId)
    if (isSuccessResponse(response) && response.data) {
      testRecord.value = {
        ...defaultRecord,
        ...response.data,
        answerDetails: Array.isArray(response.data.answerDetails)
          ? response.data.answerDetails
          : []
      }
    } else {
      ElMessage.error('加载测试结果失败')
      router.back()
    }
  } catch (error) {
    console.error('加载测试结果失败:', error)
    ElMessage.error('加载测试结果失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 格式化用户答案
const formatUserAnswer = (userAnswer: string) => {
  // 检查是否为空或null
  if (!userAnswer || userAnswer.trim() === '' || userAnswer === 'null' || userAnswer === 'undefined') {
    return '未作答'
  }
  
  // 如果是"已答题但答案为空"的情况，显示为"未作答"
  if (userAnswer === '已答题但答案为空') {
    return '未作答'
  }
  
  // 如果是字母格式的答案（如A,B,C），直接返回
  if (userAnswer.match(/^[A-Z,]+$/)) {
    return userAnswer
  }
  
  // 如果是其他格式，直接返回
  return userAnswer
}

// 返回测试分类
const goToCategories = () => {
  router.push({ name: 'TestCategories' })
}

// 重新测试
const retakeTest = () => {
  router.push({ name: 'TestCategories' })
}

// 查看错题
const viewWrongQuestions = () => {
  router.push({ name: 'TestRecords' })
}
</script>

<style lang="scss" scoped>
.test-result {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .result-header {
    display: flex;
    align-items: center;
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;

    .result-icon {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24px;
      font-size: 40px;

      &.passed {
        background: #dcfce7;
        color: #16a34a;
      }

      &.failed {
        background: #fef3c7;
        color: #d97706;
      }
    }

    .result-content {
      .result-title {
        font-size: 32px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
      }

      .result-description {
        font-size: 16px;
        color: #6b7280;
        margin: 0;
      }
    }
  }

  .score-section {
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;

    .score-card {
      text-align: center;
      padding: 20px;

      .score-value {
        font-size: 36px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
      }

      .score-label {
        font-size: 14px;
        color: #6b7280;
      }
    }
  }

  .answer-details {
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;

    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 24px;
    }

    .answers-list {
      .answer-item {
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 16px;
        transition: all 0.2s ease;

        &.correct {
          border-color: #16a34a;
          background: #f0fdf4;
        }

        &.wrong {
          border-color: #dc2626;
          background: #fef2f2;
        }

        .answer-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          .question-number {
            font-size: 16px;
            font-weight: 600;
            color: #1f2937;
          }
        }

        .question-text {
          font-size: 14px;
          color: #6b7280;
          margin-bottom: 16px;
          line-height: 1.5;
        }

        .answer-content {
          .user-answer,
          .correct-answer,
          .explanation {
            margin-bottom: 8px;

            .label {
              font-weight: 500;
              color: #374151;
              margin-right: 8px;
            }

            .value {
              color: #1f2937;
            }
          }

          .user-answer {
            .value {
              color: #dc2626;
            }
          }

          .correct-answer {
            .value {
              color: #16a34a;
            }
          }

          .explanation {
            .value {
              color: #6b7280;
              font-style: italic;
            }
          }
        }
      }
    }
  }

  .result-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    background: white;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }
}
</style>
