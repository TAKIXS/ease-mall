<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const dashboard = ref({}); const users = ref([]); const userPage = ref(1); const userTotal = ref(0)
async function loadDashboard() { const res = await request.get('/admin/dashboard'); dashboard.value = res.data }
async function loadUsers(p = 1) { userPage.value = p; const res = await request.get('/admin/users', { params: { page: p, size: 10 } }); users.value = res.data.records; userTotal.value = res.data.total }
async function toggleUserStatus(uid) { await request.put(`/admin/users/${uid}/status`); loadUsers(userPage.value) }
onMounted(() => { loadDashboard(); loadUsers() })

const cards = [
  {label:'总用户数',val:()=>dashboard.value.totalUsers||0,icon:'👥',bg:'#F5EDE4',accent:'#8B5E3C'},
  {label:'总订单数',val:()=>dashboard.value.totalOrders||0,icon:'📦',bg:'#EDF0E8',accent:'#5B9A8B'},
  {label:'今日订单',val:()=>dashboard.value.todayOrders||0,icon:'📈',bg:'#E8EEF5',accent:'#6B8DB5'},
  {label:'待发货',val:()=>dashboard.value.pendingShip||0,icon:'🚚',bg:'#F5E8E8',accent:'#C48B8B'},
  {label:'商品总数',val:()=>dashboard.value.totalProducts||0,icon:'📋',bg:'#EEE8F0',accent:'#8B6B9E'},
  {label:'总交易额',val:()=>'¥'+(dashboard.value.totalGMV||0),icon:'💰',bg:'#F5F0E8',accent:'#B8954A'},
  {label:'今日交易额',val:()=>'¥'+(dashboard.value.todayGMV||0),icon:'💎',bg:'#E8F0F0',accent:'#5B8B8B'},
]
</script>

<template>
  <div class="dash-wrap">
    <div class="dash-header">
      <div><h2>📊 数据看板</h2><span class="date">{{ new Date().toLocaleDateString('zh-CN',{year:'numeric',month:'long',day:'numeric'}) }}</span></div>
      <!-- 装饰SVG -->
      <svg viewBox="0 0 180 50" fill="none" style="width:180px">
        <path d="M10 40 Q40 15 80 35 T150 25" stroke="#C4A484" stroke-width="1.5" fill="none" opacity="0.5"/>
        <circle cx="50" cy="28" r="3" fill="#E8DDD0"/>
        <circle cx="120" cy="22" r="2.5" fill="#E8DDD0"/>
      </svg>
    </div>

    <div class="stat-grid">
      <div v-for="c in cards" :key="c.label" class="stat-card" :style="{background:c.bg}">
        <span class="stat-icon">{{ c.icon }}</span>
        <span class="stat-num" :style="{color:c.accent}">{{ c.val() }}</span>
        <span class="stat-label">{{ c.label }}</span>
      </div>
    </div>

    <h3 style="margin:28px 0 14px;font-weight:700;color:var(--text)">👥 用户管理</h3>
    <div class="table-card">
      <el-table :data="users" class="warm-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{row}"><span class="status-badge" :class="row.status===1?'on':'off'">{{ row.status===1?'正常':'禁用' }}</span></template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="165" />
        <el-table-column label="操作" width="90">
          <template #default="{row}"><button class="toggle-btn" :class="row.status===1?'off':'on'" @click="toggleUserStatus(row.id)">{{ row.status===1?'禁用':'启用' }}</button></template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:16px">
        <el-pagination v-model:current-page="userPage" :total="userTotal" background layout="prev, pager, next" @current-change="loadUsers" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dash-wrap { max-width: 1100px; margin: 0 auto; }
.dash-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.dash-header h2 { font-size: 22px; color: var(--text); font-weight: 700; }
.date { color: var(--brown-pale); font-size: 13px; }
.stat-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; }
.stat-card { border-radius: 16px; padding: 22px; display: flex; flex-direction: column; transition: transform .2s; }
.stat-card:hover { transform: translateY(-3px); }
.stat-icon { font-size: 28px; margin-bottom: 8px; }
.stat-num { font-size: 26px; font-weight: 700; }
.stat-label { font-size: 12px; color: var(--text-lt); margin-top: 2px; }
.table-card { background: var(--card); border-radius: var(--radius); padding: 20px; box-shadow: var(--shadow); }
.status-badge { padding: 3px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.status-badge.on { background: #E8F0E8; color: #5B9A8B; }
.status-badge.off { background: #F5E8E8; color: #C48B8B; }
.toggle-btn { padding: 4px 14px; border-radius: 10px; font-family: inherit; font-size: 13px; cursor: pointer; border: 1.5px solid; transition: .2s; background: none; }
.toggle-btn.on { border-color: #5B9A8B; color: #5B9A8B; } .toggle-btn.on:hover { background: #E8F0E8; }
.toggle-btn.off { border-color: #C48B8B; color: #C48B8B; } .toggle-btn.off:hover { background: #F5E8E8; }
:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
