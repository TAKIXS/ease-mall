<script setup>
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <!-- 顶部导航栏 -->
  <el-menu
    mode="horizontal"
    :ellipsis="false"
    background-color="#409eff"
    text-color="#fff"
    active-text-color="#fff"
  >
    <el-menu-item index="0" @click="router.push('/home')">
      <h2 style="color:white;margin:0">🛒 ease-mall</h2>
    </el-menu-item>
    <div style="flex-grow:1"></div>

    <template v-if="!auth.isLoggedIn()">
      <el-menu-item index="1" @click="router.push('/login')">登录</el-menu-item>
      <el-menu-item index="2" @click="router.push('/register')">注册</el-menu-item>
    </template>
    <template v-else>
      <el-menu-item index="3" @click="router.push('/cart')">🛒 购物车</el-menu-item>
      <el-menu-item index="4" @click="router.push('/orders')">📋 我的订单</el-menu-item>
      <el-menu-item index="5">
        <span>{{ auth.user?.username }}</span>
      </el-menu-item>
      <el-menu-item index="6" @click="handleLogout">退出</el-menu-item>
    </template>
  </el-menu>

  <!-- 页面内容区 -->
  <div style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <router-view />
  </div>
</template>

<style>
body { margin: 0; font-family: 'Microsoft YaHei', sans-serif; }
</style>
