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

// 加载分类树
async function loadCategories() {
  const res = await request.get('/product/category')
  categories.value = res.data
}

// 搜索商品
async function searchProducts(p = 1) {
  page.value = p
  const params = { page: p, size: 8 }
  if (keyword.value) params.keyword = keyword.value
  if (selectedCategory.value) params.categoryId = selectedCategory.value
  const res = await request.get('/product', { params })
  products.value = res.data.records
  total.value = res.data.total
}

// 加入购物车
async function addToCart(product) {
  if (!auth.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await request.post('/cart', { productId: product.id, quantity: 1 })
  ElMessage.success('已加入购物车')
}

// 切换分类
function selectCategory(id) {
  selectedCategory.value = id
  searchProducts(1)
}

onMounted(() => {
  loadCategories()
  searchProducts()
})
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <div style="display:flex; gap:10px; margin-bottom:20px">
      <el-input v-model="keyword" placeholder="搜索商品..." style="width:300px"
        @keyup.enter="searchProducts(1)" clearable />
      <el-button type="primary" @click="searchProducts(1)">搜索</el-button>
    </div>

    <div style="display:flex; gap:20px">
      <!-- 左侧分类 -->
      <div style="width:180px">
        <h3>商品分类</h3>
        <el-menu>
          <el-menu-item v-for="c in categories" :key="c.id" @click="selectCategory(c.id)">
            {{ c.name }}
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧商品列表 -->
      <div style="flex:1">
        <el-row :gutter="20">
          <el-col v-for="p in products" :key="p.id" :span="6" style="margin-bottom:20px">
            <el-card :body-style="{ padding: '10px' }" shadow="hover">
              <img :src="p.coverImage || 'https://picsum.photos/200/200?random='+p.id"
                style="width:100%;height:150px;object-fit:cover;cursor:pointer"
                @click="router.push('/product/'+p.id)" />
              <div style="padding:8px 0">
                <div style="font-weight:bold;cursor:pointer" @click="router.push('/product/'+p.id)">
                  {{ p.name }}
                </div>
                <div style="color:#ff4d4f;font-size:18px;margin:6px 0">
                  ¥{{ p.price }}
                </div>
                <el-button type="primary" size="small" style="width:100%"
                  @click="addToCart(p)">
                  加入购物车
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div style="text-align:center;margin-top:20px">
          <el-pagination
            v-model:current-page="page"
            :page-size="8"
            :total="total"
            layout="prev, pager, next"
            @current-change="searchProducts" />
        </div>
      </div>
    </div>
  </div>
</template>
