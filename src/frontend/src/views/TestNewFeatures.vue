<template>
  <div class="test-new-features">
    <h1>新功能测试页面</h1>
    
    <div class="feature-section">
      <h2>语言切换功能</h2>
      <LanguageSwitch />
    </div>
    
    <div class="feature-section">
      <h2>新手指引功能</h2>
      <GuideTrigger @trigger="handleTriggerGuide" />
    </div>
    
    <div class="feature-section">
      <h2>用户指引组件</h2>
      <UserGuide 
        ref="userGuideRef"
        :auto-show="false"
        @complete="handleGuideComplete"
        @skip="handleGuideSkip"
      />
    </div>
    
    <div class="feature-section">
      <h2>测试按钮</h2>
      <ElButton @click="testLanguageAPI">测试语言 API</ElButton>
      <ElButton @click="testGuideAPI">测试指引 API</ElButton>
      <ElButton @click="triggerGuide">手动触发指引</ElButton>
    </div>
    
    <div class="feature-section">
      <h2>API 测试结果</h2>
      <pre>{{ apiResults }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElButton, ElMessage } from 'element-plus'
import LanguageSwitch from '@/components/LanguageSwitch.vue'
import GuideTrigger from '@/components/GuideTrigger.vue'
import UserGuide from '@/components/UserGuide.vue'
import { languageApi } from '@/api/language'
import { guideApi } from '@/api/guide'

// 指引组件引用
const userGuideRef = ref()
const apiResults = ref('')

// 处理指引触发
const handleTriggerGuide = () => {
  console.log('触发新手指引')
  ElMessage.success('新手指引已触发')
}

// 处理指引完成
const handleGuideComplete = () => {
  console.log('用户完成新手指引')
  ElMessage.success('指引完成')
}

// 处理指引跳过
const handleGuideSkip = () => {
  console.log('用户跳过新手指引')
  ElMessage.info('指引已跳过')
}

// 测试语言 API
const testLanguageAPI = async () => {
  try {
    const response = await languageApi.getLanguagePreference()
    apiResults.value = `语言 API 测试结果:\n${JSON.stringify(response, null, 2)}`
    ElMessage.success('语言 API 测试成功')
  } catch (error) {
    apiResults.value = `语言 API 测试失败: ${error}`
    ElMessage.error('语言 API 测试失败')
  }
}

// 测试指引 API
const testGuideAPI = async () => {
  try {
    const response = await guideApi.getUserGuidePreference()
    apiResults.value = `指引 API 测试结果:\n${JSON.stringify(response, null, 2)}`
    ElMessage.success('指引 API 测试成功')
  } catch (error) {
    apiResults.value = `指引 API 测试失败: ${error}`
    ElMessage.error('指引 API 测试失败')
  }
}

// 手动触发指引
const triggerGuide = () => {
  userGuideRef.value?.triggerGuide()
}
</script>

<style scoped>
.test-new-features {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.feature-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.feature-section h2 {
  margin-top: 0;
  color: #333;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
