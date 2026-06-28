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
  try { await auth.login(form); ElMessage.success('登录成功'); router.push('/home') } catch (e) {}
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-header">
        <span class="header-icon">🔐</span>
        <h2>欢迎回来</h2>
        <p>登录你的 ease-mall 账号</p>
      </div>
      <el-form :model="form" size="large">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码"
            prefix-icon="Lock" @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%;height:44px;font-size:16px"
            @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="card-footer">
        还没有账号？<el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.08);
  padding: 48px 40px;
}
.card-header { text-align: center; margin-bottom: 32px; }
.header-icon { font-size: 48px; }
.card-header h2 { margin: 12px 0 6px; font-size: 24px; color: #333; }
.card-header p { color: #999; font-size: 14px; }
.card-footer { text-align: center; color: #999; font-size: 14px; margin-top: 16px; }
</style>
