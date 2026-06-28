<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const categories = ref([])
const products = ref([])
const keyword = ref('')
const selectedCategory = ref(null)
const page = ref(1)
const total = ref(0)

async function loadCategories() {
  const res = await request.get('/product/category')
  categories.value = res.data
}

async function searchProducts(p = 1) {
  page.value = p
  const params = { page: p, size: 8 }
  if (keyword.value) params.keyword = keyword.value
  if (selectedCategory.value) params.categoryId = selectedCategory.value
  const res = await request.get('/product', { params })
  products.value = res.data.records
  total.value = res.data.total
}

async function addToCart(product) {
  if (!auth.isLoggedIn()) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await request.post('/cart', { productId: product.id, quantity: 1 })
  ElMessage.success('已加入购物车')
}

function selectCategory(id) {
  selectedCategory.value = id
  searchProducts(1)
}

onMounted(() => { loadCategories(); searchProducts() })
</script>

<template>
  <!-- 横幅 -->
  <div class="hero">
    <h1>🛍️ 发现好物，乐享生活</h1>
    <p>全场商品品质保证 · 急速发货 · 7天无理由退换</p>
  </div>

  <div style="display:flex; gap:24px; margin-top:24px">
    <!-- 左侧分类 -->
    <div class="sidebar">
      <h3>📂 商品分类</h3>
      <div class="cate-list">
        <div class="cate-item" :class="{active: !selectedCategory}"
          @click="selectedCategory=null;searchProducts(1)">全部分类</div>
        <div v-for="c in categories" :key="c.id"
          class="cate-item" :class="{active: selectedCategory===c.id}"
          @click="selectCategory(c.id)">{{ c.name }}</div>
      </div>
    </div>

    <!-- 右侧商品 -->
    <div style="flex:1">
      <!-- 搜索 -->
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="🔍 搜索你想要的商品..." size="large"
          @keyup.enter="searchProducts(1)" clearable>
          <template #append>
            <el-button type="primary" @click="searchProducts(1)">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 商品网格 -->
      <el-row :gutter="20">
        <el-col v-for="p in products" :key="p.id" :span="6" style="margin-bottom:20px">
          <div class="product-card" @click="router.push('/product/'+p.id)">
            <div class="product-img-wrap">
              <img :src="p.coverImage || 'https://picsum.photos/400/400?random='+p.id" />
              <div class="price-tag">¥{{ p.price }}</div>
            </div>
            <div class="product-info">
              <div class="product-name">{{ p.name }}</div>
              <div class="product-meta">
                <span>已售 {{ p.sales }}</span>
                <span>库存 {{ p.stock }}</span>
              </div>
              <el-button type="primary" size="small" style="width:100%;margin-top:8px"
                @click.stop="addToCart(p)">🛒 加入购物车</el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-if="products.length===0" description="暂无商品" />

      <!-- 分页 -->
      <div style="text-align:center;margin-top:24px" v-if="total>8">
        <el-pagination v-model:current-page="page" :page-size="8" :total="total"
          background layout="prev, pager, next" @current-change="searchProducts" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.hero {
  background: linear-gradient(135deg, #667eea11 0%, #764ba211 100%);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  margin-bottom: 4px;
}
.hero h1 { font-size: 32px; color: #333; margin-bottom: 8px; }
.hero p { color: #999; font-size: 15px; }

.sidebar {
  width: 180px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  height: fit-content;
  position: sticky;
  top: 80px;
}
.sidebar h3 { margin-bottom: 12px; font-size: 16px; color: #333; }

.cate-list { display: flex; flex-direction: column; gap: 4px; }
.cate-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}
.cate-item:hover { background: #f0f0ff; color: #667eea; }
.cate-item.active { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; font-weight: 600; }

.search-bar { margin-bottom: 20px; }

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(0,0,0,0.12); }

.product-img-wrap {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f9f9f9;
}
.product-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .product-img-wrap img { transform: scale(1.05); }
.price-tag {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: rgba(0,0,0,0.65);
  color: #fff;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 700;
}

.product-info { padding: 12px; }
.product-name { font-size: 14px; font-weight: 600; color: #333; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-meta { display: flex; justify-content: space-between; font-size: 12px; color: #bbb; }
</style>
