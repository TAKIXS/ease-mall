<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({ username: 'admin', password: 'admin123' })

async function handleLogin() {
  try {
    const res = await request.post('/admin/login', form)
    localStorage.setItem('adminToken', res.data.token)
    localStorage.setItem('adminInfo', JSON.stringify(res.data))
    ElMessage.success('管理员登录成功')
    router.push('/admin/dashboard')
  } catch (e) {}
}
</script>

<template>
  <div style="max-width:400px;margin:60px auto">
    <h2 style="text-align:center">管理员登录</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
      <el-form-item label="密码"><el-input v-model="form.password" type="password" @keyup.enter="handleLogin" /></el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%" @click="handleLogin">登 录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
