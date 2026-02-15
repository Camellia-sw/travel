import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/frontend/Home.vue'

// 前台路由配置
const frontendRoutes = [
    {
        path: '/',
        component: () => import('@/layouts/FrontendLayout.vue'),
        children: [
            {
                path: '',
                name: 'Home',
                component: () => import('@/views/frontend/Home.vue'),
                meta: { title: '首页' }
            },
            // {
            //     path: 'profile',
            //     name: 'Profile',
            //     component: () => import('@/views/frontend/profile/index.vue'),
            //     meta: { title: '个人中心', requiresAuth: true }
            // },
            // {
            //     path: 'accommodation',
            //     name: 'AccommodationList',
            //     component: () => import('@/views/frontend/accommodation/index.vue'),
            //     meta: { title: '周边住宿' }
            // },
            // {
            //     path: 'accommodation/:id',
            //     name: 'AccommodationDetail',
            //     component: () => import('@/views/frontend/accommodation/detail.vue'),
            //     meta: { title: '住宿详情' }
            // },
            // {
            //     path: 'tickets',
            //     name: 'Tickets',
            //     component: () => import('@/views/frontend/ticket/index.vue'),
            //     meta: { title: '门票预订' }
            // },
            // {
            //     path: 'ticket/booking/:id',
            //     name: 'TicketBooking',
            //     component: () => import('@/views/frontend/ticket/booking.vue'),
            //     meta: { title: '预订门票', requiresAuth: true }
            // },
            // {
            //     path: 'orders',
            //     name: 'Orders',
            //     component: () => import('@/views/frontend/orders/index.vue'),
            //     meta: { title: '我的订单', requiresAuth: true }
            // },
            // {
            //     path: 'scenic',
            //     name: 'ScenicList',
            //     component: () => import('@/views/frontend/scenic/ScenicList.vue'),
            //     meta: { title: '景点列表' }
            // },
            // {
            //     path: 'scenic/:id',
            //     name: 'ScenicDetail',
            //     component: () => import('@/views/frontend/scenic/ScenicDetail.vue'),
            //     meta: { title: '景点详情' }
            // },
            // {
            //     path: 'scenic/category/:categoryId',
            //     name: 'ScenicByCategory',
            //     component: () => import('@/views/frontend/scenic/ScenicList.vue'),
            //     props: true,
            //     meta: { title: '分类景点' }
            // },
            // {
            //     path: 'guide',
            //     name: 'GuideList',
            //     component: () => import('@/views/frontend/guide/GuideList.vue'),
            //     meta: { title: '攻略列表' }
            // },
            // {
            //     path: 'guide/detail/:id',
            //     name: 'GuideDetail',
            //     component: () => import('@/views/frontend/guide/GuideDetail.vue'),
            //     meta: { title: '攻略详情' }
            // },
            // {
            //     path: 'guide/edit',
            //     name: 'GuideEdit',
            //     component: () => import('@/views/frontend/guide/GuideEdit.vue'),
            //     meta: { title: '发布攻略', requiresAuth: true }
            // },
            // {
            //     path: 'my-guide',
            //     name: 'MyGuideList',
            //     component: () => import('@/views/frontend/guide/MyGuideList.vue'),
            //     meta: { title: '我的攻略', requiresAuth: true }
            // },
            // {
            //     path: 'collection',
            //     name: 'Collection',
            //     component: () => import('@/views/frontend/collection/MyCollection.vue'),
            //     meta: { title: '我的收藏', requiresAuth: true }
            // },
            // {
            //     path: 'payment/alipay/:id',
            //     name: 'AlipayPayment',
            //     component: () => import('@/views/frontend/payment/alipay-form.vue'),
            //     meta: { title: '支付宝支付', requiresAuth: true }
            // },
            // {
            //     path: 'payment-failed',
            //     name: 'PaymentFailed',
            //     component: () => import('@/views/frontend/payment/payment-failed.vue'),
            //     meta: { title: '支付失败' }
            // },
            // {
            //     path: 'payment/result',
            //     name: 'PaymentResult',
            //     component: () => import('@/views/frontend/payment/payment-result.vue'),
            //     meta: { title: '支付结果' }
            // }
        ]
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
    }
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        ...frontendRoutes,
        // ...backendRoutes,
        // ...errorRoutes
    ]
})

export default router
