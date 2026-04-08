<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="楼栋编号/名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增楼栋</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="buildingCode" label="楼栋编号" width="120" />
        <el-table-column prop="buildingName" label="楼栋名称" />
        <el-table-column prop="buildingType" label="楼栋类型" width="120" />
        <el-table-column prop="totalFloors" label="楼层数" width="80" />
        <el-table-column prop="totalRooms" label="房间数" width="80" />
        <el-table-column prop="totalBeds" label="总床位" width="80" />
        <el-table-column prop="availableBeds" label="空床位" width="80">
          <template #default="{ row }">
            <el-tag :type="row.availableBeds > 0 ? 'success' : 'danger'">
              {{ row.availableBeds }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="managerName" label="宿管" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleViewRooms(row)">查看房间</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="楼栋编号" prop="buildingCode">
          <el-input v-model="form.buildingCode" placeholder="请输入楼栋编号" />
        </el-form-item>
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼栋类型">
          <el-select v-model="form.buildingType" placeholder="请选择">
            <el-option label="男生宿舍" value="男生宿舍" />
            <el-option label="女生宿舍" value="女生宿舍" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数">
          <el-input-number v-model="form.totalFloors" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="房间数">
          <el-input-number v-model="form.totalRooms" :min="1" />
        </el-form-item>
        <el-form-item label="总床位数">
          <el-input-number v-model="form.totalBeds" :min="1" />
        </el-form-item>
        <el-form-item label="宿管姓名">
          <el-input v-model="form.managerName" />
        </el-form-item>
        <el-form-item label="宿管电话">
          <el-input v-model="form.managerPhone" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="正常" value="ACTIVE" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
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
import { getBuildingPage, createBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增楼栋')
const tableData = ref([])
const formRef = ref(null)

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  buildingCode: '',
  buildingName: '',
  buildingType: '',
  totalFloors: 1,
  totalRooms: 0,
  totalBeds: 0,
  managerName: '',
  managerPhone: '',
  location: '',
  status: 'ACTIVE',
  remark: ''
})

const rules = {
  buildingCode: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }],
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBuildingPage({ ...pagination, ...searchForm })
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
  searchForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增楼栋'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑楼栋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该楼栋吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteBuilding(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleViewRooms = (row) => {
  router.push({ path: '/room', query: { buildingId: row.id } })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updateBuilding(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createBuilding(form)
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
    buildingCode: '',
    buildingName: '',
    buildingType: '',
    totalFloors: 1,
    totalRooms: 0,
    totalBeds: 0,
    managerName: '',
    managerPhone: '',
    location: '',
    status: 'ACTIVE',
    remark: ''
  })
}

const getStatusType = (status) => {
  const map = { ACTIVE: 'success', MAINTENANCE: 'warning', CLOSED: 'danger' }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = { ACTIVE: '正常', MAINTENANCE: '维修中', CLOSED: '已关闭' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.table-header {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
