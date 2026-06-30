<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Box, TrendCharts } from '@element-plus/icons-vue'

const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const product = ref({}); const quantity = ref(1)
const selectedSpecs = ref({})

// ★ 解析 specs JSON → {颜色: "原色钛金属", 存储: "256GB"}
const specEntries = computed(() => {
  if (!product.value.specs) return []
  try {
    return Object.entries(JSON.parse(product.value.specs))
  } catch { return [] }
})

function selectSpec(key, value) {
  selectedSpecs.value[key] = value
}
// 初始化选中的规格
function initSpecs() {
  if (!product.value.specs) return
  try {
    const obj = JSON.parse(product.value.specs)
    selectedSpecs.value = { ...obj }
  } catch {}
}

async function loadProduct() {
  const res = await request.get(`/product/${route.params.id}`)
  product.value = res.data
  initSpecs()
}
async function addToCart() { if (!auth.isLoggedIn()) { router.push('/login'); return }; await request.post('/cart', { productId: product.value.id, quantity: quantity.value }); ElMessage.success('已加入购物车') }
async function buyNow() { await addToCart(); router.push('/cart') }
onMounted(loadProduct)
</script>

<template>
  <div v-if="product.id" class="detail-wrap">
    <div class="img-col">
      <img :src="product.coverImage || 'https://picsum.photos/500/500?random='+product.id" />
    </div>
    <div class="info-col">
      <div class="breadcrumb"><span @click="router.push('/home')">首页</span> / {{ product.name }}</div>
      <h1>{{ product.name }}</h1>
      <div class="price">¥{{ product.price }}</div>
      <p class="desc">{{ product.description }}</p>

      <!-- ★ 可选规格 -->
      <div v-if="specEntries.length" class="spec-section">
        <div v-for="[key, value] in specEntries" :key="key" class="spec-group">
          <span class="spec-label">{{ key }}</span>
          <span class="spec-chip" :class="{active: selectedSpecs[key] === value}"
            @click="selectSpec(key, value)">{{ value }}</span>
        </div>
      </div>

      <div class="meta-row">
        <span><el-icon :size="16"><Box /></el-icon> 库存 {{ product.stock }}</span>
        <span><el-icon :size="16"><TrendCharts /></el-icon> 已售 {{ product.sales }}</span>
      </div>
      <div class="qty-row"><span>数量</span><el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" class="warm-qty" /></div>
      <div class="btns">
        <button class="btn-cart" @click="addToCart"><el-icon :size="18"><ShoppingCart /></el-icon> 加入购物车</button>
        <button class="btn-buy" @click="buyNow">立即购买</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-wrap { display: flex; gap: 40px; background: var(--card); border-radius: var(--radius); padding: 36px; box-shadow: var(--shadow); }
.img-col { flex: 0 0 420px; }
.img-col img { width: 100%; border-radius: 14px; background: #F5EDE4; }
.info-col { flex: 1; }
.breadcrumb { color: var(--brown-pale); font-size: 13px; margin-bottom: 12px; }
.breadcrumb span { cursor: pointer; color: var(--brown); }
.breadcrumb span:hover { text-decoration: underline; }
h1 { font-size: 26px; font-weight: 700; color: var(--text); margin-bottom: 12px; }
.price { font-size: 32px; font-weight: 700; color: #C0392B; margin-bottom: 16px; }
.desc { color: var(--text-lt); font-size: 15px; line-height: 1.6; margin-bottom: 16px; }
.meta-row { display: flex; gap: 24px; color: var(--brown-pale); font-size: 14px; margin-bottom: 12px; }
.meta-row span { display: flex; align-items: center; gap: 4px; }

/* 规格选择 */
.spec-section { margin-bottom: 18px; display: flex; flex-direction: column; gap: 12px; }
.spec-group { display: flex; align-items: center; gap: 10px; }
.spec-label { font-size: 14px; color: var(--text-lt); min-width: 40px; }
.spec-chip {
  display: inline-block;
  padding: 6px 16px;
  border: 1.5px solid #E8DDD0;
  border-radius: 8px;
  font-size: 13px; color: var(--text);
  cursor: pointer;
  transition: all .2s;
  background: var(--bg);
}
.spec-chip:hover { border-color: var(--brown); color: var(--brown); }
.spec-chip.active {
  border-color: var(--brown);
  background: var(--brown);
  color: #fff;
  font-weight: 600;
}

.qty-row { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; color: var(--text); font-size: 15px; }
:deep(.warm-qty .el-input__wrapper) { border-radius: 12px; border-color: #E8DDD0; box-shadow: none; }
.btns { display: flex; gap: 14px; }
.btn-cart { padding: 14px 32px; border: 2px solid var(--brown); background: transparent; color: var(--brown); border-radius: 14px; font-size: 16px; font-weight: 600; font-family: inherit; cursor: pointer; transition: all .2s; display: flex; align-items: center; gap: 6px; }
.btn-cart:hover { background: var(--brown); color: #fff; }
.btn-buy { padding: 14px 32px; border: none; background: linear-gradient(135deg, #8B5E3C, #A67C52); color: #fff; border-radius: 14px; font-size: 16px; font-weight: 600; font-family: inherit; cursor: pointer; transition: all .2s; }
.btn-buy:hover { box-shadow: 0 6px 20px rgba(139,94,60,0.35); transform: translateY(-1px); }
</style>
