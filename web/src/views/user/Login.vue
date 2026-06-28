<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()

// 表单数据（reactive = 响应式对象，改了会自动更新页面）
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  try {
    await auth.login(form)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // 错误已在 request.js 拦截器中提示
  }
}
</script>

<template>
  <div style="max-width:400px;margin:60px auto">
    <h2 style="text-align:center">用户登录</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码"
          @keyup.enter="handleLogin" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%" @click="handleLogin">登 录</el-button>
      </el-form-item>
      <div style="text-align:center">
        还没有账号？<el-link type="primary" @click="router.push('/register')">去注册</el-link>
      </div>
    </el-form>
  </div>
</template>
