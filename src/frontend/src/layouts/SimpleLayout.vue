<template>
  <div class="simple-layout">
    <header class="header">
      <h1>{{ $t('common.title') }}</h1>
      <nav>
        <RouterLink to="/test">
          {{ $t('nav.test') }}
        </RouterLink>
        <RouterLink to="/test-features">
          {{ $t('nav.features') }}
        </RouterLink>
        <RouterLink to="/simple-test">
          {{ $t('nav.simpleTest') }}
        </RouterLink>
        <RouterLink to="/basic-test">
          {{ $t('nav.basicTest') }}
        </RouterLink>
        <RouterLink to="/hello-test">
          {{ $t('nav.helloTest') }}
        </RouterLink>
        <RouterLink to="/login">
          {{ $t('auth.login') }}
        </RouterLink>
        <!-- 语言切换 -->
        <LanguageSwitch />
        <!-- 新手指引触发按钮 -->
        <GuideTrigger @trigger="handleTriggerGuide" />
      </nav>
    </header>
    <main class="main">
      <RouterView />
    </main>

    <!-- 新手指引组件 -->
    <UserGuide
      ref="userGuideRef"
      :auto-show="true"
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
.simple-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #409eff;
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  margin: 0;
  font-size: 1.5rem;
}

.header nav {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.header nav a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.header nav a:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main {
  flex: 1;
  padding: 2rem;
}
</style>
