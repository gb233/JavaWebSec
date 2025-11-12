import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import type { App } from 'vue'

// 创建pinia实例
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 安装插件
export function setupStore(app: App<Element>) {
  app.use(pinia)
}

export { pinia }
export default pinia

// 导出所有store模块
export * from './modules/app'
export * from './modules/user'
export * from './modules/settings'
export * from './modules/permission'
export * from './modules/learning'
export * from './modules/test'
export * from './modules/challenge'
