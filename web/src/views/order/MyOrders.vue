<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const orders = ref([])
const page = ref(1)
const total = ref(0)

async function loadOrders(p = 1) {
  page.value = p
  const res = await request.get('/order', { params: { page: p, size: 10 } })
  orders.value = res.data.records
  total.value = res.data.total
}

// 状态标签颜色
function statusType(status) {
  return { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[status]
}
function statusText(status) {
  return { 1: '待支付', 2: '已支付', 3: '已发货', 4: '已完成', 5: '已取消' }[status]
}

async function cancelOrder(orderId) {
  await request.put(`/order/${orderId}/cancel`)
  ElMessage.success('订单已取消')
  loadOrders(page.value)
}

onMounted(() => loadOrders())
</script>

<template>
  <div>
    <h2>📋 我的订单</h2>
    <el-table :data="orders" border>
      <el-table-column label="订单编号" width="200">
        <template #default="{row}">{{ row.orderNo }}</template>
      </el-table-column>
      <el-table-column label="金额" width="120">
        <template #default="{row}">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货人" width="100">
        <template #default="{row}">{{ row.receiverName }}</template>
      </el-table-column>
      <el-table-column label="时间" min-width="160">
        <template #default="{row}">{{ row.createTime }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button v-if="row.status===1" type="danger" size="small"
            @click="cancelOrder(row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="text-align:center;margin-top:20px">
      <el-pagination v-model:current-page="page" :total="total"
        layout="prev, pager, next" @current-change="loadOrders" />
    </div>
  </div>
</template>
