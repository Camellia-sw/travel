import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/frontend/Home.vue'
import { useUserStore } from '@/store/user'
import BackendLayout from '@/layouts/BackendLayout.vue'

// 前台路由配置
const frontendRoutes = [
    {
        path: '/',
        component: () => import('@/layouts/FrontendLayout.vue'),
        children: [
            {path: '', name: 'Home', component: () => import('@/views/frontend/Home.vue'), meta: { title: '首页' }},
        ]
    },
    {path: '/login', name: 'Login', component: () => import('@/views/auth/Login.vue'), meta: { title: '登录' }},
    {path: '/register', name: 'Register', component: () => import('@/views/auth/Register.vue'), meta: { title: '注册' }}
]

// 后台路由
export const backendRoutes = [
    {
        path: '/back',
        component: BackendLayout,
        redirect: '/back/manager',
        children: [
            {path: 'manager', name: 'Manager', component: () => import('@/views/backend/Manager.vue'), meta: { title: '首页', icon: 'HomeFilled' }},
            {path: 'user', name: 'UserManagement', component: () => import('@/views/backend/user/index.vue'), meta: { title: '用户管理', icon: 'User' }},
            {
                path: 'profile',
                name: 'BackendProfile',
                component: () => import('@/views/backend/user/PersonInfo.vue'),
                meta: { title: '个人信息', icon: 'UserFilled' }
            },
        ]
    }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        ...frontendRoutes,
        ...backendRoutes,
        // ...errorRoutes
    ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 旅游信息系统`
    }

    const userStore = useUserStore()
    console.log("Current route:", to.path)
    console.log("User status:", {
        isLoggedIn: userStore.isLoggedIn,
        isUser: userStore.isUser
    })

    // 检查是否需要登录权限
    if (to.matched.some(record => record.meta.requiresAuth) && !userStore.isLoggedIn) {
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
        return
    }

    // 已登录用户的路由控制
    if (userStore.isLoggedIn) {
        // 处理登录页面访问
        if (to.path === '/login') {
            next(userStore.isUser ? '/' : '/back/manager')
            return
        }

        if (!userStore.isUser) {
            // 非普通用户只能访问后台路由
            if (to.path.startsWith('/back')) {
                next()
            } else {
                next('/back/manager')
            }
            return
        } else {
            // 普通用户只能访问前台路由
            if (to.path.startsWith('/back')) {
                next('/')
            } else {
                next()
            }
            return
        }
    } else {
        // 未登录用户
        if (to.path.startsWith('/back')) {
            next('/login')
            return
        }
    }

    next()
})

export default router
