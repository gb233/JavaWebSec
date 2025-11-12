<template>
  <el-button 
    id="guide-trigger"
    type="text" 
    class="guide-trigger-btn"
    @click="handleTriggerGuide"
    :loading="loading"
  >
    <el-icon><QuestionFilled /></el-icon>
    <span class="guide-text">{{ $t('guide.trigger') }}</span>
  </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import { guideApi } from '@/api/guide'

// Emits
const emit = defineEmits<{
  trigger: []
}>()

// 响应式数据
const loading = ref(false)

// 方法
const handleTriggerGuide = async () => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    // 重置用户指引状态
    await guideApi.resetUserGuide()
    
    // 触发指引
    emit('trigger')
    
    ElMessage.success('新手指引已启动')
  } catch (error) {
    console.error('触发指引失败:', error)
    ElMessage.error('触发指引失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.guide-trigger-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  color: var(--el-text-color-primary);
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  background: var(--el-bg-color);
  transition: all 0.3s;
}

.guide-trigger-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.guide-text {
  font-size: 14px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .guide-text {
    display: none;
  }
  
  .guide-trigger-btn {
    padding: 8px;
  }
}
</style>
