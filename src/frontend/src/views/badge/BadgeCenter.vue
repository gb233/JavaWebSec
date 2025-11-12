<template>
  <div class="badge-center">
    <div class="page-header">
      <h1>成就徽章中心</h1>
      <p>展示您的学习成就和徽章收藏</p>
    </div>
    
    <div class="badge-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.icon }} {{ tab.label }}
      </button>
    </div>
    
    <div class="tab-content">
      <BadgeShowcase v-if="activeTab === 'showcase'" />
      <BadgeProgress v-if="activeTab === 'progress'" />
      <BadgeStats v-if="activeTab === 'stats'" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BadgeShowcase from '@/components/badge/BadgeShowcase.vue'
import BadgeProgress from '@/components/badge/BadgeProgress.vue'
import BadgeStats from '@/components/badge/BadgeStats.vue'

const activeTab = ref('showcase')

const tabs = [
  { key: 'showcase', label: '徽章展示', icon: '🏆' },
  { key: 'progress', label: '徽章进度', icon: '📊' },
  { key: 'stats', label: '徽章统计', icon: '📈' }
]
</script>

<style scoped>
.badge-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.badge-tabs {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 12px 24px;
  border: 2px solid #e9ecef;
  background: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  font-weight: 500;
}

.tab-btn:hover {
  border-color: #007bff;
  background: #f8f9fa;
}

.tab-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

.tab-content {
  min-height: 400px;
}
</style>
