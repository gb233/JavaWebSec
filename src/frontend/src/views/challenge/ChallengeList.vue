<template>
  <div class="challenge-list">
    <div class="challenge-header">
      <h2>{{ $t('challenge.integratedChallenges') }}</h2>
      <div class="filter-controls">
        <el-select v-model="selectedDifficulty" :placeholder="$t('challenge.selectDifficulty')" @change="filterChallenges">
          <el-option :label="$t('challenge.all')" value="" />
          <el-option :label="$t('challenge.beginner')" value="beginner" />
          <el-option :label="$t('challenge.intermediate')" value="intermediate" />
          <el-option :label="$t('challenge.advanced')" value="advanced" />
          <el-option :label="$t('challenge.expert')" value="expert" />
        </el-select>
      </div>
    </div>
    
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else class="challenge-grid">
      <el-card 
        v-for="challenge in filteredChallenges" 
        :key="challenge.id" 
        class="challenge-card"
        @click="viewChallenge(challenge.id)"
      >
        <div class="challenge-card-header">
          <h3>{{ challenge.title }}</h3>
          <el-tag :type="getDifficultyTag(challenge.difficultyLevel)">
            {{ getDifficultyText(challenge.difficultyLevel) }}
          </el-tag>
        </div>
        
        <div class="challenge-card-content">
          <p>{{ challenge.description }}</p>
          <div class="challenge-meta">
            <span><i class="el-icon-time"></i> {{ challenge.estimatedTime }}{{ $t('challenge.minutes') }}</span>
            <span><i class="el-icon-star"></i> {{ challenge.points }}{{ $t('challenge.points') }}</span>
          </div>
          <div class="vulnerability-chain">
            <el-tag 
              v-for="vuln in getVulnerabilityChain(challenge.vulnerabilityChain)" 
              :key="vuln"
              size="small"
              type="info"
            >
              {{ vuln }}
            </el-tag>
          </div>
        </div>
        
        <div class="challenge-card-footer">
          <el-button type="primary" @click.stop="startChallenge(challenge.id)">
            开始挑战
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { challengeApi } from '@/api/challenge'

const router = useRouter()
const challenges = ref([])
const selectedDifficulty = ref('')
const loading = ref(false)

const filteredChallenges = computed(() => {
  if (!selectedDifficulty.value) {
    return challenges.value
  }
  return challenges.value.filter(challenge => challenge.difficultyLevel === selectedDifficulty.value)
})

const loadChallenges = async () => {
  try {
    loading.value = true
    const response = await challengeApi.getScenarios(selectedDifficulty.value)
    if (response.success) {
      challenges.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载挑战场景失败')
  } finally {
    loading.value = false
  }
}

const filterChallenges = () => {
  loadChallenges()
}

const viewChallenge = (id: number) => {
  router.push(`/challenge/detail/${id}`)
}

const startChallenge = async (id: number) => {
  try {
    const response = await challengeApi.startChallenge(id)
    if (response.success) {
      ElMessage.success('挑战开始成功')
      router.push(`/challenge/arena/${id}`)
    }
  } catch (error) {
    ElMessage.error('开始挑战失败')
  }
}

const getDifficultyTag = (level: string) => {
  const tagMap = {
    'beginner': 'success',
    'intermediate': 'warning',
    'advanced': 'danger',
    'expert': 'info'
  }
  return tagMap[level] || 'info'
}

const getDifficultyText = (level: string) => {
  const textMap = {
    'beginner': '初级',
    'intermediate': '中级',
    'advanced': '高级',
    'expert': '专家'
  }
  return textMap[level] || level
}

const getVulnerabilityChain = (chain: string) => {
  try {
    return JSON.parse(chain)
  } catch {
    return []
  }
}

onMounted(() => {
  loadChallenges()
})
</script>

<style scoped>
.challenge-list {
  padding: 20px;
}

.challenge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.challenge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.challenge-card {
  cursor: pointer;
  transition: all 0.3s;
}

.challenge-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.challenge-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.challenge-card-content {
  margin-bottom: 15px;
}

.challenge-meta {
  display: flex;
  gap: 15px;
  margin: 10px 0;
  color: #666;
}

.vulnerability-chain {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 10px;
}

.challenge-card-footer {
  text-align: right;
}

.loading {
  padding: 20px;
}
</style>