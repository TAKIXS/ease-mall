<script setup>
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'
import { Shop, ShoppingCart, List, User, SwitchButton } from '@element-plus/icons-vue'

const auth = useAuthStore()
const router = useRouter()
function handleLogout() { auth.logout(); router.push('/login') }
</script>

<template>
  <!-- ♨ 顶部导航 — 暖棕渐变 -->
  <div class="topbar">
    <div class="topbar-inner">
      <div class="logo" @click="router.push('/home')">
        <el-icon :size="24"><Shop /></el-icon>
        <span class="logo-text">悠然商城</span>
      </div>
      <div class="nav-links">
        <template v-if="!auth.isLoggedIn()">
          <button class="btn-ghost" @click="router.push('/login')">登录</button>
          <button class="btn-outline" @click="router.push('/register')">注册</button>
        </template>
        <template v-else>
          <button class="btn-ghost" @click="router.push('/cart')">
            <el-icon :size="16"><ShoppingCart /></el-icon> 购物车
          </button>
          <button class="btn-ghost" @click="router.push('/orders')">
            <el-icon :size="16"><List /></el-icon> 我的订单
          </button>
          <span class="user-badge">
            <el-icon :size="14"><User /></el-icon> {{ auth.user?.username }}
          </span>
          <button class="btn-ghost" @click="handleLogout">
            <el-icon :size="16"><SwitchButton /></el-icon> 退出
          </button>
        </template>
      </div>
    </div>
  </div>

  <!-- 内容区 -->
  <div class="page">
    <router-view />
  </div>

  <!-- 底部 -->
  <div class="footer">
    <div class="footer-svg">
      <svg viewBox="0 0 1200 60" fill="none"><path d="M0 30 Q150 0 300 30 T600 30 T900 30 T1200 30" stroke="#C4A484" stroke-width="2" fill="none" opacity="0.4"/></svg>
    </div>
    <p>© 2024 悠然商城 · 用心做好每一笔订单</p>
  </div>
</template>

<style>
/* ===== ♨ Global Warm Theme ===== */
:root {
  --bg:       #F9F5F0;
  --card:     #FFFAF5;
  --brown:    #8B5E3C;
  --brown-lt: #A67C52;
  --brown-pale: #C4A484;
  --text:     #4A3728;
  --text-lt:  #8B7355;
  --shadow:   0 4px 24px rgba(139,94,60,0.08);
  --shadow-lg: 0 12px 48px rgba(139,94,60,0.10);
  --radius:   18px;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

/* Focus: 可见焦点环（键盘导航） */
:focus-visible {
  outline: 2px solid var(--brown);
  outline-offset: 2px;
  border-radius: 4px;
}

/* Reduced motion: 尊重用户系统偏好 */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}

body {
  font-family: 'Nunito Sans', 'Microsoft YaHei', sans-serif;
  background: var(--bg);
  color: var(--text);
  -webkit-font-smoothing: antialiased;
}

/* ===== Topbar ===== */
.topbar {
  background: linear-gradient(135deg, #8B5E3C 0%, #A67C52 50%, #8B5E3C 100%);
  padding: 0 24px; position: sticky; top: 0; z-index: 1000;
  box-shadow: 0 2px 20px rgba(139,94,60,0.2);
}
.topbar-inner {
  max-width: 1200px; margin: 0 auto; display: flex;
  align-items: center; height: 56px; gap: 20px;
}
.logo { display: flex; align-items: center; gap: 8px; cursor: pointer; user-select: none; color: #FFFAF5; }
.logo-text { font-size: 22px; font-weight: 700; letter-spacing: 1px; }
.nav-links { display: flex; align-items: center; gap: 8px; flex: 1; justify-content: flex-end; }
.user-badge { display: flex; align-items: center; gap: 4px; color: rgba(255,250,245,0.85); font-size: 14px; }

.btn-ghost {
  background: none; border: none; color: rgba(255,250,245,0.85);
  font-size: 15px; font-family: inherit; cursor: pointer; padding: 8px 14px; border-radius: 10px;
  transition: all .2s; display: flex; align-items: center; gap: 4px;
}
.btn-ghost:hover { background: rgba(255,255,255,0.12); color: #fff; }

.btn-outline {
  background: transparent; border: 1.5px solid rgba(255,250,245,0.5); color: #fff;
  font-size: 15px; font-family: inherit; cursor: pointer; padding: 7px 18px; border-radius: 10px;
  transition: all .2s;
}
.btn-outline:hover { background: rgba(255,255,255,0.15); border-color: #fff; }

/* ===== Page ===== */
.page { max-width: 1200px; margin: 28px auto; padding: 0 20px; min-height: 70vh; }

/* ===== Footer ===== */
.footer { text-align: center; padding: 32px 20px; background: #FFFAF5; color: var(--brown-pale); font-size: 13px; }
.footer-svg { max-width: 600px; margin: 0 auto 12px; }
</style>
