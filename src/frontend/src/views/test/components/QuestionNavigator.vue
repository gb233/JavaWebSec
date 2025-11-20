<template>
  <div class="question-navigator" :class="`mode-${modeCode}`">
    <div class="navigator-header">
      <h4>{{ t('navigator.title') }}</h4>
      <div class="navigator-stats">
        <span class="stat-item">
          <span class="stat-label">{{ t('navigator.answered') }}</span>
          <span class="stat-value answered">{{ answeredCount }}</span>
        </span>
        <span class="stat-item">
          <span class="stat-label">{{ t('navigator.correct') }}</span>
          <span class="stat-value correct">{{ correctCount }}</span>
        </span>
        <span class="stat-item">
          <span class="stat-label">{{ t('navigator.wrong') }}</span>
          <span class="stat-value incorrect">{{ incorrectCount }}</span>
        </span>
        <span class="stat-item">
          <span class="stat-label">{{ t('navigator.unanswered') }}</span>
          <span class="stat-value unanswered">{{ unansweredCount }}</span>
        </span>
      </div>
    </div>

    <!-- 题目导航 - 优化布局 -->
    <div class="question-navigation">
      <div class="navigation-header">
        <span class="nav-title">{{ t('navigator.title') }}</span>
        <div class="nav-controls">
          <ElButton
            size="small"
            :type="viewMode === 'grid' ? 'primary' : 'default'"
            @click="toggleViewMode"
          >
            {{ viewMode === 'grid' ? t('navigator.grid') : t('navigator.list') }}
          </ElButton>
        </div>
      </div>

      <!-- 网格视图 - 紧凑布局 -->
      <div v-if="viewMode === 'grid'" class="question-grid-compact">
        <div
          v-for="(question, index) in questions"
          :key="question.id || index"
          class="question-item-compact"
          :class="getQuestionItemClass(index)"
          :title="getQuestionTooltip(index)"
          @click="navigateToQuestion(index)"
        >
          <span class="question-number">{{ index + 1 }}</span>
          <div v-if="question.isAnswered" class="question-status">
            <ElIcon v-if="question.isCorrect" class="status-icon correct">
              <CircleCheckFilled />
            </ElIcon>
            <ElIcon v-else class="status-icon incorrect">
              <CircleCloseFilled />
            </ElIcon>
          </div>
        </div>
      </div>

      <!-- 列表视图 - 完整信息 -->
      <div v-else class="question-list-compact">
        <!-- 调试信息 -->
        <div v-if="questions.length === 0" class="debug-info">
          调试：没有题目数据
        </div>
        <div v-else class="debug-info">
          调试：题目数量 {{ questions.length }}，当前索引 {{ currentIndex }}
        </div>

        <div
          v-for="(question, index) in questions"
          :key="question.id || index"
          class="question-list-item-compact"
          :class="getQuestionItemClass(index)"
          @click="navigateToQuestion(index)"
        >
          <div class="question-info">
            <span class="question-number">{{ index + 1 }}</span>
            <span class="question-type">{{ getQuestionTypeName(question.questionType) }}</span>
            <span v-if="question.categoryCode && modeCode === 'random'" class="question-category">
              {{ question.categoryCode }}
            </span>
          </div>
          <div class="question-status">
            <ElIcon v-if="question.isAnswered" :class="question.isCorrect ? 'correct' : 'incorrect'">
              <CircleCheckFilled v-if="question.isCorrect" />
              <CircleCloseFilled v-else />
            </ElIcon>
            <span v-else class="status-text">{{ t('navigator.unanswered') }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="navigator-footer">
      <div class="legend">
        <div class="legend-item">
          <div class="legend-color current" />
          <span>{{ t('navigator.currentQuestion') }}</span>
        </div>
        <div class="legend-item">
          <div class="legend-color answered-correct" />
          <span>{{ t('navigator.answeredCorrect') }}</span>
        </div>
        <div class="legend-item">
          <div class="legend-color answered-incorrect" />
          <span>{{ t('navigator.answeredWrong') }}</span>
        </div>
        <div class="legend-item">
          <div class="legend-color visited" />
          <span>{{ t('navigator.visited') }}</span>
        </div>
        <div class="legend-item">
          <div class="legend-color unanswered" />
          <span>{{ t('navigator.notVisited') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElIcon, ElButton, ElPagination } from 'element-plus'
import { CircleCheckFilled, CircleCloseFilled, Grid, List } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<Props>(), {
  allowNavigation: true,
  questionsPerRow: 10
})

const emit = defineEmits<{
  'navigate': [index: number]
}>()

const { t } = useI18n()

interface Question {
  id: number
  questionText: string
  questionType: string
  isAnswered: boolean
  isCorrect?: boolean
  isVisited: boolean
  categoryCode?: string
  score?: number
}

interface Props {
  questions: Question[]
  currentIndex: number
  modeCode: string
  allowNavigation?: boolean
  questionsPerRow?: number
}

// 视图模式控制
const viewMode = ref<'grid' | 'list'>('list')

// 计算属性
const gridStyle = computed(() => {
  const questionsPerRow = Math.min(props.questionsPerRow, 6) // 限制最大列数
  return {
    'grid-template-columns': `repeat(${questionsPerRow}, 1fr)`,
    gap: '4px'
  }
})

// 题目类型名称映射
const getQuestionTypeName = (questionType: string) => {
  const typeMap: Record<string, string> = {
    single: t('navigator.type.SINGLE'),
    SINGLE: t('navigator.type.SINGLE'),
    single_choice: t('navigator.type.SINGLE'),
    multiple: t('navigator.type.MULTIPLE'),
    MULTIPLE: t('navigator.type.MULTIPLE'),
    multiple_choice: t('navigator.type.MULTIPLE'),
    judge: t('navigator.type.JUDGE'),
    JUDGE: t('navigator.type.JUDGE'),
    true_false: t('navigator.type.JUDGE'),
    fill_blank: t('navigator.type.FILL_BLANK'),
    FILL_BLANK: t('navigator.type.FILL_BLANK')
  }
  return typeMap[questionType] || ''
}

const answeredCount = computed(() =>
  props.questions.filter(q => q.isAnswered).length
)

const correctCount = computed(() =>
  props.questions.filter(q => q.isAnswered && q.isCorrect).length
)

const incorrectCount = computed(() =>
  props.questions.filter(q => q.isAnswered && !q.isCorrect).length
)

const unansweredCount = computed(() =>
  props.questions.filter(q => !q.isAnswered).length
)

// 方法
const getQuestionItemClass = (index: number) => {
  const question = props.questions[index]
  const classes = []

  // 当前题目
  if (index === props.currentIndex) {
    classes.push('current')
  }

  // 答题状态
  if (question.isAnswered) {
    if (question.isCorrect) {
      classes.push('answered-correct')
    } else {
      classes.push('answered-incorrect')
    }
  } else if (question.isVisited) {
    classes.push('visited')
  } else {
    classes.push('unanswered')
  }

  // 导航限制
  if (!props.allowNavigation && !question.isVisited) {
    classes.push('disabled')
  }

  return classes
}

const getQuestionTooltip = (index: number) => {
  const question = props.questions[index]
  const tooltips = []

  tooltips.push(t('navigator.tooltip.questionIndex', { index: index + 1 }))

  if (question.categoryCode) {
    tooltips.push(t('navigator.tooltip.category', { code: question.categoryCode }))
  }

  if (question.questionType) {
    tooltips.push(t('navigator.tooltip.type', { type: question.questionType }))
  }

  if (question.isAnswered) {
    tooltips.push(question.isCorrect ? t('navigator.answeredCorrect') : t('navigator.answeredWrong'))
  } else if (question.isVisited) {
    tooltips.push(t('navigator.tooltip.visitedUnanswered'))
  } else {
    tooltips.push(t('navigator.notVisited'))
  }

  return tooltips.join('\n')
}

const navigateToQuestion = (index: number) => {
  const question = props.questions[index]

  // 检查导航权限
  if (!props.allowNavigation && !question.isVisited) {
    return
  }

  // 检查模式限制
  if (props.modeCode === 'exam' && index > props.currentIndex) {
    return
  }

  emit('navigate', index)
}

// 切换视图模式
const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}
</script>

