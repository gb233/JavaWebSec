<template>
  <div class="app-layout" :class="{ 'no-sidebar': !authStore.isLoggedIn }">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo">
          <ElIcon :size="32" color="#409eff">
            <SecurityIcon />
          </ElIcon>
          <span class="logo-text">{{ $t('common.title') }}</span>
        </div>

        <div v-if="authStore.isLoggedIn" class="header-actions">
          <!-- 语言切换 -->
          <LanguageSwitch />
          <!-- 新手指引触发按钮 -->
          <GuideTrigger @trigger="handleTriggerGuide" />
          
          <ElDropdown @command="handleCommand">
            <span class="user-dropdown">
              <ElAvatar :size="32" :src="authStore.userAvatar">
                <ElIcon><UserFilled /></ElIcon>
              </ElAvatar>
              <span class="username">{{ authStore.userDisplayName }}</span>
              <ElIcon><ArrowDown /></ElIcon>
            </span>
            <template #dropdown>
              <ElDropdownMenu>
                <ElDropdownItem command="profile">
                  <ElIcon><User /></ElIcon>{{ $t('nav.profile') }}
                </ElDropdownItem>
                <ElDropdownItem divided command="logout">
                  <ElIcon><SwitchButton /></ElIcon>{{ $t('nav.logout') }}
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>

        <div v-else class="header-actions">
          <ElButton @click="$router.push('/login')">
            {{ $t('auth.login') }}
          </ElButton>
          <ElButton type="primary" @click="$router.push('/register')">
            {{ $t('auth.register') }}
          </ElButton>
        </div>
      </div>
    </header>

    <!-- 侧边导航栏 -->
    <aside v-if="authStore.isLoggedIn" class="app-sidebar">
      <ElMenu
        :default-active="currentRoute"
        router
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <ElMenuItem index="/dashboard">
          <ElIcon><House /></ElIcon>
          <span>{{ $t('nav.dashboard') }}</span>
        </ElMenuItem>

        <ElSubMenu index="knowledge">
          <template #title>
            <ElIcon><Reading /></ElIcon>
            <span>{{ $t('nav.vulnerability') }}</span>
          </template>
          <ElMenuItem index="/knowledge/center">
            <span>{{ $t('nav.knowledgeCenter') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A01">
            <span>A01 {{ $t('knowledge.a01.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A02">
            <span>A02 {{ $t('knowledge.a02.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A03">
            <span>A03 {{ $t('knowledge.a03.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A04">
            <span>A04 {{ $t('knowledge.a04.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A05">
            <span>A05 {{ $t('knowledge.a05.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A06">
            <span>A06 {{ $t('knowledge.a06.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A07">
            <span>A07 {{ $t('knowledge.a07.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A08">
            <span>A08 {{ $t('knowledge.a08.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A09">
            <span>A09 {{ $t('knowledge.a09.name') }}</span>
          </ElMenuItem>
          <ElMenuItem index="/knowledge/category/A10">
            <span>A10 {{ $t('knowledge.a10.name') }}</span>
          </ElMenuItem>
        </ElSubMenu>

        <ElSubMenu index="test">
          <template #title>
            <ElIcon><EditPen /></ElIcon>
            <span>{{ $t('nav.test') }}</span>
          </template>
          <ElMenuItem index="/test/categories">
            {{ $t('nav.testCategories') }}
          </ElMenuItem>
          <ElMenuItem index="/test/records">
            {{ $t('nav.testRecords') }}
          </ElMenuItem>
        </ElSubMenu>


        <ElSubMenu index="challenge">
          <template #title>
            <ElIcon><Trophy /></ElIcon>
            <span>{{ $t('nav.challenge') }}</span>
          </template>
          <ElMenuItem index="/challenge/list">
            {{ $t('nav.challengeList') }}
          </ElMenuItem>
          <!-- 排行榜功能暂时注释掉 - 2025-01-15 -->
          <!-- <ElMenuItem index="/challenge/leaderboard">
            {{ $t('nav.leaderboard') }}
          </ElMenuItem> -->
        </ElSubMenu>

        <ElSubMenu index="profile">
          <template #title>
            <ElIcon><User /></ElIcon>
            <span>{{ $t('nav.profile') }}</span>
          </template>
          <ElMenuItem index="/profile/info">
            {{ $t('profile.basicInfo') }}
          </ElMenuItem>
          <ElMenuItem index="/profile/achievements">
            {{ $t('profile.achievements') }}
          </ElMenuItem>
          <ElMenuItem index="/profile/notes">
            {{ $t('profile.notes') }}
          </ElMenuItem>
          <ElMenuItem index="/profile/collections">
            {{ $t('profile.favorites') }}
          </ElMenuItem>
        </ElSubMenu>

        <ElSubMenu v-if="authStore.isAdmin" index="admin">
          <template #title>
            <ElIcon><Tools /></ElIcon>
            <span>系统管理</span>
          </template>
          <ElMenuItem index="/admin/users">
            用户管理
          </ElMenuItem>
          <ElMenuItem index="/admin/vulnerabilities">
            漏洞管理
          </ElMenuItem>
          <ElMenuItem index="/admin/questions">
            题库管理
          </ElMenuItem>
          <ElMenuItem index="/admin/challenges">
            挑战管理
          </ElMenuItem>
          <ElMenuItem index="/admin/system">
            系统配置
          </ElMenuItem>
          <ElMenuItem index="/admin/logs">
            日志管理
          </ElMenuItem>
        </ElSubMenu>
      </ElMenu>
    </aside>

    <!-- 主要内容区域 -->
    <main ref="mainRef" class="app-main">
      <RouterView />
    </main>

    <!-- 页脚（包含GitHub链接） -->
    <footer v-if="authStore.isLoggedIn" class="app-footer">
      <div class="footer-content">
        <div class="footer-left">
          <span>© 2024 Java Web安全教学系统</span>
        </div>
        <div class="footer-right">
          <ElLink
            :href="githubUrl"
            target="_blank"
            type="primary"
            :underline="false"
          >
            <ElIcon :size="16" style="margin-right: 4px">
              <Link />
            </ElIcon>
            <span>GitHub</span>
          </ElLink>
        </div>
      </div>
    </footer>
    
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
import { computed, ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// 修复图标导入问题 - 使用确定存在的图标
import {
  Lock as SecurityIcon, // 使用Lock图标替代Shield
  UserFilled,
  ArrowDown,
  User,
  Setting,
  SwitchButton,
  House,
  Reading,
  Monitor,
  Collection,
  DataLine,
  EditPen,
  Trophy,
  Tools,
  Warning,
  Lock,
  Connection,
  Refresh,
  Link,
  VideoPlay
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { ElMessage } from 'element-plus'
import LanguageSwitch from '@/components/LanguageSwitch.vue'
import GuideTrigger from '@/components/GuideTrigger.vue'
import UserGuide from '@/components/UserGuide.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const mainRef = ref<HTMLElement | null>(null)

// 指引组件引用
const userGuideRef = ref()

// GitHub链接（从环境变量或默认值获取）
const githubUrl = (import.meta.env.VITE_APP_GITHUB_URL || 
  (typeof __GITHUB_URL__ !== 'undefined' ? __GITHUB_URL__ : 'https://github.com/javaweb-security/teaching-system')) as string

if (import.meta.env.DEV) {
  console.debug('Layout 初始化', {
    isLoggedIn: authStore.isLoggedIn,
    user: authStore.user
  })
}

const currentRoute = computed(() => (route.meta?.activeMenu as string) || route.path)

const resetScroll = () => {
  if (mainRef.value) {
    mainRef.value.scrollTop = 0
  }
}

watch(
  () => route.fullPath,
  () => {
    resetScroll()
  }
)

onMounted(() => {
    resetScroll()
})

const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile/info')
      break
    case 'logout':
      await authStore.logout()
      ElMessage.success('已退出登录')
      // 登出后会自动跳转到登录页面（在auth store中处理）
      break
  }
}

const handleMenuSelect = (index: string) => {
  if (import.meta.env.DEV) {
    console.debug('Selected menu:', index)
  }
}

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

<style scoped lang="scss">
.app-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  grid-template-rows: 60px 1fr;
  grid-template-areas:
    'header header'
    'sidebar main';
  min-height: 100vh;
  background: #f5f7fa;
}

.app-layout.no-sidebar {
  grid-template-columns: 1fr;
  grid-template-rows: 60px 1fr;
  grid-template-areas:
    'header'
    'main';
}

.app-header {
  grid-area: header;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 10;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    height: 60px;

    .logo {
      display: flex;
      align-items: center;
      gap: 12px;

      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .user-dropdown {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 12px;
        border-radius: 6px;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f5f7fa;
        }

        .username {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
}

.app-sidebar {
  grid-area: sidebar;
  width: 240px;
  background: white;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  height: calc(100vh - 60px);

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(144, 147, 153, 0.3);
    border-radius: 3px;
  }

  .sidebar-menu {
    border-right: none;
    min-height: calc(100vh - 60px);
  }
}

.app-footer {
  grid-column: 1 / -1;
  background: white;
  border-top: 1px solid #e4e7ed;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  color: #909399;

  .footer-content {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .footer-left {
    display: flex;
    align-items: center;
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 16px;
  }
}

.app-main {
  grid-area: main;
  background: #f5f7fa;
  overflow-y: auto;
  padding: 24px;
}

// 响应式设计
@media (max-width: 768px) {
  .app-sidebar {
    display: none; // 移动端隐藏侧边栏
  }

  .app-main {
    padding: 16px;
  }

  .header-content {
    padding: 0 16px !important;

    .logo-text {
      display: none; // 移动端隐藏logo文字
    }
  }
}
</style>
