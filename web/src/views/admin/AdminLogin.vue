<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { Lock } from '@element-plus/icons-vue'

const router = useRouter()
const form = reactive({ username: 'admin', password: 'admin123' })
async function handleLogin() {
  try { const res = await request.post('/admin/login', form); localStorage.setItem('adminToken', res.data.token); localStorage.setItem('adminInfo', JSON.stringify(res.data)); ElMessage.success('管理员登录成功'); router.push('/admin/dashboard') } catch (e) {}
}
</script>

<template>
  <div class="center-page">
    <div class="card">
      <div class="card-art">
        <el-icon :size="40" color="#8B5E3C"><Lock /></el-icon>
        <h2>管理员登录</h2>
        <p>后台管理系统</p>
      </div>
      <el-form :model="form" size="large" label-position="top">
        <el-form-item label="用户名"><el-input v-model="form.username" class="warm-input" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password @keyup.enter="handleLogin" class="warm-input" /></el-form-item>
        <el-form-item><el-button class="warm-btn" @click="handleLogin">登 录</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.center-page { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.card { width: 400px; background: var(--card); border-radius: var(--radius); box-shadow: var(--shadow-lg); padding: 40px 36px; }
.card-art { text-align: center; margin-bottom: 24px; }
.card-art h2 { margin: 12px 0 4px; font-size: 22px; color: var(--text); font-weight: 700; }
.card-art p { color: var(--text-lt); font-size: 14px; margin: 0; }
:deep(.warm-input .el-input__wrapper) { background: var(--bg); border-radius: 12px; border: 1.5px solid #E8DDD0; box-shadow: none; }
:deep(.warm-input .el-input__wrapper:hover) { border-color: var(--brown-pale); }
:deep(.warm-input .el-input__wrapper.is-focus) { border-color: var(--brown); box-shadow: 0 0 0 2px rgba(139,94,60,0.12); }
.warm-btn { width: 100%; height: 46px; font-size: 16px; font-weight: 600; border: none; background: linear-gradient(135deg, #8B5E3C, #A67C52); color: #fff; border-radius: 12px; cursor: pointer; transition: all .3s; }
.warm-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(139,94,60,0.3); }
</style>