<style scoped>
.question-navigator {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e4e7ed;
}

.navigator-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.navigator-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.navigator-stats {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.stat-label {
  color: #606266;
}

.stat-value {
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.stat-value.answered {
  background: #e1f3d8;
  color: #67c23a;
}

.stat-value.correct {
  background: #e1f3d8;
  color: #67c23a;
}

.stat-value.incorrect {
  background: #fde2e2;
  color: #f56c6c;
}

.stat-value.unanswered {
  background: #f4f4f5;
  color: #909399;
}

/* 题目导航容器 */
.question-navigation {
  margin-bottom: 16px;
}

.navigation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.nav-title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.nav-controls {
  display: flex;
  gap: 8px;
}

/* 紧凑网格视图 */
.question-grid-compact {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.question-item-compact {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.question-item-compact:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.question-item-compact.current {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.2);
}

.question-item-compact.answered-correct {
  border-color: #67c23a;
  background: #f0f9ff;
  color: #67c23a;
}

.question-item-compact.answered-incorrect {
  border-color: #f56c6c;
  background: #fef0f0;
  color: #f56c6c;
}

.question-item-compact.visited {
  border-color: #e6a23c;
  background: #fdf6ec;
  color: #e6a23c;
}

.question-item-compact.unanswered {
  border-color: #e4e7ed;
  background: white;
  color: #909399;
}

.question-item-compact .question-number {
  font-weight: 600;
  font-size: 12px;
  z-index: 1;
}

.question-item-compact .question-status {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid currentColor;
}

.question-item-compact .status-icon {
  font-size: 8px;
}

/* 紧凑列表视图 */
.question-list-compact {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: white;
}

.question-list-item-compact {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
}

.question-list-item-compact:hover {
  background: #f5f7fa;
}

.question-list-item-compact:last-child {
  border-bottom: none;
}

.question-list-item-compact .question-info {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.question-list-item-compact .question-number {
  font-weight: 600;
  color: #409eff;
  min-width: 20px;
  font-size: 12px;
}

.question-list-item-compact .question-type {
  font-size: 10px;
  color: #606266;
  background: #f0f0f0;
  padding: 1px 4px;
  border-radius: 2px;
}

.question-list-item-compact .question-category {
  font-size: 9px;
  color: #909399;
  background: #e6f7ff;
  padding: 1px 3px;
  border-radius: 2px;
}

.question-list-item-compact .question-status {
  display: flex;
  align-items: center;
  gap: 3px;
}

.question-list-item-compact .status-text {
  font-size: 10px;
  color: #909399;
}

.question-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.question-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.question-item.current {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.question-item.answered-correct {
  border-color: #67c23a;
  background: #f0f9ff;
  color: #67c23a;
}

.question-item.answered-incorrect {
  border-color: #f56c6c;
  background: #fef0f0;
  color: #f56c6c;
}

.question-item.visited {
  border-color: #e6a23c;
  background: #fdf6ec;
  color: #e6a23c;
}

.question-item.unanswered {
  border-color: #e4e7ed;
  background: white;
  color: #909399;
}

.question-item.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.question-item.disabled:hover {
  transform: none;
  box-shadow: none;
}

/* 紧凑列表视图中的状态样式 */
.question-list-item-compact.current {
  background: #f0f9ff;
  border-left: 3px solid #409eff;
}

.question-list-item-compact.answered-correct {
  background: #f0f9ff;
  border-left: 3px solid #67c23a;
}

.question-list-item-compact.answered-incorrect {
  background: #fef0f0;
  border-left: 3px solid #f56c6c;
}

.question-list-item-compact.visited {
  background: #fdf6ec;
  border-left: 3px solid #e6a23c;
}

.question-list-item-compact.unanswered {
  background: white;
  border-left: 3px solid #e4e7ed;
}

.question-list-item-compact.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.question-list-item-compact.disabled:hover {
  background: inherit;
}

.question-number {
  font-weight: 600;
  font-size: 14px;
  z-index: 1;
}

.question-status {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid currentColor;
}

.status-icon {
  font-size: 10px;
}

.status-icon.correct {
  color: #67c23a;
}

.status-icon.incorrect {
  color: #f56c6c;
}

.question-category {
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 8px;
  background: #409eff;
  color: white;
  padding: 1px 4px;
  border-radius: 2px;
  white-space: nowrap;
}

.navigator-footer {
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  border: 1px solid;
}

.legend-color.current {
  background: #f0f9ff;
  border-color: #409eff;
}

.legend-color.answered-correct {
  background: #f0f9ff;
  border-color: #67c23a;
}

.legend-color.answered-incorrect {
  background: #fef0f0;
  border-color: #f56c6c;
}

.legend-color.visited {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.legend-color.unanswered {
  background: white;
  border-color: #e4e7ed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-item {
    width: 35px;
    height: 35px;
  }

  .question-number {
    font-size: 12px;
  }

  .navigator-stats {
    gap: 8px;
  }

  .stat-item {
    font-size: 11px;
  }

  .navigation-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .nav-controls {
    width: 100%;
    justify-content: flex-end;
  }

  .question-list-compact {
    max-height: 250px;
  }

  .question-grid-compact {
    grid-template-columns: repeat(4, 1fr);
    max-height: 150px;
  }

  .question-list-item-compact .question-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
  }

  .question-list-item-compact .question-type,
  .question-list-item-compact .question-category {
    font-size: 9px;
  }
}

@media (max-width: 480px) {
  .question-grid-compact {
    grid-template-columns: repeat(5, 1fr);
    gap: 3px;
  }

  .question-item-compact {
    width: 28px;
    height: 28px;
  }

  .question-item-compact .question-number {
    font-size: 10px;
  }

  .question-list-compact {
    max-height: 200px;
  }
}

/* 调试信息样式 */
.debug-info {
  background: #f0f9ff;
  border: 1px solid #3b82f6;
  border-radius: 4px;
  padding: 8px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #1e40af;
}

/* 不同模式的样式差异 */
.mode-exam .question-item.disabled {
  /* 考试模式严格限制导航 */
  opacity: 0.3;
}

.mode-random .question-category {
  /* 随机综合模式显示分类信息 */
  display: block;
}
</style>
