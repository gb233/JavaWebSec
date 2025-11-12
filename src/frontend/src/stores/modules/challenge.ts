import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

type ChallengeStatus = 'LOCKED' | 'AVAILABLE' | 'IN_PROGRESS' | 'COMPLETED'

interface ChallengeProgress {
  challengeId: string
  status: ChallengeStatus
  score?: number
  updatedAt: number
}

export const useChallengeStore = defineStore('challenge', () => {
  const progressList = ref<ChallengeProgress[]>([])
  const activeChallengeId = ref<string>('')

  const completedChallenges = computed(() =>
    progressList.value.filter(item => item.status === 'COMPLETED')
  )

  const setActiveChallenge = (challengeId: string) => {
    activeChallengeId.value = challengeId
  }

  const updateChallenge = (progress: ChallengeProgress) => {
    const index = progressList.value.findIndex(item => item.challengeId === progress.challengeId)
    if (index === -1) {
      progressList.value.push(progress)
    } else {
      progressList.value[index] = progress
    }
  }

  const resetChallenges = () => {
    progressList.value = []
    activeChallengeId.value = ''
  }

  return {
    progressList,
    activeChallengeId,
    completedChallenges,
    setActiveChallenge,
    updateChallenge,
    resetChallenges
  }
}, {
  persist: {
    key: 'challenge-store',
    storage: localStorage,
    paths: ['progressList', 'activeChallengeId']
  }
})

export type ChallengeStore = ReturnType<typeof useChallengeStore>
