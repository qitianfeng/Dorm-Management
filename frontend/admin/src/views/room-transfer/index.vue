<template>
  <div class="room-transfer-container">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="待审批" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="学生姓名/房间号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentIdNumber" label="学号" width="120" />
        <el-table-column label="原宿舍" width="180">
          <template #default="{ row }">
            {{ row.oldBuildingName }} {{ row.oldRoomNumber }} {{ row.oldBedNumber }}床
          </template>
        </el-table-column>
        <el-table-column label="目标宿舍" width="180">
          <template #default="{ row }">
            {{ row.newBuildingName }} {{ row.newRoomNumber }} {{ row.newBedNumber }}床
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="换宿原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="primary" 
              link 
              @click="handleApprove(row)"
            >
              审批
            </el-button>
            <el-button link type="info" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveDialog.visible" title="审批换宿舍申请" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生姓名">{{ approveDialog.data.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ approveDialog.data.studentIdNumber }}</el-descriptions-item>
        <el-descriptions-item label="学院班级">{{ approveDialog.data.department }} {{ approveDialog.data.className }}</el-descriptions-item>
        <el-descriptions-item label="原宿舍">
          {{ approveDialog.data.oldBuildingName }} {{ approveDialog.data.oldRoomNumber }} {{ approveDialog.data.oldBedNumber }}床
        </el-descriptions-item>
        <el-descriptions-item label="目标宿舍">
          {{ approveDialog.data.newBuildingName }} {{ approveDialog.data.newRoomNumber }} {{ approveDialog.data.newBedNumber }}床
        </el-descriptions-item>
        <el-descriptions-item label="换宿原因">{{ approveDialog.data.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatTime(approveDialog.data.applyTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批结果" required>
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!approveForm.approved" label="拒绝原因" required>
          <el-input 
            v-model="approveForm.rejectReason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="approveDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="approveDialog.loading" @click="submitApprove">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialog.visible" title="申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ detailDialog.data.id }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ detailDialog.data.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ detailDialog.data.studentIdNumber }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ detailDialog.data.department }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detailDialog.data.className }}</el-descriptions-item>
        <el-descriptions-item label="原楼栋">{{ detailDialog.data.oldBuildingName }}</el-descriptions-item>
        <el-descriptions-item label="原房间">{{ detailDialog.data.oldRoomNumber }}</el-descriptions-item>
        <el-descriptions-item label="原床位">{{ detailDialog.data.oldBedNumber }}床</el-descriptions-item>
        <el-descriptions-item label="目标楼栋">{{ detailDialog.data.newBuildingName }}</el-descriptions-item>
        <el-descriptions-item label="目标房间">{{ detailDialog.data.newRoomNumber }}</el-descriptions-item>
        <el-descriptions-item label="目标床位">{{ detailDialog.data.newBedNumber }}床</el-descriptions-item>
        <el-descriptions-item label="换宿原因" :span="2">{{ detailDialog.data.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusTagType(detailDialog.data.status)">
            {{ getStatusText(detailDialog.data.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatTime(detailDialog.data.applyTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="detailDialog.data.approveTime" label="审批时间">
          {{ formatTime(detailDialog.data.approveTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailDialog.data.approverName" label="审批人">
          {{ detailDialog.data.approverName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailDialog.data.rejectReason" label="拒绝原因" :span="2">
          {{ detailDialog.data.rejectReason }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomTransferPage, approveRoomTransfer, getRoomTransferDetail } from '@/api/room-transfer'

import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const searchForm = reactive({
  page: 1,
  size: 10,
  status: '',
  keyword: ''
})

const approveDialog = reactive({
  visible: false,
  loading: false,
  data: {}
})

const approveForm = reactive({
  id: null,
  approved: true,
  rejectReason: ''
})

const detailDialog = reactive({
  visible: false,
  data: {}
})

const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const getStatusTagType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审批',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomTransferPage({
      page: searchForm.page,
      size: searchForm.size,
      status: searchForm.status,
      keyword: searchForm.keyword
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.status = ''
  searchForm.keyword = ''
  searchForm.page = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleApprove = (row) => {
  approveDialog.data = row
  approveForm.id = row.id
  approveForm.approved = true
  approveForm.rejectReason = ''
  approveDialog.visible = true
}

const handleDetail = async (row) => {
  try {
    const res = await getRoomTransferDetail(row.id)
    detailDialog.data = res.data
    detailDialog.visible = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const submitApprove = async () => {
  if (!approveForm.approved && !approveForm.rejectReason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  approveDialog.loading = true
  try {
    await approveRoomTransfer({
      id: approveForm.id,
      approved: approveForm.approved,
      rejectReason: approveForm.rejectReason
    })
    ElMessage.success('审批成功')
    approveDialog.visible = false
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '审批失败')
  } finally {
    approveDialog.loading = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.room-transfer-container {
  padding: 20px;
  
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    margin-bottom: 20px;
  }
}
</style>
