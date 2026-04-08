<template>
  <div class="room-transfer-page">
    <van-nav-bar title="换宿舍申请" left-arrow @click-left="$router.back()" />
    
    <div v-if="loading" class="loading-container">
      <van-loading size="48px" type="spinner">加载中...</van-loading>
    </div>
    
    <template v-else>
      <!-- 当前住宿信息 -->
      <van-cell-group inset title="当前住宿信息" class="info-card">
        <van-cell title="楼栋" :value="currentRoom.buildingName || '-'" icon="location-o" />
        <van-cell title="房间号" :value="currentRoom.roomNumber || '-'" icon="hotel-o" />
        <van-cell title="床位号" :value="currentRoom.bedNumber || '-'" icon="bed-o" />
      </van-cell-group>
      
      <!-- 待处理的申请 -->
      <div v-if="pendingTransfer" class="pending-section">
        <van-notice-bar
          left-icon="info-o"
          background="#fffbe8"
          color="#ed6a0c"
        >
          您有待处理的换宿舍申请
        </van-notice-bar>
        <van-cell-group inset class="pending-card">
          <van-cell title="申请状态">
            <template #value>
              <van-tag type="warning">待审批</van-tag>
            </template>
          </van-cell>
          <van-cell title="目标楼栋" :value="pendingTransfer.newBuildingName" />
          <van-cell title="目标房间" :value="pendingTransfer.newRoomNumber" />
          <van-cell title="目标床位" :value="pendingTransfer.newBedNumber" />
          <van-cell title="申请时间" :value="formatTime(pendingTransfer.applyTime)" />
          <van-cell title="换宿原因" :value="pendingTransfer.reason" />
        </van-cell-group>
        <div class="cancel-btn">
          <van-button type="danger" size="large" round block @click="handleCancel">
            取消申请
          </van-button>
        </div>
      </div>
      
      <!-- 申请表单 -->
      <div v-else class="form-section">
        <van-cell-group inset title="选择新宿舍" class="form-card">
          <van-field
            v-model="selectedBuildingName"
            is-link
            readonly
            label="目标楼栋"
            placeholder="请选择楼栋"
            @click="showBuildingPicker = true"
          />
          <van-field
            v-model="selectedRoomName"
            is-link
            readonly
            label="目标房间"
            placeholder="请先选择楼栋"
            :disabled="!selectedBuildingId"
            @click="showRoomPicker = true"
          />
          <van-field
            v-model="form.newBedNumber"
            is-link
            readonly
            label="目标床位"
            placeholder="请先选择房间"
            :disabled="!selectedRoom"
            @click="showBedPicker = true"
          />
          <van-field
            v-model="form.reason"
            label="换宿原因"
            type="textarea"
            rows="3"
            autosize
            placeholder="请输入换宿原因（必填）"
            show-word-limit
            maxlength="200"
          />
        </van-cell-group>
        
        <div class="submit-btn">
          <van-button 
            type="primary" 
            size="large" 
            round 
            block 
            :loading="submitting"
            :disabled="!canSubmit"
            @click="handleSubmit"
          >
            提交申请
          </van-button>
        </div>
      </div>
      
      <!-- 申请历史 -->
      <van-cell-group inset title="申请历史" class="history-section">
        <van-empty v-if="historyList.length === 0" description="暂无申请记录" />
        <div v-else class="history-list">
          <div v-for="item in historyList" :key="item.id" class="history-item">
            <div class="history-header">
              <span class="history-title">{{ item.newBuildingName }} {{ item.newRoomNumber }}</span>
              <van-tag :type="getStatusTagType(item.status)">{{ getStatusText(item.status) }}</van-tag>
            </div>
            <div class="history-info">
              <span>申请时间: {{ formatTime(item.applyTime) }}</span>
            </div>
            <div v-if="item.status === 'REJECTED'" class="history-reason">
              拒绝原因: {{ item.rejectReason }}
            </div>
          </div>
        </div>
      </van-cell-group>
    </template>
    
    <!-- 楼栋选择器 -->
    <van-popup v-model:show="showBuildingPicker" position="bottom" round>
      <van-picker
        title="选择楼栋"
        :columns="buildingColumns"
        @confirm="onBuildingConfirm"
        @cancel="showBuildingPicker = false"
      />
    </van-popup>
    
    <!-- 房间选择器 -->
    <van-popup v-model:show="showRoomPicker" position="bottom" round>
      <van-picker
        title="选择房间"
        :columns="roomColumns"
        @confirm="onRoomConfirm"
        @cancel="showRoomPicker = false"
      />
    </van-popup>
    
    <!-- 床位选择器 -->
    <van-popup v-model:show="showBedPicker" position="bottom" round>
      <van-picker
        title="选择床位"
        :columns="bedColumns"
        @confirm="onBedConfirm"
        @cancel="showBedPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, showLoadingToast, closeToast } from 'vant'
