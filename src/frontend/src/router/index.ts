import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'

// 布局组件
const Layout = () => import('@/layouts/index.vue')

// 基础路由（无需权限）
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: {
      title: '用户登录',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: {
      title: '用户注册',
      hidden: true,
      requireAuth: false
    }
  },
  // 忘记密码功能暂时注释掉 - 2025-01-15
  // {
  //   path: '/forgot-password',
  //   name: 'ForgotPassword',
  //   component: () => import('@/views/auth/ForgotPassword.vue'),
  //   meta: {
  //     title: '忘记密码',
  //     hidden: true,
  //     requireAuth: false
  //   }
  // },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/test-new-features',
    name: 'TestNewFeatures',
    component: () => import('@/views/TestNewFeatures.vue'),
    meta: {
      title: '新功能测试',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/test',
    name: 'TestPage',
    component: () => import('@/views/TestPage.vue'),
    meta: {
      title: '测试页面',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/test-features',
    name: 'TestFeatures',
    component: () => import('@/views/TestFeatures.vue'),
    meta: {
      title: '功能测试',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/simple-test',
    name: 'SimpleTest',
    component: () => import('@/views/SimpleTest.vue'),
    meta: {
      title: '简单测试',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/basic-test',
    name: 'BasicTest',
    component: () => import('@/views/BasicTest.vue'),
    meta: {
      title: '基础测试',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/hello-test',
    name: 'HelloTest',
    component: () => import('@/views/HelloTest.vue'),
    meta: {
      title: 'Hello测试',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '权限不足',
      hidden: true,
      requireAuth: false
    }
  },
  {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/error/500.vue'),
    meta: {
      title: '服务器错误',
      hidden: true,
      requireAuth: false
    }
  }
]

// 主要路由（需要权限）
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '控制台',
          icon: 'House',
          affix: true,
          requireAuth: true
        }
      },
      {
        path: 'test-connection',
        name: 'TestConnection',
        component: () => import('@/views/TestConnection.vue'),
        meta: {
          title: '联调测试',
          icon: 'Connection',
          requireAuth: false
        }
      },
      {
        path: 'test-knowledge',
        name: 'TestKnowledge',
        component: () => import('@/views/TestKnowledge.vue'),
        meta: {
          title: '知识中心测试',
          icon: 'Tools',
          requireAuth: false
        }
      }
    ]
  },
  {
    path: '/knowledge',
    component: Layout,
    redirect: '/knowledge/center',
    meta: {
      title: '漏洞知识中心',
      icon: 'Reading',
      requireAuth: true
    },
    children: [
      {
        path: 'center',
        name: 'KnowledgeCenter',
        component: () => import('@/views/learning/KnowledgeCenter.vue'),
        meta: {
          title: '知识中心',
          icon: 'Reading',
          requireAuth: true
        }
      },
      {
        path: 'category/:code',
        name: 'VulnerabilityCategory',
        component: () => import('@/views/learning/VulnerabilityCategory.vue'),
        meta: {
          title: '漏洞分类',
          requireAuth: true,
          activeMenu: '/knowledge/center'
        }
      },
      {
        path: 'vulnerability/:id',
        name: 'VulnerabilityDetail',
        component: () => import('@/views/learning/VulnerabilityDetail.vue'),
        meta: {
          title: '漏洞详情',
          hidden: true,
          requireAuth: true,
          activeMenu: '/knowledge/center'
        }
      }
    ]
  },
  {
    path: '/learning/:catchAll(.*)*',
    redirect: '/knowledge/center',
    meta: {
      hidden: true,
      requireAuth: true
    }
  },
  {
    path: '/test',
    component: Layout,
    redirect: '/test/categories',
    meta: {
      title: '知识测试',
      icon: 'EditPen',
      requireAuth: true
    },
    children: [
      {
        path: 'categories',
        name: 'TestCategories',
        component: () => import('@/views/test/TestCategories.vue'),
        meta: {
          title: '测试分类',
          icon: 'Folder',
          requireAuth: true
        }
      },
      {
        path: 'exam/:categoryId',
        name: 'TestExam',
        component: () => import('@/views/test/TestExam.vue'),
        meta: {
          title: '在线测试',
          hidden: true,
          requireAuth: true,
          activeMenu: '/test/categories'
        }
      },
      {
        path: 'records',
        name: 'TestRecords',
        component: () => import('@/views/test/TestRecords.vue'),
        meta: {
          title: '测试记录',
          icon: 'Document',
          requireAuth: true
        }
      },
      {
        path: 'result/:recordId',
        name: 'TestResult',
        component: () => import('@/views/test/TestResult.vue'),
        meta: {
          title: '测试结果',
          hidden: true,
          requireAuth: true,
          activeMenu: '/test/records'
        }
      }
    ]
  },
  {
    path: '/challenge',
    component: Layout,
    redirect: '/challenge/list',
    meta: {
      title: '挑战模式',
      icon: 'Trophy',
      requireAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'ChallengeList',
        component: () => import('@/views/challenge/ChallengeList.vue'),
        meta: {
          title: '挑战列表',
          icon: 'List',
          requireAuth: true
        }
      },
      {
        path: 'detail/:id',
        name: 'ChallengeDetail',
        component: () => import('@/views/challenge/ChallengeDetail.vue'),
        meta: {
          title: '挑战详情',
          hidden: true,
          requireAuth: true,
          activeMenu: '/challenge/list'
        }
      },
      {
        path: 'arena/:id',
        name: 'ChallengeArena',
        component: () => import('@/views/challenge/ChallengeArena.vue'),
        meta: {
          title: '挑战环境',
          hidden: true,
          requireAuth: true,
          activeMenu: '/challenge/list'
        }
      }
      // 排行榜功能暂时注释掉 - 2025-01-15
      // {
      //   path: 'leaderboard',
      //   name: 'ChallengeLeaderboard',
      //   component: () => import('@/views/challenge/ChallengeLeaderboard.vue'),
      //   meta: {
      //     title: '排行榜',
      //     icon: 'TrendCharts',
      //     requireAuth: true
      //   }
      // }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/info',
    meta: {
      title: '个人中心',
      icon: 'User',
      requireAuth: true
    },
    children: [
      {
        path: 'info',
        name: 'ProfileInfo',
        component: () => import('@/views/user/ProfileView.vue'),
        meta: {
          title: '个人信息',
          icon: 'UserFilled',
          requireAuth: true
        }
      },
      {
        path: 'achievements',
        name: 'ProfileAchievements',
        component: () => import('@/views/badge/BadgeCenter.vue'),
        meta: {
          title: '成就徽章',
          icon: 'Medal',
          requireAuth: true
        }
      },
      {
        path: 'notes',
        name: 'ProfileNotes',
        component: () => import('@/views/profile/ProfileNotes.vue'),
        meta: {
          title: '学习笔记',
          icon: 'Notebook',
          requireAuth: true
        }
      },
      {
        path: 'collections',
        name: 'ProfileCollections',
        component: () => import('@/views/profile/ProfileCollections.vue'),
        meta: {
          title: '我的收藏',
          icon: 'Star',
          requireAuth: true
        }
      }
    ]
  },
  // 笔记相关路由
  {
    path: '/notes',
    component: Layout,
    redirect: '/notes/create',
    meta: {
      requireAuth: true
    },
    children: [
      {
        path: 'create',
        name: 'NoteCreate',
        component: () => import('@/views/notes/NoteCreate.vue'),
        meta: {
          title: '创建笔记',
          requireAuth: true
        }
      },
      {
        path: ':id',
        name: 'NoteDetail',
        component: () => import('@/views/notes/NoteDetail.vue'),
        meta: {
          title: '笔记详情',
          requireAuth: true
        }
      },
      {
        path: ':id/edit',
        name: 'NoteEdit',
        component: () => import('@/views/notes/NoteEdit.vue'),
        meta: {
          title: '编辑笔记',
          requireAuth: true
        }
      }
    ]
  },
  // 收藏夹相关路由
  {
    path: '/collections',
    component: Layout,
    redirect: '/collections/create',
    meta: {
      requireAuth: true
    },
    children: [
      {
        path: 'create',
        name: 'CollectionCreate',
        component: () => import('@/views/collections/CollectionCreate.vue'),
        meta: {
          title: '创建收藏夹',
          requireAuth: true
        }
      },
      {
        path: ':id',
        name: 'CollectionDetail',
        component: () => import('@/views/collections/CollectionDetail.vue'),
        meta: {
          title: '收藏夹详情',
          requireAuth: true
        }
      },
      {
        path: ':id/edit',
        name: 'CollectionEdit',
        component: () => import('@/views/collections/CollectionEdit.vue'),
        meta: {
          title: '编辑收藏夹',
          requireAuth: true
        }
      }
    ]
  },
  // 系统管理模块 - 所有管理功能页面均为占位符或未完成联调，暂时全部注释 - 2025-01-15
  // 后端AttackLogController注释说明："前端日志管理/攻击日志模块尚未完成联调，目前仅用于后续演示与扩展"
  // 前端logs/index.vue注释说明："攻击日志前端仍在开发中，当前页面仅提供占位视图，接口数据未在正式环境上线"
  // {
  //   path: '/admin',
  //   component: Layout,
  //   redirect: '/admin/logs',
  //   meta: {
  //     title: '系统管理',
  //     icon: 'Tools',
  //     requireAuth: true,
  //     roles: ['admin']
  //   },
  //   children: [
  //     {
  //       path: 'users',
  //       name: 'AdminUsers',
  //       component: () => import('@/views/admin/users/index.vue'),
  //       meta: {
  //         title: '用户管理',
  //         icon: 'UserFilled',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     },
  //     {
  //       path: 'vulnerabilities',
  //       name: 'AdminVulnerabilities',
  //       component: () => import('@/views/admin/vulnerabilities/index.vue'),
  //       meta: {
  //         title: '漏洞管理',
  //         icon: 'Monitor',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     },
  //     {
  //       path: 'questions',
  //       name: 'AdminQuestions',
  //       component: () => import('@/views/admin/questions/index.vue'),
  //       meta: {
  //         title: '题库管理',
  //         icon: 'EditPen',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     },
  //     {
  //       path: 'challenges',
  //       name: 'AdminChallenges',
  //       component: () => import('@/views/admin/challenges/index.vue'),
  //       meta: {
  //         title: '挑战管理',
  //         icon: 'Trophy',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     },
  //     {
  //       path: 'system',
  //       name: 'AdminSystem',
  //       component: () => import('@/views/admin/system/index.vue'),
  //       meta: {
  //         title: '系统配置',
  //         icon: 'Setting',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     },
  //     {
  //       path: 'logs',
  //       name: 'AdminLogs',
  //       component: () => import('@/views/admin/logs/index.vue'),
  //       meta: {
  //         title: '日志管理',
  //         icon: 'Document',
  //         requireAuth: true,
  //         roles: ['admin']
  //       }
  //     }
  //   ]
  // }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior(to: any, from: any, savedPosition: any) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由前置守卫
router.beforeEach(async (to: any, from: any, next: any) => {
  // 开始进度条
  NProgress.start()

  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - Java Web安全教学系统`
  }

  // 导入认证store（动态导入避免循环依赖）
  const { useAuthStore } = await import('@/stores/modules/auth')
  const authStore = useAuthStore()

  // 初始化认证状态（仅在首次访问时）
  if (!authStore.isLoggedIn && !authStore.isLoading) {
    await authStore.initializeAuth()
  }

  // 检查路由是否需要认证
  if (to.meta.requireAuth) {
    if (!authStore.isLoggedIn) {
      // 未登录用户访问需要认证的页面，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  // 检查已登录用户是否应该隐藏某些页面（如登录、注册页）
  if ((to.name === 'Login' || to.name === 'Register') && authStore.isLoggedIn) {
    // 已登录用户访问登录/注册页，重定向到控制台
    next('/dashboard')
    return
  }

  next()
})

// 路由后置守卫
router.afterEach(() => {
  // 结束进度条
  NProgress.done()
})

// 路由错误处理
router.onError((error: any) => {
  console.error('Router error:', error)
  NProgress.done()
})

// 404重定向
router.addRoute({
  path: '/:pathMatch(.*)*',
  redirect: '/404'
})

export default router
