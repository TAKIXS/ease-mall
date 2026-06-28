<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) { ElMessage.warning('请填写用户名和密码'); return }
  try { await auth.login(form); ElMessage.success('欢迎回来~'); router.push('/home') } catch (e) {}
}
</script>

<template>
  <div class="center-page">
    <div class="card">
      <!-- 手绘风装饰 SVG -->
      <div class="card-art">
        <svg viewBox="0 0 120 80" fill="none">
          <ellipse cx="60" cy="35" rx="30" ry="22" fill="#F0E6D8" stroke="#C4A484" stroke-width="2"/>
          <circle cx="48" cy="28" r="3" fill="#8B5E3C"/>
          <circle cx="68" cy="28" r="3" fill="#8B5E3C"/>
          <path d="M50 42 Q60 52 70 42" stroke="#8B5E3C" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <h2>欢迎回来</h2>
        <p>登录你的 ease-mall 账号</p>
      </div>

      <el-form :model="form" size="large" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" class="warm-input" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
            show-password @keyup.enter="handleLogin" class="warm-input" />
        </el-form-item>
        <el-form-item>
          <el-button class="warm-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>

      <p class="link-text">还没有账号？<a @click="router.push('/register')">立即注册</a></p>
    </div>
  </div>
</template>

<style scoped>
.center-page { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.card {
  width: 420px; background: var(--card); border-radius: var(--radius);
  box-shadow: var(--shadow-lg); padding: 44px 40px 36px;
}
.card-art { text-align: center; margin-bottom: 28px; }
.card-art h2 { margin: 8px 0 4px; font-size: 22px; color: var(--text); font-weight: 700; }
.card-art p { color: var(--text-lt); font-size: 14px; margin: 0; }

:deep(.warm-input .el-input__wrapper) {
  background: var(--bg); border-radius: 12px; border: 1.5px solid #E8DDD0; box-shadow: none;
}
:deep(.warm-input .el-input__wrapper:hover) { border-color: var(--brown-pale); }
:deep(.warm-input .el-input__wrapper.is-focus) { border-color: var(--brown); box-shadow: 0 0 0 2px rgba(139,94,60,0.12); }

.warm-btn {
  width: 100%; height: 46px; font-size: 16px; font-weight: 600; border: none;
  background: linear-gradient(135deg, #8B5E3C, #A67C52); color: #fff;
  border-radius: 12px; cursor: pointer; transition: all .3s;
}
.warm-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(139,94,60,0.3); }

.link-text { text-align: center; color: var(--text-lt); font-size: 14px; margin-top: 12px; }
.link-text a { color: var(--brown); cursor: pointer; font-weight: 600; text-decoration: none; }
.link-text a:hover { text-decoration: underline; }
</style>