import { 
  getStudentInfo, 
  getAvailableRoomsForTransfer,
  applyRoomTransfer,
  cancelRoomTransfer,
  getMyRoomTransfer,
  getMyRoomTransferHistory
} from '@/api/h5'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(true)
const submitting = ref(false)
const currentRoom = ref({})
const pendingTransfer = ref(null)
const historyList = ref([])
const buildings = ref([])
const rooms = ref([])

const form = ref({
  newRoomId: null,
  newBedNumber: '',
  reason: ''
})

const selectedBuildingId = ref(null)
const selectedBuildingName = ref('')
const selectedRoom = ref(null)
const selectedRoomName = ref('')

const showBuildingPicker = ref(false)
const showRoomPicker = ref(false)
const showBedPicker = ref(false)

const buildingColumns = computed(() => {
  const buildingMap = new Map()
  rooms.value.forEach(room => {
    if (!buildingMap.has(room.buildingId)) {
      buildingMap.set(room.buildingId, {
        text: room.buildingName,
        value: room.buildingId
      })
    }
  })
  return Array.from(buildingMap.values())
})

const roomColumns = computed(() => {
  if (!selectedBuildingId.value) return []
  return rooms.value
    .filter(r => r.buildingId === selectedBuildingId.value)
    .map(r => ({
      text: `${r.roomNumber} (${r.availableBeds}床可选)`,
      value: r.id
    }))
})

const bedColumns = computed(() => {
  if (!selectedRoom.value || !selectedRoom.value.beds) return []
  return selectedRoom.value.beds.map(bed => ({
    text: `${bed}床`,
    value: bed
  }))
})

const canSubmit = computed(() => {
  return form.value.newRoomId && form.value.newBedNumber && form.value.reason
})

const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const getStatusTagType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'default'
  }
  return types[status] || 'default'
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
    // 获取学生信息和住宿信息
    const infoRes = await getStudentInfo()
    currentRoom.value = infoRes.data?.accommodation || {}
    
    // 检查是否有住宿
    if (!currentRoom.value.roomNumber) {
      showToast('您当前没有住宿记录，无法申请换宿舍')
      router.back()
      return
    }
    
    // 获取待处理的申请
    const transferRes = await getMyRoomTransfer()
    pendingTransfer.value = transferRes.data
    
    // 获取申请历史
    const historyRes = await getMyRoomTransferHistory()
    historyList.value = historyRes.data || []
    
    // 获取可选房间列表
    const roomsRes = await getAvailableRoomsForTransfer()
    rooms.value = roomsRes.data || []
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const onBuildingConfirm = ({ selectedOptions }) => {
  const option = selectedOptions[0]
  selectedBuildingId.value = option.value
  selectedBuildingName.value = option.text
  selectedRoom.value = null
  selectedRoomName.value = ''
  form.value.newRoomId = null
  form.value.newBedNumber = ''
  showBuildingPicker.value = false
}

const onRoomConfirm = ({ selectedOptions }) => {
  const option = selectedOptions[0]
  const room = rooms.value.find(r => r.id === option.value)
  selectedRoom.value = room
  selectedRoomName.value = room?.roomNumber || ''
  form.value.newRoomId = option.value
  form.value.newBedNumber = ''
  showRoomPicker.value = false
}

const onBedConfirm = ({ selectedOptions }) => {
  const option = selectedOptions[0]
  form.value.newBedNumber = option.value
  showBedPicker.value = false
}

const handleSubmit = async () => {
  if (!canSubmit.value) {
    showToast('请完整填写申请信息')
    return
  }
  
  try {
    await showConfirmDialog({
      title: '确认提交',
      message: `确定要申请换到 ${selectedBuildingName.value} ${selectedRoomName.value} ${form.value.newBedNumber}床 吗？`
    })
    
    submitting.value = true
    await applyRoomTransfer(form.value)
    showToast('申请提交成功')
    
    // 刷新数据
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      const message = error?.response?.data?.message || '提交失败'
      showToast(message)
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '取消申请',
      message: '确定要取消该换宿舍申请吗？'
    })
    
    await cancelRoomTransfer(pendingTransfer.value.id)
    showToast('已取消申请')
    
    // 刷新数据
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      showToast('取消失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.room-transfer-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.info-card {
  margin: 15px;
}

.pending-section {
  margin: 15px;
  
  .pending-card {
    margin-top: 10px;
  }
  
  .cancel-btn {
    margin-top: 15px;
  }
}

.form-section {
  margin: 15px;
  
  .form-card {
    margin-bottom: 15px;
  }
  
  .submit-btn {
    margin-top: 15px;
  }
}

.history-section {
  margin: 15px;
  
  .history-list {
    padding: 10px 0;
  }
  
  .history-item {
    padding: 12px 16px;
    border-bottom: 1px solid #ebedf0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }
  
  .history-title {
    font-size: 14px;
    font-weight: 500;
    color: #323233;
  }
  
  .history-info {
    font-size: 12px;
    color: #969799;
  }
  
  .history-reason {
    margin-top: 8px;
    font-size: 12px;
    color: #ee0a24;
  }
}
</style>
