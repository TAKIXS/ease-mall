<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter(); const items = ref([])
const orderForm = ref({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })
const showDialog = ref(false)
const total = computed(() => items.value.reduce((s,i) => s + i.price * i.quantity, 0).toFixed(2))

async function loadCart() { const res = await request.get('/cart'); items.value = res.data }
async function updateQty(pid, qty) { if(qty<1)return; await request.put(`/cart/${pid}?quantity=${qty}`); loadCart() }
async function removeItem(pid) { await ElMessageBox.confirm('确定移除？'); await request.delete(`/cart/${pid}`); ElMessage.success('已移除'); loadCart() }
async function placeOrder() {
  if(!orderForm.value.receiverName||!orderForm.value.receiverPhone||!orderForm.value.receiverAddress){ ElMessage.warning('请填写收货信息'); return }
  await request.post('/order', orderForm.value); ElMessage.success('下单成功！🎉'); showDialog.value=false; router.push('/orders')
}
onMounted(loadCart)
</script>

<template>
  <div class="page-card">
    <h2>🛒 我的购物车</h2>
    <el-table v-if="items.length" :data="items" class="warm-table" style="margin-top:20px">
      <el-table-column label="商品" min-width="200"><template #default="{row}"><span class="pname-link" @click="router.push('/product/'+row.productId)">{{ row.productName }}</span></template></el-table-column>
      <el-table-column label="单价" width="120"><template #default="{row}">¥{{ row.price }}</template></el-table-column>
      <el-table-column label="数量" width="180"><template #default="{row}"><el-input-number v-model="row.quantity" :min="1" size="small" @change="updateQty(row.productId, row.quantity)" /></template></el-table-column>
      <el-table-column label="小计" width="120"><template #default="{row}">¥{{ (row.price*row.quantity).toFixed(2) }}</template></el-table-column>
      <el-table-column label="操作" width="100"><template #default="{row}"><button class="warn-link" @click="removeItem(row.productId)">删除</button></template></el-table-column>
    </el-table>
    <el-empty v-else description="购物车空空的~" />

    <div v-if="items.length" class="checkout-bar">
      <span class="total-label">合计 <b>¥{{ total }}</b></span>
      <button class="warm-btn" @click="showDialog=true">去下单</button>
    </div>

    <el-dialog v-model="showDialog" title="📝 填写收货信息" width="440px" class="warm-dialog">
      <el-form :model="orderForm" label-position="top">
        <el-form-item label="收件人"><el-input v-model="orderForm.receiverName" class="warm-input" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" class="warm-input" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" class="warm-input" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" class="warm-input" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showDialog=false" class="ghost-btn">取消</el-button><el-button class="warm-btn" @click="placeOrder">确认下单</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-card { background: var(--card); border-radius: var(--radius); padding: 28px 32px; box-shadow: var(--shadow); }
h2 { font-weight: 700; color: var(--text); }
.pname-link { cursor: pointer; color: var(--brown); font-weight: 600; }
.pname-link:hover { text-decoration: underline; }
.warn-link { background: none; border: none; color: #c0392b; cursor: pointer; font-size: 13px; font-family: inherit; }
.warn-link:hover { text-decoration: underline; }
.checkout-bar { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 24px; padding-top: 20px; border-top: 1.5px solid #F0E6D8; }
.total-label { font-size: 16px; color: var(--text); }
.total-label b { color: #c0392b; font-size: 22px; }
.warm-btn { padding: 12px 32px; border: none; background: linear-gradient(135deg, #8B5E3C, #A67C52); color: #fff; border-radius: 12px; font-size: 15px; font-weight: 600; font-family: inherit; cursor: pointer; transition: .2s; }
.warm-btn:hover { box-shadow: 0 4px 16px rgba(139,94,60,0.35); transform: translateY(-1px); }
.ghost-btn { background: none; border: 1.5px solid #E8DDD0; color: var(--text-lt); border-radius: 12px; font-family: inherit; }
:deep(.warm-input .el-input__wrapper) { background: var(--bg); border-radius: 12px; border: 1.5px solid #E8DDD0; box-shadow: none; }
:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
