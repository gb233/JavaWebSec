import { createI18n } from 'vue-i18n'
import zhCN from '@/locales/zh-CN'
import enUS from '@/locales/en-US'

// 获取浏览器语言
const getBrowserLanguage = () => {
  const lang = navigator.language || navigator.languages[0]
  if (lang.startsWith('zh')) {
    return 'zh-CN'
  }
  return 'zh-CN' // 默认使用中文
}

// 从localStorage获取保存的语言设置
const getSavedLanguage = () => {
  // 优先使用用户语言偏好
  const userLanguage = localStorage.getItem('user-language')
  if (userLanguage) {
    return userLanguage
  }
  
  // 其次使用通用语言设置
  const savedLanguage = localStorage.getItem('language')
  if (savedLanguage) {
    return savedLanguage
  }
  
  // 默认使用中文
  return 'zh-CN'
}

const i18n = createI18n({
  legacy: false, // 使用 Composition API
  locale: getSavedLanguage(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
})

export default i18n
