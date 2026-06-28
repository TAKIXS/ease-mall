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

async function loadCategories() { const res = await request.get('/product/category'); categories.value = res.data }
async function searchProducts(p = 1) {
  page.value = p; const params = { page: p, size: 8 }
  if (keyword.value) params.keyword = keyword.value
  if (selectedCategory.value) params.categoryId = selectedCategory.value
  const res = await request.get('/product', { params })
  products.value = res.data.records; total.value = res.data.total
}
async function addToCart(product) {
  if (!auth.isLoggedIn()) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await request.post('/cart', { productId: product.id, quantity: 1 })
  ElMessage.success('已加入购物车 🛒')
}
function selectCategory(id) { selectedCategory.value = id; searchProducts(1) }
onMounted(() => { loadCategories(); searchProducts() })
</script>

<template>
  <!-- 手绘风横幅 -->
  <div class="hero">
    <div class="hero-art">
      <svg viewBox="0 0 260 80" fill="none">
        <path d="M20 65 Q60 10 130 40 T230 25" stroke="#C4A484" stroke-width="2.5" fill="none" stroke-linecap="round"/>
        <circle cx="80" cy="30" r="6" fill="#F0E6D8" stroke="#8B5E3C" stroke-width="2"/>
        <circle cx="180" cy="28" r="5" fill="#F0E6D8" stroke="#A67C52" stroke-width="2"/>
        <path d="M50 60 Q55 48 65 58" stroke="#8B5E3C" stroke-width="2" fill="none" stroke-linecap="round"/>
      </svg>
    </div>
    <h1>发现温暖好物</h1>
    <p>用心挑选，品质生活 · 从 ease-mall 开始</p>
  </div>

  <div style="display:flex; gap:24px; margin-top:24px">
    <!-- 左侧分类 -->
    <div class="sidebar">
      <h3>📂 商品分类</h3>
      <div class="cate-list">
        <div class="cate-item" :class="{active: !selectedCategory}" @click="selectedCategory=null;searchProducts(1)">✨ 全部分类</div>
        <div v-for="c in categories" :key="c.id" class="cate-item" :class="{active: selectedCategory===c.id}" @click="selectCategory(c.id)">{{ c.name }}</div>
      </div>
    </div>

    <!-- 右侧商品 -->
    <div style="flex:1">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="🔍 搜索你想要的商品..." size="large" @keyup.enter="searchProducts(1)" clearable class="warm-search">
          <template #append><el-button class="search-btn" @click="searchProducts(1)">搜索</el-button></template>
        </el-input>
      </div>

      <el-row :gutter="20">
        <el-col v-for="p in products" :key="p.id" :span="6" style="margin-bottom:20px">
          <div class="product-card" @click="router.push('/product/'+p.id)">
            <div class="img-wrap">
              <img :src="p.coverImage || 'https://picsum.photos/400/400?random='+p.id" />
              <span class="price-tag">¥{{ p.price }}</span>
            </div>
            <div class="card-body">
              <div class="pname">{{ p.name }}</div>
              <div class="pmeta"><span>已售 {{ p.sales }}</span><span>库存 {{ p.stock }}</span></div>
              <button class="cart-btn" @click.stop="addToCart(p)">🛒 加入购物车</button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-if="products.length===0" description="暂无商品" />
      <div style="text-align:center;margin-top:24px" v-if="total>8">
        <el-pagination v-model:current-page="page" :page-size="8" :total="total" background layout="prev, pager, next" @current-change="searchProducts" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.hero { text-align: center; padding: 40px 20px 24px; border-radius: var(--radius); background: linear-gradient(135deg, #FFFAF5 0%, #F9F5F0 100%); margin-bottom: 4px; }
.hero-art { max-width: 260px; margin: 0 auto 16px; }
.hero h1 { font-size: 32px; font-weight: 700; color: var(--text); margin-bottom: 6px; }
.hero p { color: var(--text-lt); font-size: 15px; }

.sidebar { width: 180px; background: var(--card); border-radius: var(--radius); padding: 20px; box-shadow: var(--shadow); height: fit-content; position: sticky; top: 80px; }
.sidebar h3 { margin-bottom: 12px; font-size: 16px; color: var(--text); font-weight: 700; }
.cate-list { display: flex; flex-direction: column; gap: 4px; }
.cate-item { padding: 10px 14px; border-radius: 12px; cursor: pointer; font-size: 14px; color: var(--text-lt); transition: all .2s; }
.cate-item:hover { background: #F0E6D8; color: var(--brown); }
.cate-item.active { background: linear-gradient(135deg, #8B5E3C, #A67C52); color: #fff; font-weight: 600; }

.search-bar { margin-bottom: 20px; }
:deep(.warm-search .el-input__wrapper) { background: #fff; border-radius: 14px; border: 1.5px solid #E8DDD0; box-shadow: none; }
:deep(.warm-search .el-input__wrapper:hover) { border-color: var(--brown-pale); }
:deep(.warm-search .el-input__wrapper.is-focus) { border-color: var(--brown); box-shadow: 0 0 0 2px rgba(139,94,60,0.1); }
.search-btn { background: var(--brown); border-color: var(--brown); color: #fff; border-radius: 0 14px 14px 0; font-family: inherit; }

.product-card { background: var(--card); border-radius: var(--radius); overflow: hidden; cursor: pointer; transition: all .3s; box-shadow: var(--shadow); }
.product-card:hover { transform: translateY(-6px); box-shadow: var(--shadow-lg); }
.img-wrap { position: relative; height: 180px; overflow: hidden; background: #F5EDE4; }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform .4s; }
.product-card:hover .img-wrap img { transform: scale(1.08); }
.price-tag { position: absolute; bottom: 8px; left: 8px; background: rgba(74,55,40,0.75); color: #FFFAF5; padding: 4px 14px; border-radius: 20px; font-size: 15px; font-weight: 700; }
.card-body { padding: 14px; }
.pname { font-size: 15px; font-weight: 600; color: var(--text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 4px; }
.pmeta { display: flex; justify-content: space-between; font-size: 12px; color: var(--brown-pale); margin-bottom: 10px; }
.cart-btn { width: 100%; padding: 9px; border: 1.5px solid var(--brown); background: transparent; color: var(--brown); border-radius: 12px; font-family: inherit; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .2s; }
.cart-btn:hover { background: var(--brown); color: #fff; }
</style>
