import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 状态
  const loading = ref(false)
  const sidebar = ref({
    opened: true,
    withoutAnimation: false
  })
  const device = ref('desktop')
  const size = ref('default')
  const language = ref('zh-cn')
  const cachedViews = ref<string[]>([])
  const visitedViews = ref<any[]>([])
  const searchVisible = ref(false)
  const fullscreen = ref(false)

  // 计算属性
  const isMobile = computed(() => device.value === 'mobile')
  const isDesktop = computed(() => device.value === 'desktop')
  const sidebarOpened = computed(() => sidebar.value.opened)

  // 方法
  const setLoading = (status: boolean) => {
    loading.value = status
  }

  const toggleSidebar = (withoutAnimation = false) => {
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = withoutAnimation

    // 保存到localStorage
    localStorage.setItem('sidebar-opened', String(sidebar.value.opened))
  }

  const closeSidebar = (withoutAnimation = false) => {
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation

    localStorage.setItem('sidebar-opened', 'false')
  }

  const openSidebar = (withoutAnimation = false) => {
    sidebar.value.opened = true
    sidebar.value.withoutAnimation = withoutAnimation

    localStorage.setItem('sidebar-opened', 'true')
  }

  const toggleDevice = (deviceType: string) => {
    device.value = deviceType

    // 移动端自动收起侧边栏
    if (deviceType === 'mobile') {
      closeSidebar(true)
    }
  }

  const setSize = (sizeValue: string) => {
    size.value = sizeValue
    localStorage.setItem('element-size', sizeValue)
  }

  const setLanguage = (lang: string) => {
    language.value = lang
    localStorage.setItem('language', lang)
  }

  // 标签页管理
  const addVisitedView = (view: any) => {
    if (visitedViews.value.some(v => v.path === view.path)) return

    visitedViews.value.push({
      name: view.name,
      path: view.path,
      title: view.meta?.title || 'No Title',
      meta: { ...view.meta }
    })
  }

  const addCachedView = (view: any) => {
    if (cachedViews.value.includes(view.name)) return

    if (view.meta?.keepAlive) {
      cachedViews.value.push(view.name)
    }
  }

  const delVisitedView = (view: any) => {
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index !== -1) {
      visitedViews.value.splice(index, 1)
    }
  }

  const delCachedView = (view: any) => {
    const index = cachedViews.value.findIndex(name => name === view.name)
    if (index !== -1) {
      cachedViews.value.splice(index, 1)
    }
  }

  const delOthersVisitedViews = (view: any) => {
    visitedViews.value = visitedViews.value.filter(v => {
      return v.meta?.affix || v.path === view.path
    })
  }

  const delOthersCachedViews = (view: any) => {
    cachedViews.value = cachedViews.value.filter(name => name === view.name)
  }

  const delAllVisitedViews = () => {
    visitedViews.value = visitedViews.value.filter(view => view.meta?.affix)
  }

  const delAllCachedViews = () => {
    cachedViews.value = []
  }

  const updateVisitedView = (view: any) => {
    for (let v of visitedViews.value) {
      if (v.path === view.path) {
        v = Object.assign(v, view)
        break
      }
    }
  }

  // 搜索相关
  const toggleSearch = () => {
    searchVisible.value = !searchVisible.value
  }

  const closeSearch = () => {
    searchVisible.value = false
  }

  const openSearch = () => {
    searchVisible.value = true
  }

  // 全屏相关
  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen().then(() => {
        fullscreen.value = true
      })
    } else {
      document.exitFullscreen().then(() => {
        fullscreen.value = false
      })
    }
  }

  const exitFullscreen = () => {
    if (document.fullscreenElement) {
      document.exitFullscreen().then(() => {
        fullscreen.value = false
      })
    }
  }

  // 初始化应用状态
  const initAppState = () => {
    // 恢复侧边栏状态
    const sidebarStatus = localStorage.getItem('sidebar-opened')
    if (sidebarStatus !== null) {
      sidebar.value.opened = sidebarStatus === 'true'
    }

    // 恢复组件尺寸
    const elementSize = localStorage.getItem('element-size')
    if (elementSize) {
      size.value = elementSize
    }

    // 恢复语言设置
    const savedLanguage = localStorage.getItem('language')
    if (savedLanguage) {
      language.value = savedLanguage
    }

    // 检测设备类型
    const { userAgent } = navigator
    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent)) {
      device.value = 'mobile'
      sidebar.value.opened = false
    } else {
      device.value = 'desktop'
    }

    // 监听全屏状态变化
    document.addEventListener('fullscreenchange', () => {
      fullscreen.value = !!document.fullscreenElement
    })

    // 监听窗口大小变化
    window.addEventListener('resize', () => {
      const width = window.innerWidth
      if (width < 768) {
        device.value = 'mobile'
        closeSidebar(true)
      } else {
        device.value = 'desktop'
      }
    })
  }

  // 重置应用状态
  const resetAppState = () => {
    loading.value = false
    sidebar.value = {
      opened: true,
      withoutAnimation: false
    }
    cachedViews.value = []
    visitedViews.value = []
    searchVisible.value = false
    fullscreen.value = false
  }

  return {
    // 状态
    loading,
    sidebar,
    device,
    size,
    language,
    cachedViews,
    visitedViews,
    searchVisible,
    fullscreen,

    // 计算属性
    isMobile,
    isDesktop,
    sidebarOpened,

    // 方法
    setLoading,
    toggleSidebar,
    closeSidebar,
    openSidebar,
    toggleDevice,
    setSize,
    setLanguage,
    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    delOthersVisitedViews,
    delOthersCachedViews,
    delAllVisitedViews,
    delAllCachedViews,
    updateVisitedView,
    toggleSearch,
    closeSearch,
    openSearch,
    toggleFullscreen,
    exitFullscreen,
    initAppState,
    resetAppState
  }
}, {
  persist: {
    key: 'app-store',
    storage: localStorage,
    paths: ['sidebar.opened', 'size', 'language', 'device']
  }
})
