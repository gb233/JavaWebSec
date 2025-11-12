import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

interface PermissionState {
  routes: RouteRecordRaw[]
  dynamicRoutesLoaded: boolean
  buttonPermissions: string[]
}

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const dynamicRoutesLoaded = ref(false)
  const buttonPermissions = ref<string[]>([])

  const setRoutes = (newRoutes: RouteRecordRaw[]) => {
    routes.value = newRoutes
  }

  const markDynamicRoutesLoaded = (loaded: boolean) => {
    dynamicRoutesLoaded.value = loaded
  }

  const setButtonPermissions = (permissions: string[]) => {
    buttonPermissions.value = permissions
  }

  const reset = () => {
    routes.value = []
    dynamicRoutesLoaded.value = false
    buttonPermissions.value = []
  }

  return {
    routes,
    dynamicRoutesLoaded,
    buttonPermissions,
    setRoutes,
    markDynamicRoutesLoaded,
    setButtonPermissions,
    reset
  }
})

export type PermissionStore = ReturnType<typeof usePermissionStore>
