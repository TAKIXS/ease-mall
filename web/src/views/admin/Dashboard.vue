<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, Goods, FolderOpened, User, List } from '@element-plus/icons-vue'

// ===== 数据看板 =====
const dashboard = ref({})

async function loadDashboard() {
  const res = await request.get('/admin/dashboard')
  dashboard.value = res.data
}

// ===== 用户管理 =====
const users = ref([]); const userPage = ref(1); const userTotal = ref(0)

async function loadUsers(p = 1) {
  userPage.value = p
  const res = await request.get('/admin/users', { params: { page: p, size: 10 } })
  users.value = res.data.records; userTotal.value = res.data.total
}

async function toggleUser(uid) {
  await request.put(`/admin/users/${uid}/status`)
  loadUsers(userPage.value)
}

// ===== 订单管理 =====
const orders = ref([]); const orderPage = ref(1); const orderTotal = ref(0)

async function loadOrders(p = 1) {
  orderPage.value = p
  const res = await request.get('/admin/orders', { params: { page: p, size: 10 } })
  orders.value = res.data.records; orderTotal.value = res.data.total
}

async function shipOrder(oid) {
  await request.put(`/order/${oid}/ship`)
  ElMessage.success('已发货'); loadOrders(orderPage.value)
}

async function completeOrder(oid) {
  await request.put(`/order/${oid}/complete`)
  ElMessage.success('已完成'); loadOrders(orderPage.value)
}

// ===== 商品管理 =====
const products = ref([]); const prodPage = ref(1); const prodTotal = ref(0)
const prodDialog = ref(false); const editProd = ref(null)
const prodForm = ref({ name: '', price: 0, stock: 0, categoryId: null, description: '', specs: '' })

async function loadProducts(p = 1) {
  prodPage.value = p
  const res = await request.get('/product', { params: { page: p, size: 10 } })
  products.value = res.data.records; prodTotal.value = res.data.total
}

function openAddProd() {
  editProd.value = null
  Object.keys(prodForm.value).forEach(k => prodForm.value[k] = k === 'price' || k === 'stock' ? 0 : '')
  prodDialog.value = true
}

function openEditProd(p) {
  editProd.value = p.id
  prodForm.value = { name: p.name, price: p.price, stock: p.stock, categoryId: p.categoryId, description: p.description || '', specs: p.specs || '' }
  prodDialog.value = true
}

async function saveProd() {
  if (editProd.value) {
    await request.put(`/product/${editProd.value}`, prodForm.value)
  } else {
    await request.post('/product', { ...prodForm.value, status: 1 })
  }
  ElMessage.success('已保存'); prodDialog.value = false; loadProducts()
}

async function toggleProd(id) {
  await request.put(`/product/${id}/status`)
  loadProducts(prodPage.value)
}

// ===== 分类管理 =====
const categories = ref([])
const cateDialog = ref(false); const editCate = ref(null)
const cateForm = ref({ name: '', parentId: null, sort: 0 })

async function loadCategories() {
  const res = await request.get('/product/category')
  categories.value = res.data
}

function openAddCate(parentId) {
  editCate.value = null
  cateForm.value = { name: '', parentId: parentId || null, sort: 0 }
  cateDialog.value = true
}

function openEditCate(c) {
  editCate.value = c.id
  cateForm.value = { name: c.name, parentId: null, sort: c.sort }
  cateDialog.value = true
}

async function saveCate() {
  const params = new URLSearchParams(cateForm.value)
  if (editCate.value) {
    await request.put(`/product/category/${editCate.value}?${params}`)
  } else {
    await request.post(`/product/category?${params}`)
  }
  ElMessage.success('已保存'); cateDialog.value = false; loadCategories()
}

async function deleteCate(id) {
  await ElMessageBox.confirm('确定删除？')
  await request.delete(`/product/category/${id}`)
  loadCategories()
}

// ===== Tab =====
const activeTab = ref('dashboard')
const tabs = [
  { name: 'dashboard', label: '数据看板', icon: DataAnalysis },
  { name: 'orders', label: '订单管理', icon: List },
  { name: 'products', label: '商品管理', icon: Goods },
  { name: 'categories', label: '分类管理', icon: FolderOpened },
  { name: 'users', label: '用户管理', icon: User },
]

