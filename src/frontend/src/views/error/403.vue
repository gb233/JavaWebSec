<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <ElIcon :size="120" color="#f56c6c">
          <CircleCloseFilled />
        </ElIcon>
      </div>

      <h1 class="error-title">
        403
      </h1>
      <p class="error-message">
        权限不足
      </p>
      <p class="error-description">
        抱歉，您没有访问此页面的权限。请联系管理员或检查您的账户权限。
      </p>

      <div class="error-actions">
        <ElButton type="primary" @click="goHome">
          <ElIcon><House /></ElIcon>
          返回首页
        </ElButton>
        <ElButton @click="goBack">
          <ElIcon><Back /></ElIcon>
          返回上页
        </ElButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { CircleCloseFilled, House, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped lang="scss">
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f5f7fa;
}

.error-content {
  text-align: center;
  padding: 40px;

  .error-icon {
    margin-bottom: 24px;
  }

  .error-title {
    font-size: 72px;
    font-weight: 700;
    color: #f56c6c;
    margin: 0 0 16px 0;
  }

  .error-message {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px 0;
  }

  .error-description {
    font-size: 16px;
    color: #606266;
    line-height: 1.6;
    margin: 0 0 32px 0;
    max-width: 500px;
  }

  .error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}
</style>
