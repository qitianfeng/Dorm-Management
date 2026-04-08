<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="缴费状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="未缴费" value="UNPAID" />
            <el-option label="已缴费" value="PAID" />
            <el-option label="已逾期" value="OVERDUE" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select v-model="searchForm.feeType" placeholder="请选择" clearable>
            <el-option label="住宿费" value="住宿费" />
            <el-option label="水电费" value="水电费" />
            <el-option label="押金" value="押金" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增缴费记录</el-button>
        <el-button type="success" @click="handleBatchPay" :disabled="selectedRows.length === 0">批量缴费</el-button>
        <el-button @click="handleExport">导出Excel</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentIdNumber" label="学号" width="120" />
        <el-table-column prop="feeType" label="费用类型" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="dueTime" label="应缴时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.dueTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="paidTime" label="缴费时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.paidTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handlePay(row)" v-if="row.status === 'UNPAID'">缴费</el-button>
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
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" filterable placeholder="请选择学生">
            <el-option v-for="item in students" :key="item.id" :label="`${item.realName}(${item.studentId})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="form.feeType" placeholder="请选择">
            <el-option label="住宿费" value="住宿费" />
            <el-option label="水电费" value="水电费" />
            <el-option label="押金" value="押金" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" :step="50" />
        </el-form-item>
        <el-form-item label="应缴时间" prop="dueTime">
          <el-date-picker v-model="form.dueTime" type="datetime" placeholder="选择日期时间" />
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

    <el-dialog v-model="payDialogVisible" title="缴费" width="500px">
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="支付方式">
          <el-select v-model="payForm.paymentMethod" placeholder="请选择">
            <el-option label="现金" value="现金" />
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="银行转账" value="银行转账" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易流水号">
          <el-input v-model="payForm.transactionId" placeholder="请输入交易流水号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPay" :loading="payLoading">确定缴费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const payLoading = ref(false)
const dialogVisible = ref(false)
const payDialogVisible = ref(false)
const dialogTitle = ref('新增缴费记录')
const tableData = ref([])
const students = ref([])
const selectedRows = ref([])
const formRef = ref(null)
const currentPayId = ref(null)

const searchForm = reactive({ status: '', feeType: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  studentId: null,
  feeType: '',
  amount: 0,
  dueTime: '',
  remark: ''
})
const payForm = reactive({
  paymentMethod: '',
  transactionId: ''
})

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  dueTime: [{ required: true, message: '请选择应缴时间', trigger: 'change' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/fee/page',
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
  Object.assign(searchForm, { status: '', feeType: '' })
  handleSearch()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleAdd = () => {
  dialogTitle.value = '新增缴费记录'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑缴费记录'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该缴费记录吗？', '提示', { type: 'warning' }).then(async () => {
    await request({ url: `/fee/${row.id}`, method: 'delete' })
    ElMessage.success('删除成功')
    loadData()
  })
}

const handlePay = (row) => {
  currentPayId.value = row.id
  Object.assign(payForm, { paymentMethod: '', transactionId: '' })
  payDialogVisible.value = true
}

const handleBatchPay = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要缴费的记录')
    return
  }
  const unpaidRows = selectedRows.value.filter(row => row.status === 'UNPAID')
  if (unpaidRows.length === 0) {
    ElMessage.warning('选中的记录都已缴费')
    return
  }
  ElMessage.info('批量缴费功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const data = { ...form, operatorId: userInfo.id }
        
        if (form.id) {
          await request({ url: `/fee/${form.id}`, method: 'put', data })
          ElMessage.success('更新成功')
        } else {
          await request({ url: '/fee', method: 'post', data })
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

const submitPay = async () => {
  payLoading.value = true
  try {
    await request({ url: `/fee/${currentPayId.value}/pay`, method: 'put', data: payForm })
    ElMessage.success('缴费成功')
    payDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('缴费失败:', error)
  } finally {
    payLoading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    studentId: null,
    feeType: '',
    amount: 0,
    dueTime: '',
    remark: ''
  })
}

const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}

const getStatusType = (status) => {
  const map = { UNPAID: 'danger', PAID: 'success', OVERDUE: 'warning', REFUNDED: 'info' }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = { UNPAID: '未缴费', PAID: '已缴费', OVERDUE: '已逾期', REFUNDED: '已退款' }
  return map[status] || status
}
</script>

<style scoped lang="scss">
.table-header { margin-bottom: 16px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
