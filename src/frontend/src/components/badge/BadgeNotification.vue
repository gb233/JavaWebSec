<template>
  <div v-if="visible" class="badge-notification">
    <div class="notification-content">
      <div class="badge-icon">
        {{ notification.badgeIcon }}
      </div>
      <div class="notification-text">
        <h3>{{ notification.message }}</h3>
        <p>{{ notification.badgeDescription }}</p>
        <div v-if="notification.pointsReward" class="points-reward">
          +{{ notification.pointsReward }} 积分
        </div>
      </div>
      <button class="close-btn" @click="close">
        ×
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface BadgeNotification {
  type: string
  badgeId: number
  badgeName: string
  badgeIcon: string
  badgeDescription: string
  pointsReward?: number
  message: string
}

const visible = ref(false)
const notification = ref<BadgeNotification>({
  type: '',
  badgeId: 0,
  badgeName: '',
  badgeIcon: '',
  badgeDescription: '',
  message: ''
})

let autoCloseTimer: NodeJS.Timeout | null = null

const show = (data: BadgeNotification) => {
  notification.value = data
  visible.value = true

  // 自动关闭
  autoCloseTimer = setTimeout(() => {
    close()
  }, 5000)
}

const close = () => {
  visible.value = false
  if (autoCloseTimer) {
    clearTimeout(autoCloseTimer)
    autoCloseTimer = null
  }
}

// 监听WebSocket通知
onMounted(() => {
  // TODO: 实现WebSocket监听
  // window.addEventListener('badgeNotification', (event) => {
  //   show(event.detail)
  // })
})

onUnmounted(() => {
  if (autoCloseTimer) {
    clearTimeout(autoCloseTimer)
  }
})

defineExpose({
  show,
  close
})
</script>

<style scoped>
.badge-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

.notification-content {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
  min-width: 300px;
}

.badge-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.notification-text {
  flex: 1;
}

.notification-text h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.notification-text p {
  margin: 0 0 8px 0;
  font-size: 14px;
  opacity: 0.9;
}

.points-reward {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  display: inline-block;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>
