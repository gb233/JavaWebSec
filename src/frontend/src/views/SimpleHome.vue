<template>
  <div class="simple-home">
    <h1>欢迎使用Java Web安全教学系统</h1>
    <p>这是一个简化的首页，用于测试前端应用是否正常工作。</p>

    <div class="actions">
      <ElButton type="primary" @click="goToTest">
        测试页面
      </ElButton>
      <ElButton @click="goToLogin">
        登录
      </ElButton>
    </div>

    <div class="status">
      <h3>系统状态</h3>
      <p>前端应用: ✅ 正常运行</p>
      <p>路由系统: ✅ 正常工作</p>
      <p>组件渲染: ✅ 正常工作</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToTest = () => {
  router.push('/test')
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.simple-home {
  text-align: center;
  padding: 2rem;
}

.actions {
  margin: 2rem 0;
}

.actions .el-button {
  margin: 0 0.5rem;
}

.status {
  margin-top: 2rem;
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 8px;
  text-align: left;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.status h3 {
  margin-top: 0;
  color: #409eff;
}
</style>
