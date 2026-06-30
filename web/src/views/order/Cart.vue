<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Edit, Plus } from '@element-plus/icons-vue'

const router = useRouter(); const items = ref([])
const orderForm = ref({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })
const showDialog = ref(false)
const total = computed(() => items.value.reduce((s,i) => s + i.price * i.quantity, 0).toFixed(2))

// ★ 地址列表
const addresses = ref([])
const selectedAddrId = ref(null)

async function loadCart() { const res = await request.get('/cart'); items.value = res.data }
async function updateQty(pid, qty) { if(qty<1)return; await request.put(`/cart/${pid}?quantity=${qty}`); loadCart() }
async function removeItem(pid) { await ElMessageBox.confirm('确定移除？'); await request.delete(`/cart/${pid}`); ElMessage.success('已移除'); loadCart() }

// ★ 加载地址列表，打开下单弹窗时调用
async function openOrderDialog() {
  try {
    const res = await request.get('/user/address')
    addresses.value = res.data
    // 自动选中默认地址
    const defaultAddr = res.data.find(a => a.isDefault === 1) || res.data[0]
    if (defaultAddr) selectAddress(defaultAddr)
  } catch(e) { /* 没有地址也正常 */ }
  showDialog.value = true
}

// ★ 选中地址，自动填充表单
function selectAddress(addr) {
  selectedAddrId.value = addr.id
  orderForm.value.receiverName = addr.receiverName
  orderForm.value.receiverPhone = addr.receiverPhone
  orderForm.value.receiverAddress = addr.province + ' ' + addr.city + ' ' + addr.district + ' ' + addr.detail
}

// ★ 切换到手动输入
function manualInput() {
  selectedAddrId.value = null
  orderForm.value.receiverName = ''
  orderForm.value.receiverPhone = ''
  orderForm.value.receiverAddress = ''
}

async function placeOrder() {
  if(!orderForm.value.receiverName||!orderForm.value.receiverPhone||!orderForm.value.receiverAddress){ ElMessage.warning('请填写收货信息'); return }
  await request.post('/order', orderForm.value); ElMessage.success('下单成功'); showDialog.value=false; router.push('/orders')
}
onMounted(loadCart)
</script>

<template>
  <div class="page-card">
    <h2><el-icon :size="22"><ShoppingCart /></el-icon> 我的购物车</h2>
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
      <button class="warm-btn" @click="openOrderDialog">去下单</button>
    </div>

    <el-dialog v-model="showDialog" width="520px" class="warm-dialog">
      <template #header><el-icon :size="18"><Edit /></el-icon> 填写收货信息</template>

      <!-- ★ 已保存的地址 -->
      <div v-if="addresses.length" class="addr-section">
        <div class="addr-label">选择已保存的地址</div>
        <div class="addr-cards">
          <div v-for="a in addresses" :key="a.id"
            class="addr-radio" :class="{active: selectedAddrId===a.id}"
            @click="selectAddress(a)">
            <div class="addr-radio-top">
              <b>{{ a.receiverName }}</b> {{ a.receiverPhone }}
              <el-tag v-if="a.isDefault===1" size="small" type="warning">默认</el-tag>
            </div>
            <div class="addr-radio-detail">{{ a.province }} {{ a.city }} {{ a.district }} {{ a.detail }}</div>
          </div>
        </div>
        <el-button :icon="Plus" size="small" text type="primary" @click="manualInput" style="margin-top:8px">
          手动输入新地址
        </el-button>
      </div>

      <!-- 手动输入（无地址或点手动输入时用） -->
      <el-divider v-if="addresses.length && !selectedAddrId" />
      <el-form v-if="!selectedAddrId" :model="orderForm" label-position="top">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="收件人"><el-input v-model="orderForm.receiverName" class="warm-input" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" class="warm-input" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" class="warm-input" placeholder="省 市 区 详细地址" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" class="warm-input" /></el-form-item>
      </el-form>

      <!-- 已选地址时只需备注 -->
      <el-form v-else :model="orderForm" label-position="top">
        <el-form-item label="备注（选填）"><el-input v-model="orderForm.remark" class="warm-input" /></el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showDialog=false" class="ghost-btn">取消</el-button>
        <el-button class="warm-btn" @click="placeOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-card { background: var(--card); border-radius: var(--radius); padding: 28px 32px; box-shadow: var(--shadow); }
h2 { font-weight: 700; color: var(--text); display: flex; align-items: center; gap: 8px; }
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

/* 地址选择 */
.addr-section { margin-bottom: 8px; }
.addr-label { font-size: 14px; color: var(--text-lt); margin-bottom: 10px; }
.addr-cards { display: flex; flex-direction: column; gap: 8px; margin-bottom: 4px; }
.addr-radio { border: 1.5px solid #E8DDD0; border-radius: 12px; padding: 12px 16px; cursor: pointer; transition: .2s; }
.addr-radio:hover { border-color: var(--brown-pale); background: #FFF9F2; }
.addr-radio.active { border-color: var(--brown); background: #FFF9F2; }
.addr-radio-top { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; font-size: 14px; }
.addr-radio-detail { font-size: 13px; color: var(--text-lt); }

:deep(.warm-input .el-input__wrapper) { background: var(--bg); border-radius: 12px; border: 1.5px solid #E8DDD0; box-shadow: none; }
:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
