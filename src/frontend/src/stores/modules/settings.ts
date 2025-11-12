import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  // 状态
  const isDark = ref(false)
  const theme = ref('light')
  const primaryColor = ref('#409eff')
  const transitionName = ref('fade')
  const showBreadcrumb = ref(true)
  const showTagsView = ref(true)
  const showSidebarLogo = ref(true)
  const showFooter = ref(true)
  const fixedHeader = ref(true)
  const sidebarTextTheme = ref('dark')
  const uniqueOpened = ref(false)
  const showSettings = ref(false)
  const recordPosition = ref(true)
  const cachePage = ref(true)
  const messageDuration = ref(3000)
  const requestTimeout = ref(10000)
  const defaultPageSize = ref(10)
  const pageSizes = ref([10, 20, 50, 100])

  // 计算属性
  const isLight = computed(() => !isDark.value)
  const currentTheme = computed(() => theme.value)

  // 方法
  const setDark = (dark: boolean) => {
    isDark.value = dark
    theme.value = dark ? 'dark' : 'light'
    localStorage.setItem('is-dark', String(dark))
  }

  const toggleDark = () => {
    setDark(!isDark.value)
  }

  const setTheme = (themeValue: string) => {
    theme.value = themeValue
    localStorage.setItem('theme', themeValue)
  }

  const setPrimaryColor = (color: string) => {
    primaryColor.value = color
    localStorage.setItem('primary-color', color)

    // 动态设置CSS变量
    document.documentElement.style.setProperty('--el-color-primary', color)
  }

  const setTransitionName = (name: string) => {
    transitionName.value = name
    localStorage.setItem('transition-name', name)
  }

  const setShowBreadcrumb = (show: boolean) => {
    showBreadcrumb.value = show
    localStorage.setItem('show-breadcrumb', String(show))
  }

  const setShowTagsView = (show: boolean) => {
    showTagsView.value = show
    localStorage.setItem('show-tags-view', String(show))
  }

  const setShowSidebarLogo = (show: boolean) => {
    showSidebarLogo.value = show
    localStorage.setItem('show-sidebar-logo', String(show))
  }

  const setShowFooter = (show: boolean) => {
    showFooter.value = show
    localStorage.setItem('show-footer', String(show))
  }

  const setFixedHeader = (fixed: boolean) => {
    fixedHeader.value = fixed
    localStorage.setItem('fixed-header', String(fixed))
  }

  const setSidebarTextTheme = (textTheme: string) => {
    sidebarTextTheme.value = textTheme
    localStorage.setItem('sidebar-text-theme', textTheme)
  }

  const setUniqueOpened = (unique: boolean) => {
    uniqueOpened.value = unique
    localStorage.setItem('unique-opened', String(unique))
  }

  const setShowSettings = (show: boolean) => {
    showSettings.value = show
  }

  const setRecordPosition = (record: boolean) => {
    recordPosition.value = record
    localStorage.setItem('record-position', String(record))
  }

  const setCachePage = (cache: boolean) => {
    cachePage.value = cache
    localStorage.setItem('cache-page', String(cache))
  }

  const setMessageDuration = (duration: number) => {
    messageDuration.value = duration
    localStorage.setItem('message-duration', String(duration))
  }

  const setRequestTimeout = (timeout: number) => {
    requestTimeout.value = timeout
    localStorage.setItem('request-timeout', String(timeout))
  }

  const setDefaultPageSize = (size: number) => {
    defaultPageSize.value = size
    localStorage.setItem('default-page-size', String(size))
  }

  // 初始化设置
  const initSettings = () => {
    // 恢复主题设置
    const savedIsDark = localStorage.getItem('is-dark')
    if (savedIsDark !== null) {
      isDark.value = savedIsDark === 'true'
    } else {
      // 检测系统主题
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      isDark.value = prefersDark
    }

    // 恢复其他设置
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      theme.value = savedTheme
    }

    const savedPrimaryColor = localStorage.getItem('primary-color')
    if (savedPrimaryColor) {
      primaryColor.value = savedPrimaryColor
      document.documentElement.style.setProperty('--el-color-primary', savedPrimaryColor)
    }

    const savedTransitionName = localStorage.getItem('transition-name')
    if (savedTransitionName) {
      transitionName.value = savedTransitionName
    }

    const savedShowBreadcrumb = localStorage.getItem('show-breadcrumb')
    if (savedShowBreadcrumb !== null) {
      showBreadcrumb.value = savedShowBreadcrumb === 'true'
    }

    const savedShowTagsView = localStorage.getItem('show-tags-view')
    if (savedShowTagsView !== null) {
      showTagsView.value = savedShowTagsView === 'true'
    }

    const savedShowSidebarLogo = localStorage.getItem('show-sidebar-logo')
    if (savedShowSidebarLogo !== null) {
      showSidebarLogo.value = savedShowSidebarLogo === 'true'
    }

    const savedShowFooter = localStorage.getItem('show-footer')
    if (savedShowFooter !== null) {
      showFooter.value = savedShowFooter === 'true'
    }

    const savedFixedHeader = localStorage.getItem('fixed-header')
    if (savedFixedHeader !== null) {
      fixedHeader.value = savedFixedHeader === 'true'
    }

    const savedSidebarTextTheme = localStorage.getItem('sidebar-text-theme')
    if (savedSidebarTextTheme) {
      sidebarTextTheme.value = savedSidebarTextTheme
    }

    const savedUniqueOpened = localStorage.getItem('unique-opened')
    if (savedUniqueOpened !== null) {
      uniqueOpened.value = savedUniqueOpened === 'true'
    }

    const savedRecordPosition = localStorage.getItem('record-position')
    if (savedRecordPosition !== null) {
      recordPosition.value = savedRecordPosition === 'true'
    }

    const savedCachePage = localStorage.getItem('cache-page')
    if (savedCachePage !== null) {
      cachePage.value = savedCachePage === 'true'
    }

    const savedMessageDuration = localStorage.getItem('message-duration')
    if (savedMessageDuration) {
      messageDuration.value = parseInt(savedMessageDuration)
    }

    const savedRequestTimeout = localStorage.getItem('request-timeout')
    if (savedRequestTimeout) {
      requestTimeout.value = parseInt(savedRequestTimeout)
    }

    const savedDefaultPageSize = localStorage.getItem('default-page-size')
    if (savedDefaultPageSize) {
      defaultPageSize.value = parseInt(savedDefaultPageSize)
    }

    // 监听系统主题变化
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', e => {
      if (!localStorage.getItem('is-dark')) {
        isDark.value = e.matches
      }
    })
  }

  // 重置设置
  const resetSettings = () => {
    isDark.value = false
    theme.value = 'light'
    primaryColor.value = '#409eff'
    transitionName.value = 'fade'
    showBreadcrumb.value = true
    showTagsView.value = true
    showSidebarLogo.value = true
    showFooter.value = true
    fixedHeader.value = true
    sidebarTextTheme.value = 'dark'
    uniqueOpened.value = false
    showSettings.value = false
    recordPosition.value = true
    cachePage.value = true
    messageDuration.value = 3000
    requestTimeout.value = 10000
    defaultPageSize.value = 10

    // 清除localStorage
    localStorage.removeItem('is-dark')
    localStorage.removeItem('theme')
    localStorage.removeItem('primary-color')
    localStorage.removeItem('transition-name')
    localStorage.removeItem('show-breadcrumb')
    localStorage.removeItem('show-tags-view')
    localStorage.removeItem('show-sidebar-logo')
    localStorage.removeItem('show-footer')
    localStorage.removeItem('fixed-header')
    localStorage.removeItem('sidebar-text-theme')
    localStorage.removeItem('unique-opened')
    localStorage.removeItem('record-position')
    localStorage.removeItem('cache-page')
    localStorage.removeItem('message-duration')
    localStorage.removeItem('request-timeout')
    localStorage.removeItem('default-page-size')
  }

  return {
    // 状态
    isDark,
    theme,
    primaryColor,
    transitionName,
    showBreadcrumb,
    showTagsView,
    showSidebarLogo,
    showFooter,
    fixedHeader,
    sidebarTextTheme,
    uniqueOpened,
    showSettings,
    recordPosition,
    cachePage,
    messageDuration,
    requestTimeout,
    defaultPageSize,
    pageSizes,

    // 计算属性
    isLight,
    currentTheme,

    // 方法
    setDark,
    toggleDark,
    setTheme,
    setPrimaryColor,
    setTransitionName,
    setShowBreadcrumb,
    setShowTagsView,
    setShowSidebarLogo,
    setShowFooter,
    setFixedHeader,
    setSidebarTextTheme,
    setUniqueOpened,
    setShowSettings,
    setRecordPosition,
    setCachePage,
    setMessageDuration,
    setRequestTimeout,
    setDefaultPageSize,
    initSettings,
    resetSettings
  }
}, {
  persist: {
    key: 'settings-store',
    storage: localStorage,
    paths: ['isDark', 'theme', 'primaryColor', 'transitionName', 'showBreadcrumb', 'showTagsView', 'showSidebarLogo', 'showFooter', 'fixedHeader', 'sidebarTextTheme', 'uniqueOpened', 'recordPosition', 'cachePage', 'messageDuration', 'requestTimeout', 'defaultPageSize']
  }
})
