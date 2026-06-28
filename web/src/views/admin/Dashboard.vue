<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const dashboard = ref({})
const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)

async function loadDashboard() { const res = await request.get('/admin/dashboard'); dashboard.value = res.data }
async function loadUsers(p = 1) {
  userPage.value = p
  const res = await request.get('/admin/users', { params: { page: p, size: 10 } })
  users.value = res.data.records; userTotal.value = res.data.total
}
async function toggleUserStatus(userId) { await request.put(`/admin/users/${userId}/status`); loadUsers(userPage.value) }

onMounted(() => { loadDashboard(); loadUsers() })
</script>

<template>
  <div>
    <div class="dash-header">
      <h2>📊 数据看板</h2>
      <span class="date">{{ new Date().toLocaleDateString('zh-CN', {year:'numeric',month:'long',day:'numeric'}) }}</span>
    </div>

    <el-row :gutter="16">
      <el-col :span="6" v-for="(card,i) in [
        {label:'总用户数',val:dashboard.totalUsers||0,icon:'👥',color:'#667eea'},
        {label:'总订单数',val:dashboard.totalOrders||0,icon:'📦',color:'#f093fb'},
        {label:'今日订单',val:dashboard.todayOrders||0,icon:'📈',color:'#4facfe'},
        {label:'待发货',val:dashboard.pendingShip||0,icon:'🚚',color:'#fa709a'},
        {label:'商品总数',val:dashboard.totalProducts||0,icon:'📋',color:'#43e97b'},
        {label:'总交易额',val:'¥'+(dashboard.totalGMV||0),icon:'💰',color:'#f6d365'},
        {label:'今日交易额',val:'¥'+(dashboard.todayGMV||0),icon:'💎',color:'#a18cd1'},
      ]" :key="i" style="margin-bottom:16px">
        <div class="stat-card" :style="{background: card.color}">
          <div class="stat-icon">{{ card.icon }}</div>
          <div class="stat-num">{{ card.val }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <h3 style="margin:24px 0 12px">👥 用户管理</h3>
    <div class="table-card">
      <el-table :data="users" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'danger'" size="small">
              {{ row.status===1?'正常':'禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button size="small" :type="row.status===1?'danger':'success'"
              @click="toggleUserStatus(row.id)">{{ row.status===1?'禁用':'启用' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:16px">
        <el-pagination v-model:current-page="userPage" :total="userTotal"
          background layout="prev, pager, next" @current-change="loadUsers" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dash-header { display: flex; align-items: baseline; gap: 16px; margin-bottom: 24px; }
.dash-header h2 { font-size: 22px; }
.date { color: #999; font-size: 14px; }
.stat-card {
  border-radius: 14px;
  padding: 24px;
  color: #fff;
  min-height: 110px;
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-3px); }
.stat-icon { font-size: 32px; margin-bottom: 8px; }
.stat-num { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.9; margin-top: 4px; }
.table-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
</style>
