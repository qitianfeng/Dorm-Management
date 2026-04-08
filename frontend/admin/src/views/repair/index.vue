<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="学生姓名/房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="studentName" label="申请人" width="100" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="repairType" label="报修类型" width="120" />
        <el-table-column prop="description" label="问题描述" show-overflow-tooltip />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">{{ getPriorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link @click="handleProcess(row)" v-if="row.status === 'PENDING'">处理</el-button>
            <el-button type="danger" link @click="handleCancel(row)" v-if="row.status === 'PENDING'">取消</el-button>
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

    <el-dialog v-model="processDialogVisible" title="处理报修" width="500px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="processForm.handleResult" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="维修费用">
          <el-input-number v-model="processForm.repairCost" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="processForm.status">
            <el-radio label="PROCESSING">处理中</el-radio>
            <el-radio label="COMPLETED">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRepairPage, handleRepair, cancelRepair } from '@/api/repair'

const loading = ref(false)
const submitLoading = ref(false)
const processDialogVisible = ref(false)
const tableData = ref([])
const currentRow = ref(null)

const searchForm = reactive({ status: '', keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const processForm = reactive({
  handleResult: '',
  repairCost: 0,
  status: 'PROCESSING'
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRepairPage({ ...pagination, ...searchForm })
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
  searchForm.status = ''
  searchForm.keyword = ''
  handleSearch()
}

const handleDetail = (row) => {
  ElMessage.info('详情功能开发中')
}

const handleProcess = (row) => {
  currentRow.value = row
  Object.assign(processForm, { handleResult: '', repairCost: 0, status: 'PROCESSING' })
  processDialogVisible.value = true
}

const submitProcess = async () => {
  submitLoading.value = true
  try {
    await handleRepair(currentRow.value.id, processForm)
    ElMessage.success('处理成功')
    processDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('处理失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该报修申请吗？', '提示', { type: 'warning' })
    .then(async () => {
      await cancelRepair(row.id)
      ElMessage.success('取消成功')
      loadData()
    })
}

const getPriorityType = (priority) => {
  const map = { LOW: 'info', NORMAL: '', HIGH: 'warning', URGENT: 'danger' }
  return map[priority] || ''
}

const getPriorityLabel = (priority) => {
  const map = { LOW: '低', NORMAL: '普通', HIGH: '高', URGENT: '紧急' }
  return map[priority] || priority
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', PROCESSING: '', COMPLETED: 'success', CANCELLED: 'info' }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = { PENDING: '待处理', PROCESSING: '处理中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
