<template>
  <div class="test-features">
    <h1>功能测试页面</h1>

    <!-- 语言切换测试 -->
    <div class="feature-section">
      <h2>语言切换功能</h2>
      <LanguageSwitch />
    </div>

    <!-- 新手指引测试 -->
    <div class="feature-section">
      <h2>新手指引功能</h2>
      <GuideTrigger @trigger="handleTriggerGuide" />
    </div>

    <!-- 新手指引组件 -->
    <UserGuide
      ref="userGuideRef"
      :auto-show="false"
      @complete="handleGuideComplete"
      @skip="handleGuideSkip"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LanguageSwitch from '@/components/LanguageSwitch.vue'
import GuideTrigger from '@/components/GuideTrigger.vue'
import UserGuide from '@/components/UserGuide.vue'

// 指引组件引用
const userGuideRef = ref()

// 处理指引触发
const handleTriggerGuide = () => {
  userGuideRef.value?.triggerGuide()
}

// 处理指引完成
const handleGuideComplete = () => {
  console.log('用户完成新手指引')
}

// 处理指引跳过
const handleGuideSkip = () => {
  console.log('用户跳过新手指引')
}
</script>

<style scoped>
.test-features {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.feature-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.feature-section h2 {
  margin-top: 0;
  color: #333;
}
</style>
