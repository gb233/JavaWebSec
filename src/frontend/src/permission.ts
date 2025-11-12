// 权限控制 - 已移至 router/index.ts 中统一管理
// 此文件保留用于其他权限相关功能

import { useAppStore } from '@/stores/modules/app'

// 权限相关工具函数
export const hasPermission = (permission: string): boolean => {
  // 权限检查逻辑
  return true // 临时返回true，后续可扩展
}

export const hasRole = (role: string): boolean => {
  // 角色检查逻辑
  return true // 临时返回true，后续可扩展
}

// 添加访问记录
export const addVisitedView = (route: any) => {
  const appStore = useAppStore()
  appStore.addVisitedView(route)
  appStore.addCachedView(route)
}