<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Edit, Delete, Plus } from '@element-plus/icons-vue'

const router = useRouter()

// ============ 个人信息 ============
const profile = ref({})
const editMode = ref(false)
const profileForm = reactive({ nickname: '', phone: '', email: '', gender: 0 })

async function loadProfile() {
  const res = await request.get('/user/profile')
  profile.value = res.data
  Object.assign(profileForm, res.data)
}

async function saveProfile() {
  await request.put('/user/profile', profileForm)
  ElMessage.success('个人信息已更新')
  editMode.value = false
  loadProfile()
}

// ============ 地址管理 ============
const addresses = ref([])
const showAddrDialog = ref(false)
const editingAddr = ref(null)
const addrForm = reactive({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

async function loadAddresses() {
  const res = await request.get('/user/address')
  addresses.value = res.data
}

function openAddAddr() {
  editingAddr.value = null
  Object.keys(addrForm).forEach(k => addrForm[k] = '')
  addrForm.isDefault = 0
  showAddrDialog.value = true
}

function openEditAddr(addr) {
  editingAddr.value = addr.id
  Object.assign(addrForm, addr)
  showAddrDialog.value = true
}

async function saveAddr() {
  if (!addrForm.receiverName || !addrForm.receiverPhone || !addrForm.province || !addrForm.city || !addrForm.district || !addrForm.detail) {
    ElMessage.warning('请填写完整地址信息'); return
  }
  if (editingAddr.value) {
    await request.put(`/user/address/${editingAddr.value}`, addrForm)
    ElMessage.success('地址已更新')
  } else {
    await request.post('/user/address', addrForm)
    ElMessage.success('地址已添加')
  }
  showAddrDialog.value = false
  loadAddresses()
}

async function deleteAddr(id) {
  await ElMessageBox.confirm('确定删除这个地址吗？')
  await request.delete(`/user/address/${id}`)
  ElMessage.success('已删除')
  loadAddresses()
}

onMounted(() => { loadProfile(); loadAddresses() })
</script>

<template>
  <div class="profile-wrap">
    <div class="page-header">
      <h2>个人中心</h2>
      <el-button :icon="HomeFilled" @click="router.push('/home')" class="home-btn">返回首页</el-button>
    </div>

    <!-- 个人信息卡片 -->
    <div class="card">
      <div class="card-title">
        <span>基本信息</span>
        <el-button v-if="!editMode" :icon="Edit" size="small" text type="primary" @click="editMode=true">编辑</el-button>
      </div>
      <el-descriptions v-if="!editMode" :column="2" border>
        <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ profile.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ profile.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ profile.createTime?.substring(0,10) }}</el-descriptions-item>
      </el-descriptions>

      <el-form v-else :model="profileForm" label-width="80px" class="profile-form">
        <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
          <el-button @click="editMode=false">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 地址管理卡片 -->
    <div class="card" style="margin-top:20px">
      <div class="card-title">
        <span>常用地址</span>
        <el-button :icon="Plus" size="small" type="primary" @click="openAddAddr">新增地址</el-button>
      </div>

      <div v-if="addresses.length" class="addr-list">
        <div v-for="a in addresses" :key="a.id" class="addr-item" :class="{default: a.isDefault===1}">
          <div class="addr-top">
            <span class="addr-name">{{ a.receiverName }}</span>
            <span class="addr-phone">{{ a.receiverPhone }}</span>
            <el-tag v-if="a.isDefault===1" size="small" type="warning">默认</el-tag>
          </div>
          <div class="addr-detail">{{ a.province }} {{ a.city }} {{ a.district }} {{ a.detail }}</div>
          <div class="addr-actions">
            <el-button :icon="Edit" size="small" text @click="openEditAddr(a)">编辑</el-button>
            <el-button :icon="Delete" size="small" text type="danger" @click="deleteAddr(a.id)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="还没有地址，添加一个吧" :image-size="80" />
    </div>

    <!-- 地址弹窗 -->
    <el-dialog v-model="showAddrDialog" :title="editingAddr ? '编辑地址' : '新增地址'" width="480px">
      <el-form :model="addrForm" label-width="80px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="收件人"><el-input v-model="addrForm.receiverName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="addrForm.receiverPhone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="省"><el-input v-model="addrForm.province" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="市"><el-input v-model="addrForm.city" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="区"><el-input v-model="addrForm.district" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="详细地址"><el-input v-model="addrForm.detail" /></el-form-item>
        <el-form-item label="设为默认"><el-switch v-model="addrForm.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddrDialog=false">取消</el-button><el-button type="primary" @click="saveAddr">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-wrap { max-width: 800px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
h2 { font-weight: 700; color: var(--text); }
.home-btn { border: 1.5px solid var(--brown); color: var(--brown); background: none; border-radius: 10px; font-family: inherit; }
.home-btn:hover { background: var(--brown); color: #fff; }

.card { background: var(--card); border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); }
.card-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; font-size: 16px; font-weight: 700; color: var(--text); }
.profile-form { max-width: 400px; }

.addr-list { display: flex; flex-direction: column; gap: 12px; }
.addr-item { border: 1.5px solid #F0E6D8; border-radius: 14px; padding: 16px; transition: .2s; }
.addr-item.default { border-color: var(--brown); background: #FFF9F2; }
.addr-top { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.addr-name { font-weight: 700; color: var(--text); }
.addr-phone { color: var(--text-lt); font-size: 14px; }
.addr-detail { color: var(--text-lt); font-size: 14px; margin-bottom: 8px; }
.addr-actions { display: flex; gap: 4px; }
</style>
