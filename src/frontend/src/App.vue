<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useAppStore } from '@/stores/modules/app'
import { useSettingsStore } from '@/stores/modules/settings'
import { useUserStore } from '@/stores/modules/user'

const appStore = useAppStore()
const settingsStore = useSettingsStore()
const userStore = useUserStore()

onMounted(() => {
  console.log('🚀 Java Web安全教学系统前端启动')

  // 初始化设置
  settingsStore.initSettings()

  // 检查登录状态
  userStore.checkLoginStatus()

  // 初始化应用状态
  // 修复：使用正确的方法名
  appStore.initAppState()
})

onUnmounted(() => {
  console.log('📱 应用卸载')
})
</script>
