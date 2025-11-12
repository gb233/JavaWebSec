import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface LearningProgress {
  vulnerabilityId: string
  completed: boolean
  lastVisitedAt: number
  score?: number
}

export const useLearningStore = defineStore('learning', () => {
  const progressMap = ref<Record<string, LearningProgress>>({})
  const currentTopic = ref<string>('')

  const completedCount = computed(() =>
    Object.values(progressMap.value).filter(item => item.completed).length
  )

  const setCurrentTopic = (topic: string) => {
    currentTopic.value = topic
  }

  const updateProgress = (progress: LearningProgress) => {
    progressMap.value[progress.vulnerabilityId] = progress
  }

  const clearProgress = () => {
    progressMap.value = {}
    currentTopic.value = ''
  }

  return {
    progressMap,
    currentTopic,
    completedCount,
    setCurrentTopic,
    updateProgress,
    clearProgress
  }
}, {
  persist: {
    key: 'learning-store',
    storage: localStorage,
    paths: ['progressMap', 'currentTopic']
  }
})

export type LearningStore = ReturnType<typeof useLearningStore>
