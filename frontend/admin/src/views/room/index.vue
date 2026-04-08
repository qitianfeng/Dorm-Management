<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择楼栋" clearable>
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增房间</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="buildingName" label="所属楼栋" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="roomType" label="类型" width="100">
          <template #default="{ row }">
            {{ getRoomTypeLabel(row.roomType) }}
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="总床位" width="80" />
        <el-table-column prop="occupiedBeds" label="已住" width="80" />
        <el-table-column prop="availableBeds" label="空床位" width="80">
          <template #default="{ row }">
            <el-tag :type="row.availableBeds > 0 ? 'success' : 'danger'">{{ row.availableBeds }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="monthlyFee" label="月租" width="100">
          <template #default="{ row }">¥{{ row.monthlyFee || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoomStatusType(row.status)">{{ getRoomStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleViewStudents(row)">入住学生</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋">
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="楼层">
          <el-input-number v-model="form.floor" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="房间类型">
          <el-select v-model="form.roomType">
            <el-option label="四人间" value="FOUR_PERSON" />
            <el-option label="六人间" value="SIX_PERSON" />
            <el-option label="八人间" value="EIGHT_PERSON" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位数">
          <el-input-number v-model="form.capacity" :min="1" :max="12" />
        </el-form-item>
        <el-form-item label="月租费用">
          <el-input-number v-model="form.monthlyFee" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="设施">
          <el-input v-model="form.facilities" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="空闲" value="AVAILABLE" />
            <el-option label="维修中" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomPage, createRoom, updateRoom, deleteRoom } from '@/api/building'
import { getBuildingList } from '@/api/building'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增房间')
const tableData = ref([])
const buildings = ref([])
const formRef = ref(null)

const searchForm = reactive({ buildingId: null, keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  buildingId: null,
  roomNumber: '',
  floor: 1,
  capacity: 4,
  roomType: 'FOUR_PERSON',
  status: 'AVAILABLE',
  facilities: '',
  monthlyFee: 800
})

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

onMounted(() => {
  loadBuildings()
  loadData()
})

const loadBuildings = async () => {
  try {
    const res = await getBuildingList()
    buildings.value = res.data
  } catch (error) {
    console.error('加载楼栋失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomPage({ ...pagination, ...searchForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.buildingId = null
  searchForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增房间'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房间'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该房间吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleViewStudents = (row) => {
  router.push({ path: '/accommodation', query: { roomId: row.id } })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updateRoom(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createRoom(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    buildingId: null,
    roomNumber: '',
    floor: 1,
    capacity: 4,
    roomType: 'FOUR_PERSON',
    status: 'AVAILABLE',
    facilities: '',
    monthlyFee: 800
  })
}

const getRoomTypeLabel = (type) => {
  const map = { FOUR_PERSON: '四人间', SIX_PERSON: '六人间', EIGHT_PERSON: '八人间' }
  return map[type] || type
}

const getRoomStatusType = (status) => {
  const map = { AVAILABLE: 'success', PARTIAL: 'warning', FULL: 'danger', MAINTENANCE: 'info' }
  return map[status] || ''
}

const getRoomStatusLabel = (status) => {
  const map = { AVAILABLE: '空闲', PARTIAL: '部分占用', FULL: '已满', MAINTENANCE: '维修中' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.table-header { margin-bottom: 16px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