function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'dashboard') loadDashboard()
  else if (tab === 'orders') loadOrders()
  else if (tab === 'products') loadProducts()
  else if (tab === 'categories') loadCategories()
  else if (tab === 'users') loadUsers()
}

onMounted(() => { loadDashboard(); loadUsers() })
</script>

<template>
  <div class="admin-wrap">
    <h2 style="margin-bottom:20px;font-weight:700;color:var(--text)">管理后台</h2>

    <!-- Tab 按钮 -->
    <div class="tab-bar">
      <button v-for="t in tabs" :key="t.name"
        class="tab-btn" :class="{active: activeTab===t.name}" @click="switchTab(t.name)">
        <el-icon :size="16"><component :is="t.icon" /></el-icon> {{ t.label }}
      </button>
    </div>

    <!-- ========== 数据看板 ========== -->
    <div v-if="activeTab==='dashboard'" class="stat-grid">
      <div v-for="c in [
        {l:'总用户',v:dashboard.totalUsers||0,bg:'#F5EDE4',clr:'#8B5E3C'},
        {l:'总订单',v:dashboard.totalOrders||0,bg:'#EDF0E8',clr:'#5B9A8B'},
        {l:'今日订单',v:dashboard.todayOrders||0,bg:'#E8EEF5',clr:'#6B8DB5'},
        {l:'待发货',v:dashboard.pendingShip||0,bg:'#F5E8E8',clr:'#C48B8B'},
        {l:'商品数',v:dashboard.totalProducts||0,bg:'#EEE8F0',clr:'#8B6B9E'},
        {l:'总GMV',v:'¥'+(dashboard.totalGMV||0),bg:'#F5F0E8',clr:'#B8954A'},
        {l:'今日GMV',v:'¥'+(dashboard.todayGMV||0),bg:'#E8F0F0',clr:'#5B8B8B'},
      ]" :key="c.l" class="stat-card" :style="{background:c.bg}">
        <span class="stat-num" :style="{color:c.clr}">{{ c.v }}</span>
        <span class="stat-label">{{ c.l }}</span>
      </div>
    </div>

    <!-- ========== 订单管理 ========== -->
    <div v-if="activeTab==='orders'" class="table-card">
      <el-table :data="orders" class="warm-table">
        <el-table-column prop="orderNo" label="编号" width="190" />
        <el-table-column label="金额" width="100"><template #default="{row}">¥{{ row.totalAmount }}</template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}">{{ {1:'待支付',2:'已支付',3:'已发货',4:'已完成',5:'已取消'}[row.status] }}</template></el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="80" />
        <el-table-column label="操作" min-width="160">
          <template #default="{row}">
            <button v-if="row.status===2" class="btn-sm" style="background:#8B5E3C;color:#fff" @click="shipOrder(row.id)">发货</button>
            <button v-if="row.status===3" class="btn-sm" style="background:#5B9A8B;color:#fff" @click="completeOrder(row.id)">完成</button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:16px">
        <el-pagination v-model:current-page="orderPage" :total="orderTotal" background layout="prev, pager, next" @current-change="loadOrders" />
      </div>
    </div>

    <!-- ========== 商品管理 ========== -->
    <div v-if="activeTab==='products'" class="table-card">
      <el-button type="primary" @click="openAddProd" style="margin-bottom:16px">新增商品</el-button>
      <el-table :data="products" class="warm-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column label="价格" width="100"><template #default="{row}">¥{{ row.price }}</template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}">{{ row.status===1?'上架':'下架' }}</template></el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <button class="btn-sm outline" @click="openEditProd(row)">编辑</button>
            <button class="btn-sm outline" @click="toggleProd(row.id)">{{ row.status===1?'下架':'上架' }}</button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:16px">
        <el-pagination v-model:current-page="prodPage" :total="prodTotal" background layout="prev, pager, next" @current-change="loadProducts" />
      </div>
    </div>

    <!-- ========== 分类管理 ========== -->
    <div v-if="activeTab==='categories'" class="table-card">
      <el-button type="primary" @click="openAddCate(null)" style="margin-bottom:16px">新增一级分类</el-button>
      <div class="cate-tree">
        <template v-for="c in categories" :key="c.id">
          <div class="cate-row" style="font-weight:700">
            <span>{{ c.name }}</span>
            <span>
              <button class="btn-sm outline" @click="openAddCate(c.id)">+子分类</button>
              <button class="btn-sm outline" @click="openEditCate(c)">编辑</button>
              <button class="btn-sm outline" style="color:#c0392b" @click="deleteCate(c.id)">删除</button>
            </span>
          </div>
          <div v-for="sub in c.children" :key="sub.id" class="cate-row" style="padding-left:40px">
            <span>{{ sub.name }}</span>
            <span>
              <button class="btn-sm outline" @click="openEditCate(sub)">编辑</button>
              <button class="btn-sm outline" style="color:#c0392b" @click="deleteCate(sub.id)">删除</button>
            </span>
          </div>
        </template>
      </div>
    </div>

    <!-- ========== 用户管理 ========== -->
    <div v-if="activeTab==='users'" class="table-card">
      <el-table :data="users" class="warm-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{row}"><span class="tag" :class="row.status===1?'on':'off'">{{ row.status===1?'正常':'禁用' }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="{row}"><button class="btn-sm outline" @click="toggleUser(row.id)">{{ row.status===1?'禁用':'启用' }}</button></template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:16px">
        <el-pagination v-model:current-page="userPage" :total="userTotal" background layout="prev, pager, next" @current-change="loadUsers" />
      </div>
    </div>

    <!-- ===== 商品/分类弹窗 ===== -->
    <el-dialog v-model="prodDialog" :title="editProd?'编辑商品':'新增商品'" width="460px">
      <el-form :model="prodForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="prodForm.name" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="价格"><el-input v-model.number="prodForm.price" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="库存"><el-input v-model.number="prodForm.stock" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="分类ID"><el-input v-model.number="prodForm.categoryId" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述"><el-input v-model="prodForm.description" type="textarea" /></el-form-item>
        <el-form-item label="规格JSON"><el-input v-model="prodForm.specs" placeholder='{"颜色":"红,蓝","尺寸":"S,M"}' /></el-form-item>
      </el-form>
      <template #footer><el-button @click="prodDialog=false">取消</el-button><el-button type="primary" @click="saveProd">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="cateDialog" :title="editCate?'编辑分类':'新增分类'" width="400px">
      <el-form :model="cateForm" label-width="60px">
        <el-form-item label="名称"><el-input v-model="cateForm.name" /></el-form-item>
        <el-form-item label="排序"><el-input v-model.number="cateForm.sort" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="cateDialog=false">取消</el-button><el-button type="primary" @click="saveCate">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-wrap { max-width: 1100px; margin: 0 auto; }

/* Tab 按钮 */
.tab-bar { display: flex; gap: 8px; margin-bottom: 24px; }
.tab-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 10px 20px; border: 1.5px solid #E8DDD0; background: var(--card);
  border-radius: 12px; font-family: inherit; font-size: 14px; color: var(--text-lt);
  cursor: pointer; transition: all .2s;
}
.tab-btn:hover { border-color: var(--brown); color: var(--brown); }
.tab-btn.active { background: var(--brown); color: #fff; border-color: var(--brown); }

/* 统计卡片 */
.stat-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; }
.stat-card { border-radius: 16px; padding: 24px; transition: transform .2s; }
.stat-card:hover { transform: translateY(-3px); }
.stat-num { font-size: 28px; font-weight: 700; display: block; }
.stat-label { font-size: 12px; color: var(--text-lt); margin-top: 4px; }

/* 表格卡片 */
.table-card { background: var(--card); border-radius: var(--radius); padding: 20px; box-shadow: var(--shadow); }

/* 分类树 */
.cate-tree { display: flex; flex-direction: column; gap: 2px; }
.cate-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-radius: 10px; border: 1px solid #F0E6D8; }

/* 标签 & 小按钮 */
.tag { padding: 3px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.tag.on { background: #E8F0E8; color: #5B9A8B; } .tag.off { background: #F5E8E8; color: #C48B8B; }
.btn-sm { padding: 4px 14px; border-radius: 8px; font-family: inherit; font-size: 12px; cursor: pointer; border: none; margin: 2px; }
.btn-sm.outline { border: 1.5px solid #E8DDD0; background: none; color: var(--text); }
.btn-sm.outline:hover { border-color: var(--brown); color: var(--brown); }

:deep(.warm-table) { --el-table-border-color: #F0E6D8; --el-table-header-bg-color: #F9F5F0; }
</style>
