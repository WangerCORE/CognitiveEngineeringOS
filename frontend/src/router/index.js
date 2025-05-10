import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { setupGlobalErrorHandler } from '@/utils/errorHandler'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layouts/DefaultLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue')
        },
        {
          path: 'formatters',
          name: 'formatters',
          component: () => import('../views/FormatterListView.vue')
        },
        {
          path: 'formatters/create',
          name: 'formatter-create',
          component: () => import('../views/FormatterEditView.vue')
        },
        {
          path: 'formatters/:id',
          name: 'formatter-edit',
          component: () => import('../views/FormatterEditView.vue')
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: {
        title: '登录',
        requiresAuth: false
      }
    },
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: {
            title: '仪表盘',
            requiresAuth: true
          }
        },
        {
          path: 'sources',
          name: 'SourceManager',
          component: () => import('@/views/SourceManager.vue'),
          meta: {
            title: 'RSS源管理',
            requiresAuth: true
          }
        },
        {
          path: 'formatters',
          name: 'FormatterManager',
          component: () => import('@/views/FormatterManager.vue'),
          meta: {
            title: '格式化器管理',
            requiresAuth: true
          }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/Profile.vue'),
          meta: {
            title: '个人信息',
            requiresAuth: true
          }
        },
        {
          path: 'entries',
          component: () => import('@/views/entry/EntryList.vue'),
          meta: {
            title: 'RSS条目',
            requiresAuth: true
          }
        },
        {
          path: 'classifier',
          name: 'ClassifierManager',
          component: () => import('@/views/classifier/ClassifierManager.vue'),
          meta: {
            title: 'AI分类器管理',
            requiresAuth: true
          }
        },
        {
          path: 'reports',
          name: 'ReportManager',
          component: () => import('@/views/report/ReportManager.vue'),
          meta: {
            title: '报告管理',
            requiresAuth: true
          }
        },
        {
          path: 'archives',
          name: 'ArchiveManager',
          component: () => import('@/views/archive/ArchiveManager.vue'),
          meta: {
            title: '文件归档',
            requiresAuth: true
          }
        }
      ]
    },
    {
      path: '/formatter',
      component: () => import('@/layouts/DefaultLayout.vue'),
      children: [
        {
          path: '',
          name: 'FormatterManager',
          component: () => import('@/views/FormatterManager.vue'),
          meta: {
            title: '格式化器管理',
            icon: 'Document'
          }
        }
      ]
    },
    {
      path: '/error/:code',
      name: 'Error',
      component: () => import('@/views/error/ErrorPage.vue'),
      props: true
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/error/404'
    }
  ]
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // 设置页面标题
  document.title = `${to.meta.title} - CE-OS管理系统`

  // 检查是否需要认证
  if (requiresAuth) {
    // 如果未登录，重定向到登录页
    if (!authStore.isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 如果已登录但没有用户信息，获取用户信息
    if (!authStore.user) {
      try {
        await authStore.fetchCurrentUser()
      } catch (error) {
        // 如果获取用户信息失败，可能是token过期，清除token并重定向到登录页
        authStore.logout()
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }
  }

  // 如果已登录且访问登录页，重定向到首页
  if (to.path === '/login' && authStore.isAuthenticated) {
    next({ path: '/' })
    return
  }

  next()
})

// 设置全局错误处理
setupGlobalErrorHandler()

export default router 