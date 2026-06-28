<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const dashboard = ref({})
const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)

async function loadDashboard() {
  const res = await request.get('/admin/dashboard')
  dashboard.value = res.data
}

async function loadUsers(p = 1) {
  userPage.value = p
  const res = await request.get('/admin/users', { params: { page: p, size: 10 } })
  users.value = res.data.records
  userTotal.value = res.data.total
}

async function toggleUserStatus(userId) {
  await request.put(`/admin/users/${userId}/status`)
  loadUsers(userPage.value)
}

onMounted(() => { loadDashboard(); loadUsers() })
</script>

<template>
  <div>
    <h2>📊 数据看板</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom:30px">
      <el-col :span="6" v-for="card in [
        {label:'总用户',val:dashboard.totalUsers,icon:'User'},
        {label:'总订单',val:dashboard.totalOrders,icon:'Document'},
        {label:'今日订单',val:dashboard.todayOrders,icon:'TrendCharts'},
        {label:'总交易额',val:'¥'+(dashboard.totalGMV||0),icon:'Money'},
        {label:'今日交易额',val:'¥'+(dashboard.todayGMV||0),icon:'Coin'},
        {label:'待发货',val:dashboard.pendingShip,icon:'Box'},
        {label:'商品数',val:dashboard.totalProducts,icon:'Goods'},
      ]" :key="card.label" style="margin-bottom:20px">
        <el-card shadow="hover">
          <div style="color:#999">{{ card.label }}</div>
          <div style="font-size:24px;font-weight:bold;margin-top:8px">{{ card.val }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <h3>👥 用户管理</h3>
    <el-table :data="users" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status===1?'success':'danger'">
            {{ row.status===1?'正常':'禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button size="small" :type="row.status===1?'danger':'success'"
            @click="toggleUserStatus(row.id)">
            {{ row.status===1?'禁用':'启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="text-align:center;margin-top:20px">
      <el-pagination v-model:current-page="userPage" :total="userTotal"
        layout="prev, pager, next" @current-change="loadUsers" />
    </div>
  </div>
</template>
