<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { List, CreditCard, HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const orders = ref([]); const page = ref(1); const total = ref(0)

async function loadOrders(p = 1) {
  page.value = p
  const res = await request.get('/order', { params: { page: p, size: 10 } })
  orders.value = res.data.records
  total.value = res.data.total
}

const statusMap = {
  1: ['待支付', '#E8A838'],
  2: ['已支付', '#5B9A8B'],
  3: ['已发货', '#8B5E3C'],
  4: ['已完成', '#A0A0A0'],
  5: ['已取消', '#D4A0A0']
}

async function payOrder(oid) {
  try {
    await request.post('/payment/pay', { orderId: oid, payMethod: 'ALIPAY' })
    ElMessage.success('支付成功！')
    await loadOrders(page.value)
  } catch (e) {
    // error handled by interceptor
  }
}

async function cancelOrder(oid) {
  try {
    await request.put(`/order/${oid}/cancel`)
    ElMessage.success('订单已取消')
    await loadOrders(page.value)
  } catch (e) {}
}

onMounted(() => loadOrders())
</script>

<template>
  <div class="page-card">
    <div class="page-header">
      <h2><el-icon :size="22"><List /></el-icon> 我的订单</h2>
      <el-button :icon="HomeFilled" @click="router.push('/home')" class="home-btn">返回首页</el-button>
    </div>

    <el-table v-if="orders.length" :data="orders" class="warm-table" style="margin-top:20px">
      <el-table-column label="订单编号" width="190"><template #default="{r}">{{ r.orderNo }}</template></el-table-column>
      <el-table-column label="金额" width="110"><template #default="{r}">¥{{ r.totalAmount }}</template></el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{r}"><span class="status-badge" :style="{background:statusMap[r.status][1]}">{{ statusMap[r.status][0] }}</span></template>
      </el-table-column>
      <el-table-column label="收货人" width="90"><template #default="{r}">{{ r.receiverName }}</template></el-table-column>
      <el-table-column label="时间" min-width="160"><template #default="{r}">{{ r.createTime?.substring(0,16) }}</template></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{r}">
          <button v-if="r.status===1" class="btn-pay" @click="payOrder(r.id)"><el-icon :size="14"><CreditCard /></el-icon> 支付</button>
          <button v-if="r.status===1" class="btn-cancel" @click="cancelOrder(r.id)">取消</button>
          <span v-if="r.status===2" style="color:#5B9A8B;font-size:13px">等待发货</span>
          <span v-if="r.status===3" style="color:#8B5E3C;font-size:13px">运输中</span>
          <span v-if="r.status===4" style="color:#A0A0A0;font-size:13px">已完成</span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else description="还没有订单，去逛逛吧">
      <el-button type="primary" @click="router.push('/home')">去购物</el-button>
    </el-empty>

    <div style="text-align:center;margin-top:20px" v-if="total>10">
      <el-pagination v-model:current-page="page" :total="total" background layout="prev, pager, next" @current-change="loadOrders" />
    </div>
  </div>
</template>

<style scoped>
.page-card { background: var(--card); border-radius: var(--radius); padding: 28px 32px; box-shadow: var(--shadow); }
.page-header { display: flex; justify-content: space-between; align-items: center; }
h2 { font-weight: 700; color: var(--text); display: flex; align-items: center; gap: 8px; }
.home-btn { border: 1.5px solid var(--brown); color: var(--brown); background: none; border-radius: 10px; font-family: inherit; cursor: pointer; transition: .2s; }
.home-btn:hover { background: var(--brown); color: #fff; }
.status-badge { padding: 3px 12px; border-radius: 20px; color: #fff; font-size: 12px; font-weight: 600; }
.btn-pay { padding: 5px 14px; border: none; background: #5B9A8B; color: #fff; border-radius: 10px; font-family: inherit; font-size: 13px; cursor: pointer; margin-right: 6px; transition: .2s; display: inline-flex; align-items: center; gap: 4px; }
.btn-pay:hover { opacity: 0.85; }
.btn-cancel { padding: 5px 12px; border: 1.5px solid #D4A0A0; background: none; color: #c0392b; border-radius: 10px; font-family: inherit; font-size: 13px; cursor: pointer; transition: .2s; }
.btn-cancel:hover { background: #fef0f0; }
:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
