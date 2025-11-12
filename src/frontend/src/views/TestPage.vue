<template>
  <div class="test-page">
    <h1>测试页面</h1>
    <p>如果你能看到这个页面，说明前端应用正常工作！</p>
    <ElButton type="primary" @click="testApi">
      测试API
    </ElButton>
    <div v-if="apiResult">
      <h3>API测试结果：</h3>
      <pre>{{ apiResult }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const apiResult = ref('')

const testApi = async () => {
  try {
    const response = await fetch('/api/v1/users/stats')
    const data = await response.json()
    apiResult.value = JSON.stringify(data, null, 2)
    ElMessage.success('API测试成功！')
  } catch (error) {
    ElMessage.error(`API测试失败：${error}`)
    apiResult.value = `Error: ${error}`
  }
}
</script>

<style scoped>
.test-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
