<template>
  <div class="test-connection">
    <h1>前后端联调测试</h1>

    <div class="test-section">
      <h2>后端健康检查</h2>
      <ElButton :loading="healthLoading" @click="testHealth">
        测试后端连接
      </ElButton>
      <div v-if="healthResult" class="result">
        <ElAlert
          :title="healthResult.success ? '连接成功' : '连接失败'"
          :type="healthResult.success ? 'success' : 'error'"
          :description="healthResult.message"
          show-icon
        />
      </div>
    </div>

    <div class="test-section">
      <h2>用户统计API测试</h2>
      <ElButton :loading="statsLoading" @click="testUserStats">
        获取用户统计
      </ElButton>
      <div v-if="statsResult" class="result">
        <ElAlert
          :title="statsResult.success ? '获取成功' : '获取失败'"
          :type="statsResult.success ? 'success' : 'error'"
          :description="statsResult.message"
          show-icon
        />
        <div v-if="statsResult.data" class="data-display">
          <pre>{{ JSON.stringify(statsResult.data, null, 2) }}</pre>
        </div>
      </div>
    </div>

    <div class="test-section">
      <h2>学习笔记API测试</h2>
      <ElForm :model="noteForm" label-width="100px">
        <ElFormItem label="笔记标题">
          <ElInput v-model="noteForm.title" placeholder="请输入笔记标题" />
        </ElFormItem>
        <ElFormItem>
          <ElButton :loading="noteLoading" @click="testNoteApi">
            测试笔记API
          </ElButton>
        </ElFormItem>
      </ElForm>
      <div v-if="noteResult" class="result">
        <ElAlert
          :title="noteResult.success ? '测试成功' : '测试失败'"
          :type="noteResult.success ? 'success' : 'error'"
          :description="noteResult.message"
          show-icon
        />
        <div v-if="noteResult.data" class="data-display">
          <pre>{{ JSON.stringify(noteResult.data, null, 2) }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const healthLoading = ref(false)
const statsLoading = ref(false)
const noteLoading = ref(false)

const healthResult = ref<any>(null)
const statsResult = ref<any>(null)
const noteResult = ref<any>(null)

const noteForm = reactive({
  title: '测试笔记'
})

// 获取API基础URL（使用环境变量或相对路径）
const getApiBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL
  if (envUrl) {
    return envUrl.replace(/\/api\/?$/, '')
  }
  // 生产环境使用相对路径，开发环境使用默认值
  return import.meta.env.PROD ? '' : 'http://localhost:8080'
}

// 测试后端健康检查
const testHealth = async () => {
  healthLoading.value = true
  try {
    const baseUrl = getApiBaseUrl()
    const response = await fetch(`${baseUrl}/actuator/health`)
    const data = await response.json()
    healthResult.value = {
      success: response.ok,
      message: `状态: ${data.status}`,
      data
    }
  } catch (error: any) {
    healthResult.value = {
      success: false,
      message: `连接失败: ${error.message}`
    }
  } finally {
    healthLoading.value = false
  }
}

// 测试用户统计API
const testUserStats = async () => {
  statsLoading.value = true
  try {
    const baseUrl = getApiBaseUrl()
    const response = await fetch(`${baseUrl}/api/v1/users/stats`)
    const data = await response.json()
    statsResult.value = {
      success: data.success !== false,
      message: data.message || '获取用户统计信息',
      data: data.data
    }
  } catch (error: any) {
    statsResult.value = {
      success: false,
      message: `请求失败: ${error.message}`
    }
  } finally {
    statsLoading.value = false
  }
}

// 测试学习笔记API
const testNoteApi = async () => {
  if (!noteForm.title.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }

  noteLoading.value = true
  try {
    const baseUrl = getApiBaseUrl()
    const response = await fetch(`${baseUrl}/api/v1/notes/my?page=0&size=10`)
    const data = await response.json()
    noteResult.value = {
      success: data.success !== false,
      message: data.message || '获取学习笔记列表',
      data: data.data
    }
  } catch (error: any) {
    noteResult.value = {
      success: false,
      message: `请求失败: ${error.message}`
    }
  } finally {
    noteLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.test-connection {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 32px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;

  h2 {
    margin-bottom: 16px;
    color: #1f2937;
  }
}

.result {
  margin-top: 16px;
}

.data-display {
  margin-top: 12px;
  padding: 12px;
  background: #f3f4f6;
  border-radius: 6px;
  border: 1px solid #e5e7eb;

  pre {
    margin: 0;
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 12px;
    color: #1f2937;
    white-space: pre-wrap;
    word-break: break-word;
  }
}
</style>
