<template>
  <div class="test-knowledge">
    <h1>知识中心测试页面</h1>
    
    <div class="test-section">
      <h2>认证状态测试</h2>
      <p>Token: {{ token ? '已设置' : '未设置' }}</p>
      <p>Token Type: {{ tokenType }}</p>
      <p>用户信息: {{ userInfo ? '已登录' : '未登录' }}</p>
    </div>

    <div class="test-section">
      <h2>API测试</h2>
      <ElButton @click="testCategories">测试分类接口</ElButton>
      <ElButton @click="testVulnerabilities">测试漏洞接口</ElButton>
      <ElButton @click="testUsers">测试用户接口</ElButton>
    </div>

    <div class="test-section">
      <h2>示例数据测试</h2>
      <ElButton @click="loadMockData">加载示例数据</ElButton>
      <div v-if="mockData.length > 0">
        <h3>示例数据 ({{ mockData.length }} 条)</h3>
        <div v-for="item in mockData" :key="item.id" class="mock-item">
          <strong>{{ item.title }}</strong> - {{ item.description }}
        </div>
      </div>
    </div>

    <div class="test-section">
      <h2>API响应</h2>
      <pre>{{ apiResponse }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElButton, ElMessage } from 'element-plus'
import { fetchKnowledgeCategories, fetchVulnerabilities } from '@/api/knowledge'
import { useAuthStore } from '@/stores/modules/auth'

const authStore = useAuthStore()
const token = ref('')
const tokenType = ref('')
const userInfo = ref<any>(null)
const apiResponse = ref('')
const mockData = ref<any[]>([])

onMounted(() => {
  // 检查认证状态
  token.value = localStorage.getItem('token') || ''
  tokenType.value = localStorage.getItem('token_type') || 'Bearer'
  userInfo.value = authStore.user
})

const testCategories = async () => {
  try {
    const res = await fetchKnowledgeCategories()
    apiResponse.value = JSON.stringify(res, null, 2)
    ElMessage.success('分类接口调用成功')
  } catch (error: any) {
    apiResponse.value = JSON.stringify(error.response?.data || error.message, null, 2)
    ElMessage.error('分类接口调用失败: ' + error.message)
  }
}

const testVulnerabilities = async () => {
  try {
    const res = await fetchVulnerabilities({ page: 0, size: 5 })
    apiResponse.value = JSON.stringify(res, null, 2)
    ElMessage.success('漏洞接口调用成功')
  } catch (error: any) {
    apiResponse.value = JSON.stringify(error.response?.data || error.message, null, 2)
    ElMessage.error('漏洞接口调用失败: ' + error.message)
  }
}

const testUsers = async () => {
  try {
    const res = await fetch('/api/v1/users/stats')
    const data = await res.json()
    apiResponse.value = JSON.stringify(data, null, 2)
    ElMessage.success('用户接口调用成功')
  } catch (error: any) {
    apiResponse.value = JSON.stringify(error.message, null, 2)
    ElMessage.error('用户接口调用失败: ' + error.message)
  }
}

const loadMockData = () => {
  mockData.value = [
    { id: 1, title: 'SQL注入漏洞', description: '数据库安全漏洞演示' },
    { id: 2, title: 'XSS跨站脚本', description: '前端安全漏洞演示' },
    { id: 3, title: 'CSRF跨站请求', description: '会话安全漏洞演示' }
  ]
  ElMessage.success('示例数据加载成功')
}
</script>

<style scoped>
.test-knowledge {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.mock-item {
  margin: 10px 0;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 300px;
}
</style>
