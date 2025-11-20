/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 环境变量类型声明
interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_VERSION: string
  readonly VITE_APP_API_BASE_URL: string
  readonly VITE_APP_BASE_URL: string
  readonly VITE_APP_ENV: 'development' | 'test' | 'production'
  readonly VITE_APP_MOCK: 'true' | 'false'
  readonly VITE_APP_DEBUG: 'true' | 'false'
  readonly VITE_APP_UPLOAD_URL: string
  readonly VITE_APP_WS_URL: string
  readonly VITE_APP_GITHUB_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 全局类型声明
declare global {
  // 应用版本信息
  declare const __APP_VERSION__: string
  declare const __BUILD_TIME__: string

  // Window对象扩展
  interface Window {
    // Monaco Editor
    monaco?: typeof import('monaco-editor')

    // 全局事件总线
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    $eventBus?: any
  }

  // 通用类型
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  type Recordable<T = any> = Record<string, T>
  type Nullable<T> = T | null
  type NonNullable<T> = T extends null | undefined ? never : T
  type Arrayable<T> = T | T[]
  type Awaitable<T> = T | Promise<T>

  // 函数类型
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  type Fn<T = any> = (...args: any[]) => T // eslint-disable-line @typescript-eslint/no-explicit-any
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  type PromiseFn<T = any> = (...args: any[]) => Promise<T> // eslint-disable-line @typescript-eslint/no-explicit-any

  // 响应数据类型
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  interface ApiResponse<T = any> {
    code: number
    message: string
    data: T
    timestamp?: number
    path?: string
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    requestId?: string
  }

  // 分页数据类型
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  interface PageResult<T = any> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
  }

  // 分页查询参数
  interface PageQuery {
    current?: number
    size?: number
    keyword?: string
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    [key: string]: any
  }

  // 用户信息类型
  interface UserInfo {
    id: number
    username: string
    email: string
    fullName?: string
    avatar?: string
    role: string
    permissions?: string[]
    lastLoginAt?: string
    createdAt: string
  }

  // 路由Meta类型
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    keepAlive?: boolean
    requireAuth?: boolean
    roles?: string[]
    permissions?: string[]
    activeMenu?: string
    noCache?: boolean
    breadcrumb?: boolean
    affix?: boolean
  }
}

export {}
