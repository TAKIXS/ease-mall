import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

// ★ Pinia Store：全局管理登录状态
export const useAuthStore = defineStore('auth', () => {
  // 响应式状态（类似 data）
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // 注册
  async function register(form) {
    await request.post('/auth/register', form)
  }

  // 登录
  async function login(form) {
    const res = await request.post('/auth/login', form)
    token.value = res.data.token
    user.value = { id: res.data.userId, username: res.data.username, nickname: res.data.nickname }
    // 持久化到 localStorage（刷新不丢失）
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  // 退出
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 是否已登录
  const isLoggedIn = () => !!token.value

  return { token, user, register, login, logout, isLoggedIn }
})
