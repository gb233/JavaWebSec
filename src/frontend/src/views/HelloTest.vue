<template>
  <div>
    <h1>Hello Test Page</h1>
    <p>如果你看到这个页面，说明Vue组件正常工作！</p>
    <button @click="testClick">点击测试</button>
    <p v-if="clicked">按钮已点击！</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const clicked = ref(false)

const testClick = () => {
  clicked.value = true
  console.log('按钮被点击了！')
}
</script>

<style scoped>
h1 {
  color: #409eff;
}
button {
  background: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
