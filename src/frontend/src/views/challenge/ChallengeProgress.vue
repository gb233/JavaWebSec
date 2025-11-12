<template>
  <div class="challenge-progress">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="progress" class="progress-content">
      <div class="progress-header">
        <h2>{{ challenge?.title }}</h2>
        <div class="progress-stats">
          <el-statistic title="完成度" :value="progress.progressPercentage" suffix="%" />
          <el-statistic title="当前步骤" :value="progress.currentStep" />
          <el-statistic title="用时" :value="timeSpent" suffix="分钟" />
        </div>
      </div>
      
      <div class="progress-steps">
        <el-steps :active="progress.currentStep" finish-status="success">
          <el-step 
            v-for="(vuln, index) in vulnerabilityChain" 
            :key="index"
            :title="vuln"
            :description="`步骤 ${index + 1}`"
          />
        </el-steps>
      </div>
      
      <div class="progress-actions">
        <el-button type="primary" @click="executeStep" :loading="executing">
          执行下一步
        </el-button>
        <el-button @click="viewChallenge">
          返回挑战详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { challengeApi } from '@/api/challenge'

const route = useRoute()
const router = useRouter()
const progress = ref(null)
const challenge = ref(null)
const loading = ref(false)
const executing = ref(false)

const vulnerabilityChain = computed(() => {
  if (!challenge.value?.vulnerabilityChain) return []
  try {
    return JSON.parse(challenge.value.vulnerabilityChain)
  } catch {
    return []
  }
})

const timeSpent = computed(() => {
  if (!progress.value?.startedAt) return 0
  const start = new Date(progress.value.startedAt)
  const now = new Date()
  return Math.floor((now.getTime() - start.getTime()) / 60000)
})

const loadProgress = async () => {
  try {
    loading.value = true
    const [progressResponse, challengeResponse] = await Promise.all([
      challengeApi.getProgress(route.params.id),
      challengeApi.getScenario(route.params.id)
    ])
    
    if (progressResponse.success) {
      progress.value = progressResponse.data
    }
    if (challengeResponse.success) {
      challenge.value = challengeResponse.data
    }
  } catch (error) {
    ElMessage.error('加载挑战进度失败')
  } finally {
    loading.value = false
  }
}

const executeStep = async () => {
  try {
    executing.value = true
    const step = `step${progress.value.currentStep + 1}`
    const params = {
      // 根据当前步骤设置参数
      username: 'admin',
      password: 'admin'
    }
    
    const response = await challengeApi.executeStep(route.params.id, step, params)
    if (response.success) {
      ElMessage.success('步骤执行成功')
      loadProgress() // 重新加载进度
    }
  } catch (error) {
    ElMessage.error('执行步骤失败')
  } finally {
    executing.value = false
  }
}

const viewChallenge = () => {
  router.push(`/challenges/${route.params.id}`)
}

onMounted(() => {
  loadProgress()
})
</script>

<style scoped>
.challenge-progress {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.progress-header {
  margin-bottom: 30px;
}

.progress-stats {
  display: flex;
  gap: 30px;
  margin-top: 15px;
}

.progress-steps {
  margin-bottom: 30px;
}

.progress-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.loading {
  padding: 20px;
}
</style>
