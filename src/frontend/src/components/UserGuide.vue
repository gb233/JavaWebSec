<template>
  <div v-if="showGuide" class="user-guide-overlay">
    <!-- 指引遮罩层 -->
    <div class="guide-mask" @click="handleMaskClick"></div>
    
    <!-- 指引内容 -->
    <div 
      v-if="currentStep" 
      class="guide-content"
      :class="`guide-${currentStep.position}`"
      :style="guideStyle"
    >
      <!-- 指引头部 -->
      <div class="guide-header">
        <div class="guide-title">
          {{ currentStep.title }}
        </div>
        <div class="guide-counter">
          {{ currentStepIndex + 1 }} / {{ guideSteps.length }}
        </div>
      </div>
      
      <!-- 指引内容 -->
      <div class="guide-body">
        <div class="guide-description">
          {{ currentStep.description }}
        </div>
      </div>
      
      <!-- 指引操作 -->
      <div class="guide-actions">
        <el-button 
          v-if="currentStepIndex > 0" 
          @click="previousStep"
          size="small"
        >
          {{ $t('guide.previous') }}
        </el-button>
        
        <el-button 
          v-if="currentStepIndex < guideSteps.length - 1" 
          @click="nextStep"
          type="primary"
          size="small"
        >
          {{ $t('guide.next') }}
        </el-button>
        
        <el-button 
          v-if="currentStepIndex === guideSteps.length - 1" 
          @click="completeGuide"
          type="success"
          size="small"
        >
          {{ $t('guide.complete') }}
        </el-button>
        
        <el-button 
          @click="skipGuide"
          size="small"
          text
        >
          {{ $t('guide.skip') }}
        </el-button>
      </div>
      
      <!-- 指引指示器 -->
      <div class="guide-indicator" :class="`indicator-${currentStep.position}`"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { guideApi, type GuideStep } from '@/api/guide'
import { isSuccessResponse } from '@/utils/api-helpers'

// Props
interface Props {
  autoShow?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  autoShow: true
})

// Emits
const emit = defineEmits<{
  complete: []
  skip: []
}>()

// 响应式数据
const showGuide = ref(false)
const guideSteps = ref<GuideStep[]>([])
const currentStepIndex = ref(0)
const currentStep = computed(() => guideSteps.value[currentStepIndex.value])
const guideStyle = ref({})

// 方法
const loadGuideSteps = async () => {
  try {
    const response = await guideApi.getGuideSteps()
    if (isSuccessResponse(response) && response.data) {
      guideSteps.value = response.data
    }
  } catch (error) {
    console.error('加载指引步骤失败:', error)
  }
}

const checkShouldShowGuide = async () => {
  if (!props.autoShow) return false
  
  try {
    const response = await guideApi.shouldShowGuide()
    return isSuccessResponse(response) && response.data === true
  } catch (error) {
    console.error('检查指引显示状态失败:', error)
    return false
  }
}

const startGuide = async () => {
  await loadGuideSteps()
  if (guideSteps.value.length > 0) {
    showGuide.value = true
    await nextTick()
    updateGuidePosition()
  }
}

const updateGuidePosition = () => {
  if (!currentStep.value) return
  
  const targetElement = document.querySelector(currentStep.value.targetElement)
  if (targetElement) {
    const rect = targetElement.getBoundingClientRect()
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft
    
    const position = currentStep.value.position || 'bottom'
    let top = rect.top + scrollTop
    let left = rect.left + scrollLeft
    
    // 根据位置调整
    switch (position) {
      case 'top':
        top = rect.top + scrollTop - 20
        left = rect.left + scrollLeft + rect.width / 2
        break
      case 'bottom':
        top = rect.bottom + scrollTop + 20
        left = rect.left + scrollLeft + rect.width / 2
        break
      case 'left':
        top = rect.top + scrollTop + rect.height / 2
        left = rect.left + scrollLeft - 20
        break
      case 'right':
        top = rect.top + scrollTop + rect.height / 2
        left = rect.right + scrollLeft + 20
        break
    }
    
    guideStyle.value = {
      position: 'absolute',
      top: `${top}px`,
      left: `${left}px`,
      zIndex: 9999
    }
  }
}

const nextStep = () => {
  if (currentStepIndex.value < guideSteps.value.length - 1) {
    currentStepIndex.value++
    nextTick(() => {
      updateGuidePosition()
    })
  }
}

const previousStep = () => {
  if (currentStepIndex.value > 0) {
    currentStepIndex.value--
    nextTick(() => {
      updateGuidePosition()
    })
  }
}

