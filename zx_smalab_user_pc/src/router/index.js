import { createRouter, createWebHistory } from 'vue-router'

// 导入路由模块
import homeRoute from './modules/home'
import courseRoute from './modules/course'
import profileRoute from './modules/profile'
import schoolRoute from './modules/school'
import aboutRoute from './modules/about'
import loginRoute from './modules/login'
import registerRoute from './modules/register'

/**
 * 路由配置
 * path: 路由路径
 * name: 路由名称
 * component: 路由组件
 * meta: 路由元信息
 * - meta.title: 页面标题
 * - meta.showInNav: 是否在导航栏显示
 * - meta.navOrder: 导航栏顺序
 * - meta.showNavbar: 是否显示顶部导航栏 (默认: true)
 * - meta.showFooter: 是否显示页脚 (默认: true)
 */

// 合并所有路由模块
const routes = [
  homeRoute,
  courseRoute,
  profileRoute,
  schoolRoute,
  aboutRoute,
  loginRoute,
  registerRoute
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '智慧学堂'
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
