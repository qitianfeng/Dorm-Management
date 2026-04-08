<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="在住" value="ACTIVE" />
            <el-option label="已退宿" value="CHECKED_OUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/学号/房间" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleCheckIn">办理入住</el-button>
        <el-button type="warning" @click="handleTransfer">调宿</el-button>
        <el-button type="danger" @click="handleCheckOut">退宿</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentIdNumber" label="学号" width="120" />
        <el-table-column prop="buildingName" label="楼栋" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床位" width="80" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="deposit" label="押金" width="100">
          <template #default="{ row }">¥{{ row.deposit || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link @click="handleSingleTransfer(row)" v-if="row.status === 'ACTIVE'">调宿</el-button>
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

    <el-dialog v-model="checkInDialogVisible" title="办理入住" width="600px">
      <el-form :model="checkInForm" :rules="checkInRules" ref="checkInFormRef" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="checkInForm.studentId" filterable placeholder="请选择学生">
            <el-option v-for="item in students" :key="item.id" :label="`${item.realName}(${item.studentId})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间" prop="roomId">
          <el-cascader
            v-model="roomCascader"
            :options="buildingOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择楼栋和房间"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="床位号">
          <el-input v-model="checkInForm.bedNumber" />
        </el-form-item>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker v-model="checkInForm.checkInDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="押金">
          <el-input-number v-model="checkInForm.deposit" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckIn" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAccommodationPage, checkIn as checkInApi, transfer, checkOut } from '@/api/accommodation'
import { getBuildingList } from '@/api/building'

const loading = ref(false)
const submitLoading = ref(false)
const checkInDialogVisible = ref(false)
const tableData = ref([])
const buildings = ref([])
const students = ref([])
const selectedRows = ref([])
const checkInFormRef = ref(null)

const searchForm = reactive({ buildingId: null, roomId: null, status: '', keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const roomCascader = ref([])

const checkInForm = reactive({
  studentId: null,
  roomId: null,
  bedNumber: '',
  checkInDate: '',
  deposit: 500
})

const checkInRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
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
    const res = await getAccommodationPage({ ...pagination, ...searchForm })
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
  Object.assign(searchForm, { buildingId: null, roomId: null, status: '', keyword: '' })
  handleSearch()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleCheckIn = () => {
  checkInDialogVisible.value = true
}

const handleRoomChange = (value) => {
  if (value && value.length === 2) {
    checkInForm.roomId = value[1]
  }
}

const submitCheckIn = async () => {
  if (!checkInFormRef.value) return
  await checkInFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await checkInApi(checkInForm)
        ElMessage.success('入住成功')
        checkInDialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('入住失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleTransfer = () => {
  if (selectedRows.value.length !== 1) {
    ElMessage.warning('请选择一条记录进行调宿')
    return
  }
  if (selectedRows.value[0].status !== 'ACTIVE') {
    ElMessage.warning('只能对在住学生进行调宿')
    return
  }
  ElMessage.info('调宿功能开发中')
}

const handleCheckOut = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要退宿的记录')
    return
  }
  const hasInvalid = selectedRows.value.some(row => row.status !== 'ACTIVE')
  if (hasInvalid) {
    ElMessage.warning('只能对在住学生进行退宿')
    return
  }
  ElMessageBox.confirm('确定要为选中学生办理退宿吗？', '提示', { type: 'warning' })
    .then(async () => {
      for (const row of selectedRows.value) {
        await checkOut({ accommodationId: row.id, checkOutDate: new Date().toISOString().split('T')[0] })
      }
      ElMessage.success('退宿成功')
      loadData()
    })
}

const handleDetail = (row) => {
  ElMessage.info('详情功能开发中')
}

const handleSingleTransfer = (row) => {
  ElMessage.info('调宿功能开发中')
}

const getStatusType = (status) => {
  const map = { ACTIVE: 'success', TRANSFERRED: 'warning', CHECKED_OUT: 'info', SUSPENDED: 'danger' }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = { ACTIVE: '在住', TRANSFERRED: '已调宿', CHECKED_OUT: '已退宿', SUSPENDED: '暂停' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.table-header { margin-bottom: 16px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
