import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/home' },

  // 用户
  { path: '/login', name: 'Login', component: () => import('../views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/user/Register.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/user/Profile.vue') },

  // 商品
  { path: '/home', name: 'Home', component: () => import('../views/product/Home.vue') },
  { path: '/product/:id', name: 'ProductDetail', component: () => import('../views/product/Detail.vue') },

  // 订单
  { path: '/cart', name: 'Cart', component: () => import('../views/order/Cart.vue') },
  { path: '/orders', name: 'Orders', component: () => import('../views/order/MyOrders.vue') },

  // 管理员
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue') },
  { path: '/admin/dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
