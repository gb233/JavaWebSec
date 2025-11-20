<template>
  <ElDropdown
    id="language-switch"
    trigger="click"
    placement="bottom-end"
    @command="handleLanguageChange"
  >
    <ElButton type="text" class="language-switch-btn">
      <ElIcon><Setting /></ElIcon>
      <span class="language-text">{{ currentLanguage.displayName }}</span>
      <ElIcon class="el-icon--right">
        <ArrowDown />
      </ElIcon>
    </ElButton>

    <template #dropdown>
      <ElDropdownMenu>
        <ElDropdownItem
          v-for="language in supportedLanguages"
          :key="language.languageCode"
          :command="language.languageCode"
          :class="{ 'is-active': language.languageCode === currentLanguage.languageCode }"
        >
          <span class="language-option">
            <span class="language-name">{{ language.displayName }}</span>
            <ElIcon v-if="language.languageCode === currentLanguage.languageCode" class="check-icon">
              <Check />
            </ElIcon>
          </span>
        </ElDropdownItem>
      </ElDropdownMenu>
    </template>
  </ElDropdown>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, ArrowDown, Check } from '@element-plus/icons-vue'
import { languageApi, type LanguagePreference } from '@/api/language'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

// 响应式数据
const currentLanguage = ref<LanguagePreference>({
  languageCode: 'zh-CN',
  languageName: '中文',
  displayName: '简体中文'
})

const supportedLanguages = ref<LanguagePreference[]>([])

// 计算属性
const isChanging = ref(false)

// 方法
const loadCurrentLanguage = async () => {
  try {
    // 优先从localStorage获取用户语言偏好
    const savedLanguage = localStorage.getItem('user-language')
    if (savedLanguage) {
      // 等待支持的语言列表加载完成
      if (supportedLanguages.value.length > 0) {
        const language = supportedLanguages.value.find(lang => lang.languageCode === savedLanguage)
        if (language) {
          currentLanguage.value = language
          locale.value = savedLanguage
          return
        }
      }
    }

    // 如果localStorage没有或语言不支持，使用默认中文
    currentLanguage.value = {
      languageCode: 'zh-CN',
      languageName: '中文',
      displayName: '简体中文'
    }
    locale.value = 'zh-CN'
    localStorage.setItem('user-language', 'zh-CN')

    // 可选：尝试从API获取（但不依赖它）
    try {
      const response = await languageApi.getCurrentLanguage()
      if (response.success && response.data) {
        // 如果API返回成功，更新本地设置
        currentLanguage.value = response.data
        locale.value = response.data.languageCode
        localStorage.setItem('user-language', response.data.languageCode)
      }
    } catch (apiError) {
      // API错误不影响本地功能，只记录警告
      console.warn('获取后端语言偏好失败，使用本地设置:', apiError)
    }
  } catch (error) {
    console.error('语言设置初始化失败:', error)
    // 出错时使用默认中文
    currentLanguage.value = {
      languageCode: 'zh-CN',
      languageName: '中文',
      displayName: '简体中文'
    }
    locale.value = 'zh-CN'
    localStorage.setItem('user-language', 'zh-CN')
  }
}

const loadSupportedLanguages = async () => {
  try {
    const response = await languageApi.getSupportedLanguages()
    if (response.success && response.data) {
      supportedLanguages.value = response.data
    }
  } catch (error) {
    console.warn('获取支持语言列表失败，使用默认语言列表:', error)
    // 使用默认支持的语言列表
    supportedLanguages.value = [
      {
        languageCode: 'zh-CN',
        languageName: '中文',
        displayName: '简体中文'
      },
      {
        languageCode: 'en-US',
        languageName: 'English',
        displayName: 'English'
      }
    ]
  }
}

const handleLanguageChange = async (languageCode: string) => {
  if (isChanging.value || languageCode === currentLanguage.value.languageCode) {
    return
  }

  isChanging.value = true

  try {
    // 立即更新UI和localStorage
    const selectedLanguage = supportedLanguages.value.find(lang => lang.languageCode === languageCode)
    if (selectedLanguage) {
      currentLanguage.value = selectedLanguage
      locale.value = languageCode

      // 保存用户语言偏好到localStorage
      localStorage.setItem('user-language', languageCode)
      localStorage.setItem('language', languageCode)

      ElMessage.success(
        languageCode === 'zh-CN'
          ? '语言切换成功'
          : 'Language switched successfully'
      )

      // 尝试保存到后端（可选）
      try {
        await languageApi.setLanguage(languageCode)
      } catch (apiError) {
        console.warn('保存语言偏好到后端失败，但本地设置已保存:', apiError)
      }

      // 设置语言切换标记，避免触发新手指引
      sessionStorage.setItem('language-switching', 'true')

      // 刷新页面以应用语言更改
      setTimeout(() => {
        window.location.reload()
      }, 1000)
    } else {
      ElMessage.error('不支持的语言代码')
    }
  } catch (error) {
    console.error('语言切换失败:', error)
    ElMessage.error('语言切换失败')
  } finally {
    isChanging.value = false
  }
}

// 生命周期
onMounted(async () => {
  // 先加载支持的语言列表
  await loadSupportedLanguages()
  // 然后加载当前语言
  await loadCurrentLanguage()
})
</script>

<style scoped>
.language-switch-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  color: var(--el-text-color-primary);
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  background: var(--el-bg-color);
  transition: all 0.3s;
}

.language-switch-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.language-text {
  font-size: 14px;
  font-weight: 500;
}

.language-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-width: 120px;
}

.language-name {
  font-size: 14px;
}

.check-icon {
  color: var(--el-color-primary);
  font-size: 16px;
}

.is-active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .language-text {
    display: none;
  }

  .language-switch-btn {
    padding: 8px;
  }
}
</style>
