<template>
  <div class="basic-test">
    <h1>基础功能测试</h1>

    <div class="test-section">
      <h2>语言切换测试</h2>
      <button @click="switchLanguage">
        切换语言
      </button>
      <p>当前语言: {{ currentLanguage }}</p>
    </div>

    <div class="test-section">
      <h2>新手指引测试</h2>
      <button @click="showGuide">
        显示指引
      </button>
      <p>指引状态: {{ guideMessage }}</p>
    </div>

    <div class="test-section">
      <h2>API连接测试</h2>
      <button @click="testBackendConnection">
        测试后端连接
      </button>
      <pre>{{ connectionResult }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 响应式数据
const currentLanguage = ref('zh-CN')
const guideMessage = ref('未测试')
const connectionResult = ref('')

// 切换语言
const switchLanguage = () => {
  currentLanguage.value = currentLanguage.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  guideMessage.value = `语言已切换到: ${currentLanguage.value}`
}

// 显示指引
const showGuide = () => {
  guideMessage.value = '新手指引功能已触发！'
}

// 测试后端连接
const testBackendConnection = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/v1/language/supported', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    const data = await response.json()
    connectionResult.value = `状态码: ${response.status}\n响应: ${JSON.stringify(data, null, 2)}`
  } catch (error) {
    connectionResult.value = `连接失败: ${error}`
  }
}
</script>

<style scoped>
.basic-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.test-section h2 {
  margin-top: 0;
  color: #333;
}

button {
  margin: 5px;
  padding: 10px 20px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

button:hover {
  background: #66b1ff;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  white-space: pre-wrap;
  font-size: 12px;
}
</style>
