// 全局组件注册
import type { App } from 'vue'
import LanguageSwitch from '@/components/LanguageSwitch.vue'
import GuideTrigger from '@/components/GuideTrigger.vue'
import UserGuide from '@/components/UserGuide.vue'

// 这里可以注册全局组件
export default {
  install(app: App) {
    // 注册全局组件
    app.component('LanguageSwitch', LanguageSwitch)
    app.component('GuideTrigger', GuideTrigger)
    app.component('UserGuide', UserGuide)

    console.log('全局组件注册完成')
  }
}