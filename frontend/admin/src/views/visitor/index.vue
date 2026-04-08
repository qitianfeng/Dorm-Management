<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="访问中" value="VISITING" />
            <el-option label="已离开" value="LEFT" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="访客姓名/被访学生" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">登记访客</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="visitorName" label="访客姓名" width="100" />
        <el-table-column prop="visitorPhone" label="联系电话" width="120" />
        <el-table-column prop="visitorType" label="访客类型" width="100" />
        <el-table-column prop="roomNumber" label="被访房间" width="100" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="visitStudentName" label="被访学生" width="100" />
        <el-table-column prop="visitReason" label="来访事由" show-overflow-tooltip />
        <el-table-column prop="visitTime" label="来访时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.visitTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleLeave(row)" v-if="row.status === 'VISITING'">登记离开</el-button>
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
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.visitorPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="访客类型">
          <el-select v-model="form.visitorType" placeholder="请选择">
            <el-option label="家长" value="家长" />
            <el-option label="朋友" value="朋友" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="被访房间" prop="visitRoomId">
          <el-cascader
            v-model="roomCascader"
            :options="buildingOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择楼栋和房间"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="被访学生" prop="visitStudentId">
          <el-select v-model="form.visitStudentId" filterable placeholder="请选择学生">
            <el-option v-for="item in students" :key="item.id" :label="`${item.realName}(${item.studentId})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="来访事由" prop="visitReason">
          <el-input v-model="form.visitReason" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="来访时间" prop="visitTime">
          <el-date-picker v-model="form.visitTime" type="datetime" placeholder="选择日期时间" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingList } from '@/api/building'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('登记访客')
const tableData = ref([])
const buildings = ref([])
const students = ref([])
const formRef = ref(null)
const roomCascader = ref([])

const searchForm = reactive({ status: '', keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  visitorName: '',
  visitorPhone: '',
  idCard: '',
  visitorType: '',
  visitRoomId: null,
  visitStudentId: null,
  visitStudentName: '',
  visitReason: '',
  visitTime: '',
  remark: '',
  register: ''
})

const rules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitRoomId: [{ required: true, message: '请选择被访房间', trigger: 'change' }],
  visitStudentId: [{ required: true, message: '请选择被访学生', trigger: 'change' }],
  visitReason: [{ required: true, message: '请输入来访事由', trigger: 'blur' }],
  visitTime: [{ required: true, message: '请选择来访时间', trigger: 'change' }]
}

const buildingOptions = computed(() => {
  return buildings.value.map(b => ({
    id: b.id,
    name: b.buildingName,
    children: []
  }))
})

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
    const res = await request({
      url: '/visitor/page',
      method: 'get',
      params: { ...pagination, ...searchForm }
    })
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
  Object.assign(searchForm, { status: '', keyword: '' })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '登记访客'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑访客信息'
  Object.assign(form, row)
  roomCascader.value = [row.buildingId, row.visitRoomId]
  dialogVisible.value = true
}

const handleLeave = (row) => {
  ElMessageBox.confirm('确定要登记该访客离开吗？', '提示', { type: 'warning' }).then(async () => {
    await request({ url: `/visitor/${row.id}/leave`, method: 'put' })
    ElMessage.success('已登记离开')
    loadData()
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该访客记录吗？', '提示', { type: 'warning' }).then(async () => {
    await request({ url: `/visitor/${row.id}`, method: 'delete' })
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleRoomChange = (value) => {
  if (value && value.length === 2) {
    form.visitRoomId = value[1]
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        form.register = userInfo.realName
        
        if (form.id) {
          await request({ url: `/visitor/${form.id}`, method: 'put', data: form })
          ElMessage.success('更新成功')
        } else {
          await request({ url: '/visitor', method: 'post', data: form })
          ElMessage.success('登记成功')
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
    visitorName: '',
    visitorPhone: '',
    idCard: '',
    visitorType: '',
    visitRoomId: null,
    visitStudentId: null,
    visitStudentName: '',
    visitReason: '',
    visitTime: '',
    remark: '',
    register: ''
  })
  roomCascader.value = []
}

const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}

const getStatusType = (status) => {
  const map = { VISITING: 'warning', LEFT: 'success' }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = { VISITING: '访问中', LEFT: '已离开' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.table-header { margin-bottom: 16px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
