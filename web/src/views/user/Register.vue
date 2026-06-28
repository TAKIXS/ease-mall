<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '', phone: '', email: '' })

async function handleRegister() {
  if (!form.username || !form.password) {
    ElMessage.warning('用户名和密码必填')
    return
  }
  try {
    await auth.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {}
}
</script>

<template>
  <div style="max-width:400px;margin:60px auto">
    <h2 style="text-align:center">用户注册</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="3-20位" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="6-30位" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="选填" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="选填" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%" @click="handleRegister">注 册</el-button>
      </el-form-item>
      <div style="text-align:center">
        已有账号？<el-link type="primary" @click="router.push('/login')">去登录</el-link>
      </div>
    </el-form>
  </div>
</template>
