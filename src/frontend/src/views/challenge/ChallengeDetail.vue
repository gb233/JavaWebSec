<template>
  <div class="challenge-detail">
    <div v-if="loading" class="loading">
      <ElSkeleton :rows="5" animated />
    </div>

    <div v-else-if="challenge" class="challenge-content">
      <div class="challenge-header">
        <h1>{{ challenge.title }}</h1>
        <div class="challenge-meta">
          <ElTag :type="getDifficultyTag(challenge.difficultyLevel)">
            {{ getDifficultyText(challenge.difficultyLevel) }}
          </ElTag>
          <span class="time">{{ challenge.estimatedTime }}分钟</span>
          <span class="points">{{ challenge.points }}分</span>
        </div>
      </div>

      <div class="challenge-description">
        <h3>挑战描述</h3>
        <p>{{ challenge.description }}</p>
      </div>

      <div class="vulnerability-chain">
        <h3>漏洞链</h3>
        <ElSteps :active="0" finish-status="success">
          <ElStep
            v-for="(vuln, index) in vulnerabilityChain"
            :key="index"
            :title="vuln"
            :description="`步骤 ${index + 1}`"
          />
        </ElSteps>
      </div>

      <div class="challenge-actions">
        <ElButton type="primary" size="large" @click="startChallenge">
          开始挑战
        </ElButton>
        <ElButton size="large" @click="viewProgress">
          查看进度
        </ElButton>
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
const challenge = ref(null)
const loading = ref(false)

const vulnerabilityChain = computed(() => {
  if (!challenge.value?.vulnerabilityChain) return []
  try {
    return JSON.parse(challenge.value.vulnerabilityChain)
  } catch {
    return []
  }
})

const loadChallenge = async () => {
  try {
    loading.value = true
    const response = await challengeApi.getScenario(route.params.id)
    if (response.success) {
      challenge.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载挑战详情失败')
  } finally {
    loading.value = false
  }
}

const startChallenge = async () => {
  try {
    const response = await challengeApi.startChallenge(route.params.id)
    if (response.success) {
      ElMessage.success('挑战开始成功')
      router.push(`/challenge/arena/${route.params.id}`)
    }
  } catch (error) {
    ElMessage.error('开始挑战失败')
  }
}

const viewProgress = () => {
  router.push(`/challenge/arena/${route.params.id}`)
}

const getDifficultyTag = (level: string) => {
  const tagMap = {
    beginner: 'success',
    intermediate: 'warning',
    advanced: 'danger',
    expert: 'info'
  }
  return tagMap[level] || 'info'
}

const getDifficultyText = (level: string) => {
  const textMap = {
    beginner: '初级',
    intermediate: '中级',
    advanced: '高级',
    expert: '专家'
  }
  return textMap[level] || level
}

onMounted(() => {
  loadChallenge()
})
</script>

<style scoped>
.challenge-detail {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.challenge-header {
  margin-bottom: 30px;
}

.challenge-header h1 {
  margin-bottom: 15px;
}

.challenge-meta {
  display: flex;
  gap: 15px;
  align-items: center;
}

.challenge-description {
  margin-bottom: 30px;
}

.vulnerability-chain {
  margin-bottom: 30px;
}

.challenge-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.loading {
  padding: 20px;
}
</style>