<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="学生姓名/学号" clearable />
        </el-form-item>
        <el-form-item label="违纪类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="晚归" value="LATE_RETURN" />
            <el-option label="违规用电" value="ELECTRIC_VIOLATION" />
            <el-option label="损坏公物" value="PROPERTY_DAMAGE" />
            <el-option label="噪音扰民" value="NOISE_DISTURBANCE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="已处理" value="PROCESSED" />
            <el-option label="已忽略" value="IGNORED" />
          </el-select>
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
        <el-button type="primary" @click="handleAdd">新增记录</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="violationType" label="违纪类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getViolationTypeLabel(row.violationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="违纪描述" />
        <el-table-column prop="violationTime" label="违纪时间" width="160" />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="违纪类型" prop="violationType">
          <el-select v-model="form.violationType" placeholder="请选择">
            <el-option label="晚归" value="LATE_RETURN" />
            <el-option label="违规用电" value="ELECTRIC_VIOLATION" />
            <el-option label="损坏公物" value="PROPERTY_DAMAGE" />
            <el-option label="噪音扰民" value="NOISE_DISTURBANCE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="违纪时间" prop="violationTime">
          <el-date-picker v-model="form.violationTime" type="datetime" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="违纪描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已处理" value="PROCESSED" />
            <el-option label="已忽略" value="IGNORED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="form.processNote" type="textarea" :rows="2" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  studentId: '',
  violationType: '',
  violationTime: '',
  description: '',
  status: 'PENDING',
  processNote: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  violationType: [{ required: true, message: '请选择违纪类型', trigger: 'change' }],
  violationTime: [{ required: true, message: '请选择违纪时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入违纪描述', trigger: 'blur' }]
}

const getViolationTypeLabel = (type) => {
  const map = {
    'LATE_RETURN': '晚归',
    'ELECTRIC_VIOLATION': '违规用电',
    'PROPERTY_DAMAGE': '损坏公物',
    'NOISE_DISTURBANCE': '噪音扰民',
    'OTHER': '其他'
  }
  return map[type] || type
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PROCESSED': 'success',
    'IGNORED': 'info'
  }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSED': '已处理',
    'IGNORED': '已忽略'
  }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await fetch(`/api/disciplinary/page?${new URLSearchParams(params)}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
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
  searchForm.type = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增记录'
  isEdit.value = false
  Object.assign(form, {
    id: null,
    studentId: '',
    violationType: '',
    violationTime: '',
    description: '',
    status: 'PENDING',
    processNote: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑记录'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const url = isEdit.value ? `/api/disciplinary/${form.id}` : '/api/disciplinary'
    const method = isEdit.value ? 'PUT' : 'POST'
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(form)
    })
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/disciplinary/${row.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch {
    // 取消删除
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
.table-header {
  margin-bottom: 15px;
}
</style>
