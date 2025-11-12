<template>
  <div class="placeholder-view">
    <div class="placeholder-content">
      <div class="placeholder-icon">
        <ElIcon :size="80" color="#dcdfe6">
          <component :is="icon" />
        </ElIcon>
      </div>

      <h2 class="placeholder-title">
        {{ title }}
      </h2>
      <p class="placeholder-description">
        {{ description }}
      </p>

      <div v-if="showActions" class="placeholder-actions">
        <ElButton type="primary" @click="$router.push('/')">
          <ElIcon><House /></ElIcon>
          返回首页
        </ElButton>
        <ElButton @click="$router.go(-1)">
          <ElIcon><Back /></ElIcon>
          返回上页
        </ElButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { House, Back, Tools } from '@element-plus/icons-vue'

interface Props {
  title: string
  description?: string
  icon?: string
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  description: '该功能正在开发中，敬请期待！',
  icon: 'Tools',
  showActions: true
})
</script>

<style scoped lang="scss">
.placeholder-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px 20px;
}

.placeholder-content {
  text-align: center;
  max-width: 500px;

  .placeholder-icon {
    margin-bottom: 24px;
  }

  .placeholder-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px 0;
  }

  .placeholder-description {
    font-size: 16px;
    color: #606266;
    line-height: 1.6;
    margin: 0 0 32px 0;
  }

  .placeholder-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}
</style>
