<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const product = ref({})
const quantity = ref(1)

async function loadProduct() {
  const res = await request.get(`/product/${route.params.id}`)
  product.value = res.data
}

async function addToCart() {
  if (!auth.isLoggedIn()) { router.push('/login'); return }
  await request.post('/cart', { productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

async function buyNow() {
  await addToCart()
  router.push('/cart')
}

onMounted(loadProduct)
</script>

<template>
  <div v-if="product.id">
    <el-row :gutter="40">
      <el-col :span="10">
        <img :src="product.coverImage || 'https://picsum.photos/400/400?random='+product.id"
          style="width:100%;border-radius:8px" />
      </el-col>
      <el-col :span="14">
        <h1>{{ product.name }}</h1>
        <p style="color:#ff4d4f;font-size:28px;font-weight:bold">¥{{ product.price }}</p>
        <p>库存：{{ product.stock }} | 销量：{{ product.sales }}</p>
        <p style="color:#666">{{ product.description }}</p>

        <div v-if="product.specs" style="margin:20px 0">
          <p>规格：{{ product.specs }}</p>
        </div>

        <div style="display:flex;align-items:center;gap:10px;margin:20px 0">
          <span>数量：</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock" />
        </div>

        <div style="display:flex;gap:10px">
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
