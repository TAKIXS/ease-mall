<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const orders = ref([]); const page = ref(1); const total = ref(0)
async function loadOrders(p = 1) { page.value = p; const res = await request.get('/order',{params:{page:p,size:10}}); orders.value=res.data.records; total.value=res.data.total }
const statusMap = {1:['待支付','#E8A838'],2:['已支付','#5B9A8B'],3:['已发货','#8B5E3C'],4:['已完成','#A0A0A0'],5:['已取消','#D4A0A0']}
async function payOrder(oid) { await request.post('/payment/pay',{orderId:oid,payMethod:'ALIPAY'}); ElMessage.success('支付成功 💰'); loadOrders(page.value) }
async function cancelOrder(oid) { await request.put(`/order/${oid}/cancel`); ElMessage.success('已取消'); loadOrders(page.value) }
onMounted(()=>loadOrders())
</script>

<template>
  <div class="page-card">
    <h2>📋 我的订单</h2>
    <el-table :data="orders" class="warm-table" style="margin-top:20px">
      <el-table-column label="订单编号" width="190"><template #default="{r}">{{ r.orderNo }}</template></el-table-column>
      <el-table-column label="金额" width="110"><template #default="{r}">¥{{ r.totalAmount }}</template></el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{r}"><span class="status-badge" :style="{background:statusMap[r.status][1]}">{{ statusMap[r.status][0] }}</span></template>
      </el-table-column>
      <el-table-column label="收货人" width="90"><template #default="{r}">{{ r.receiverName }}</template></el-table-column>
      <el-table-column label="时间" min-width="155"><template #default="{r}">{{ r.createTime?.substring(0,16) }}</template></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{r}">
          <button v-if="r.status===1" class="btn-pay" @click="payOrder(r.id)">💳 支付</button>
          <button v-if="r.status===1" class="btn-cancel" @click="cancelOrder(r.id)">取消</button>
        </template>
      </el-table-column>
    </el-table>
    <div style="text-align:center;margin-top:20px">
      <el-pagination v-model:current-page="page" :total="total" background layout="prev, pager, next" @current-change="loadOrders" />
    </div>
  </div>
</template>

<style scoped>
.page-card { background: var(--card); border-radius: var(--radius); padding: 28px 32px; box-shadow: var(--shadow); }
h2 { font-weight: 700; color: var(--text); }
.status-badge { padding: 3px 12px; border-radius: 20px; color: #fff; font-size: 12px; font-weight: 600; }
.btn-pay { padding: 5px 14px; border: none; background: #5B9A8B; color: #fff; border-radius: 10px; font-family: inherit; font-size: 13px; cursor: pointer; margin-right: 6px; transition: .2s; }
.btn-pay:hover { opacity: 0.85; }
.btn-cancel { padding: 5px 12px; border: 1.5px solid #D4A0A0; background: none; color: #c0392b; border-radius: 10px; font-family: inherit; font-size: 13px; cursor: pointer; transition: .2s; }
.btn-cancel:hover { background: #fef0f0; }
:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