const completeGuide = async () => {
  try {
    await guideApi.markGuideCompleted()
    showGuide.value = false
    ElMessage.success('指引完成！')
    emit('complete')
  } catch (error) {
    console.error('标记指引完成失败:', error)
    ElMessage.error('指引完成失败')
  }
}

const skipGuide = async () => {
  try {
    // 先关闭指引遮罩，避免层级冲突
    showGuide.value = false
    
    // 使用setTimeout确保DOM更新完成
    await new Promise(resolve => setTimeout(resolve, 100))
    
    await ElMessageBox.confirm(
      '确定要跳过新手指引吗？您可以在右上角重新查看指引。',
      '跳过指引',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 用户确认跳过，禁用自动显示指引
    // 这样下次登录时就不会自动弹出指引了
    try {
      await guideApi.setAutoShowGuide(false)
      console.log('已禁用自动显示指引，用户可以通过右上角手动查看')
      ElMessage.success('已跳过指引，您可以在右上角重新查看')
    } catch (error) {
      console.error('禁用自动显示指引失败:', error)
      // 即使API调用失败，也继续执行跳过逻辑
    }
    
    emit('skip')
  } catch {
    // 用户取消，重新显示指引
    showGuide.value = true
  }
}

const handleMaskClick = () => {
  // 点击遮罩层不关闭指引，需要用户主动操作
}

// 暴露方法给父组件
const triggerGuide = () => {
  startGuide()
}

defineExpose({
  triggerGuide
})

// 生命周期
onMounted(async () => {
  if (props.autoShow) {
    // 检查是否是语言切换触发的页面刷新
    const isLanguageSwitch = sessionStorage.getItem('language-switching') === 'true'
    if (isLanguageSwitch) {
      // 清除语言切换标记
      sessionStorage.removeItem('language-switching')
      console.log('检测到语言切换，跳过新手指引')
      return
    }
    
    // 完全依赖后端API判断是否显示新手指引
    // 后端会检查数据库中的 hasCompletedInitialGuide 字段
    // 如果用户已经完成过指引，API会返回false，不会显示指引
    const shouldShow = await checkShouldShowGuide()
    if (shouldShow) {
      console.log('后端判断需要显示新手指引')
      await startGuide()
    } else {
      console.log('后端判断不需要显示新手指引（用户已完成或已禁用）')
    }
  }
})
</script>

<style scoped>
.user-guide-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9998;
}

.guide-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
}

.guide-content {
  position: absolute;
  max-width: 320px;
  background: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--el-border-color);
  overflow: hidden;
}

.guide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-color-primary-light-9);
}

.guide-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-primary);
}

.guide-counter {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-color-primary-light-8);
  padding: 2px 8px;
  border-radius: 12px;
}

.guide-body {
  padding: 16px 20px;
}

.guide-description {
  font-size: 14px;
  line-height: 1.6;
  color: var(--el-text-color-primary);
}

.guide-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color-page);
}

/* 跳过指引确认弹窗样式 */
:deep(.guide-skip-confirm) {
  z-index: 10000 !important;
}

:deep(.guide-skip-confirm .el-message-box) {
  z-index: 10000 !important;
  position: fixed !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
}

:deep(.guide-skip-confirm .el-message-box__wrapper) {
  z-index: 10000 !important;
  background: rgba(0, 0, 0, 0.5) !important;
}

:deep(.guide-skip-confirm .el-button) {
  pointer-events: auto !important;
  cursor: pointer !important;
}

:deep(.guide-skip-confirm .el-button:hover) {
  background-color: var(--el-color-primary) !important;
  border-color: var(--el-color-primary) !important;
}

.guide-indicator {
  position: absolute;
  width: 0;
  height: 0;
  border: 8px solid transparent;
}

.indicator-top {
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-top-color: var(--el-bg-color);
  border-bottom: none;
}

.indicator-bottom {
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-bottom-color: var(--el-bg-color);
  border-top: none;
}

.indicator-left {
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  border-left-color: var(--el-bg-color);
  border-right: none;
}

.indicator-right {
  right: 100%;
  top: 50%;
  transform: translateY(-50%);
  border-right-color: var(--el-bg-color);
  border-left: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .guide-content {
    max-width: 280px;
    margin: 0 20px;
  }
  
  .guide-header {
    padding: 12px 16px 8px;
  }
  
  .guide-body {
    padding: 12px 16px;
  }
  
  .guide-actions {
    padding: 8px 16px 12px;
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
