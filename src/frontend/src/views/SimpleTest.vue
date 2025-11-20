<template>
  <div class="simple-test">
    <h1>简单功能测试</h1>

    <div class="test-section">
      <h2>语言切换测试</h2>
      <button @click="testLanguageSwitch">
        测试语言切换
      </button>
      <p>当前语言: {{ currentLanguage }}</p>
    </div>

    <div class="test-section">
      <h2>新手指引测试</h2>
      <button @click="testGuide">
        测试新手指引
      </button>
      <p>指引状态: {{ guideStatus }}</p>
    </div>

    <div class="test-section">
      <h2>API测试</h2>
      <button @click="testLanguageApi">
        测试语言API
      </button>
      <button @click="testGuideApi">
        测试指引API
      </button>
      <pre>{{ apiResult }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { languageApi } from '@/api/language'
import { guideApi } from '@/api/guide'

// 响应式数据
const currentLanguage = ref('zh-CN')
const guideStatus = ref('未测试')
const apiResult = ref('')

// 测试语言切换
const testLanguageSwitch = () => {
  currentLanguage.value = currentLanguage.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  console.log('语言切换测试:', currentLanguage.value)
}

// 测试新手指引
const testGuide = () => {
  guideStatus.value = '测试中...'
  console.log('新手指引测试')
}

// 测试语言API
const testLanguageApi = async () => {
  try {
    const response = await languageApi.getSupportedLanguages()
    apiResult.value = JSON.stringify(response, null, 2)
    console.log('语言API测试结果:', response)
  } catch (error) {
    apiResult.value = `错误: ${error}`
    console.error('语言API测试失败:', error)
  }
}

// 测试指引API
const testGuideApi = async () => {
  try {
    const response = await guideApi.shouldShowGuide()
    apiResult.value = JSON.stringify(response, null, 2)
    console.log('指引API测试结果:', response)
  } catch (error) {
    apiResult.value = `错误: ${error}`
    console.error('指引API测试失败:', error)
  }
}
</script>

<style scoped>
.simple-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.test-section h2 {
  margin-top: 0;
  color: #333;
}

button {
  margin: 5px;
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background: #66b1ff;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
