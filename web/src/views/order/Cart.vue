<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const items = ref([])

async function loadCart() {
  const res = await request.get('/cart')
  items.value = res.data
}

async function updateQuantity(productId, qty) {
  if (qty < 1) return
  await request.put(`/cart/${productId}?quantity=${qty}`)
  loadCart()
}

async function removeItem(productId) {
  await ElMessageBox.confirm('确定要移除吗？')
  await request.delete(`/cart/${productId}`)
  ElMessage.success('已移除')
  loadCart()
}

// 计算总价
const totalPrice = () => items.value.reduce((sum, i) => sum + i.price * i.quantity, 0)

// 下单
const orderForm = ref({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })
const showOrderDialog = ref(false)

async function placeOrder() {
  if (!orderForm.value.receiverName || !orderForm.value.receiverPhone || !orderForm.value.receiverAddress) {
    ElMessage.warning('请填写收货信息')
    return
  }
  await request.post('/order', orderForm.value)
  ElMessage.success('下单成功！')
  showOrderDialog.value = false
  router.push('/orders')
}

onMounted(loadCart)
</script>

<template>
  <div>
    <h2>🛒 我的购物车</h2>
    <el-table v-if="items.length" :data="items" border>
      <el-table-column label="商品" min-width="200">
        <template #default="{row}">
          <span style="cursor:pointer" @click="router.push('/product/'+row.productId)">
            {{ row.productName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="120">
        <template #default="{row}">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="数量" width="180">
        <template #default="{row}">
          <el-input-number v-model="row.quantity" :min="1" size="small"
            @change="updateQuantity(row.productId, row.quantity)" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120">
        <template #default="{row}">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button type="danger" size="small" @click="removeItem(row.productId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="购物车是空的" />

    <div v-if="items.length" style="text-align:right;margin-top:20px">
      <h3 style="color:#ff4d4f">合计：¥{{ totalPrice().toFixed(2) }}</h3>
      <el-button type="primary" size="large" @click="showOrderDialog = true">去下单</el-button>
    </div>

    <!-- 下单弹窗 -->
    <el-dialog v-model="showOrderDialog" title="填写收货信息" width="450px">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="收件人"><el-input v-model="orderForm.receiverName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showOrderDialog = false">取消</el-button>
        <el-button type="primary" @click="placeOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>
