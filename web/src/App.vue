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
  <!-- 顶部导航栏（渐变蓝） -->
  <div class="header">
    <div class="header-inner">
      <div class="logo" @click="router.push('/home')">
        <span class="logo-icon">🛒</span>
        <span class="logo-text">ease-mall</span>
      </div>
      <div class="nav-links">
        <template v-if="!auth.isLoggedIn()">
          <el-button type="primary" link @click="router.push('/login')">登录</el-button>
          <el-button type="primary" link style="color:#fff" @click="router.push('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-badge :value="0" :hidden="true">
            <el-button type="primary" link @click="router.push('/cart')">🛒 购物车</el-button>
          </el-badge>
          <el-button type="primary" link @click="router.push('/orders')">📋 我的订单</el-button>
          <span class="user-tag">👤 {{ auth.user?.username }}</span>
          <el-button type="primary" link @click="handleLogout">退出</el-button>
        </template>
      </div>
    </div>
  </div>

  <!-- 页面内容 -->
  <div class="main-content">
    <router-view />
  </div>

  <!-- 底部 -->
  <div class="footer">
    <p>© 2024 ease-mall · Java Spring Boot + Vue3 全栈项目</p>
  </div>
</template>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif; background: #f5f7fa; color: #333; }

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 56px;
  gap: 24px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}
.logo-icon { font-size: 26px; }
.logo-text { font-size: 22px; font-weight: 700; letter-spacing: 1px; }
.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: flex-end;
}
.nav-links .el-button--primary.link { color: rgba(255,255,255,0.9); font-size: 15px; }
.nav-links .el-button--primary.link:hover { color: #fff; }
.user-tag { color: rgba(255,255,255,0.9); font-size: 14px; }

.main-content { max-width: 1200px; margin: 24px auto; padding: 0 20px; min-height: 70vh; }

.footer {
  text-align: center;
  padding: 24px;
  color: #999;
  font-size: 13px;
  background: #fff;
  border-top: 1px solid #eee;
}
</style>
