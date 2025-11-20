import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/styles/index.scss'
import '@/permission'
import GlobalComponents from '@/components/global'
import pinia from '@/stores'
import i18n from '@/i18n'

// 创建应用实例
const app = createApp(App)

// 创建Pinia实例
// 注册插件
app.use(pinia)
app.use(router)
app.use(i18n)

// 强制使用中文语言包，避免Element Plus显示英文
app.use(ElementPlus, {
  locale: zhCn, // 强制使用中文，避免英文显示
  size: 'default'
})
app.use(GlobalComponents)

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err, info)
}

// 挂载应用
app.mount('#app')

console.log('🚀 Java Web安全教学系统前端启动成功!')
