<template>
  <div class="home-page">
    <van-nav-bar title="宿舍管理系统" />
    
    <div class="user-card">
      <div class="user-info">
        <van-image round width="60" height="60" src="https://via.placeholder.com/60" />
        <div class="user-detail">
          <div class="name">{{ userInfo.realName || '未登录' }}</div>
          <div class="info">{{ userInfo.department || '' }} {{ userInfo.className || '' }}</div>
        </div>
      </div>
    </div>

    <van-cell-group inset class="info-card">
      <van-cell title="我的宿舍" :value="accommodation.roomNumber || '未分配'" icon="location-o" />
      <van-cell title="楼栋" :value="accommodation.buildingName || '-'" icon="cluster-o" />
      <van-cell title="床位号" :value="accommodation.bedNumber || '-'" icon="bed-o" />
      <van-cell title="入住日期" :value="accommodation.checkInDate || '-'" icon="calendar-o" />
    </van-cell-group>

    <van-cell-group inset title="快捷功能" class="quick-actions">
      <van-grid :column-num="4">
        <!-- 只有未入住的学生才显示选宿舍入口 -->
        <van-grid-item v-if="!hasAccommodation" icon="home-o" text="选宿舍" to="/dorm-selection" />
        <!-- 已入住学生显示换宿舍入口 -->
        <van-grid-item v-if="hasAccommodation" icon="exchange" text="换宿舍" to="/room-transfer" />
        <van-grid-item icon="records" text="报修申请" to="/repair" />
        <van-grid-item icon="todo-list-o" text="报修记录" to="/repair" />
        <van-grid-item icon="balance-list-o" text="缴费记录" />
        <van-grid-item icon="description" text="卫生检查" />
        <van-grid-item icon="wap-home-o" text="退宿申请" @click="handleCheckOut" />
      </van-grid>
    </van-cell-group>

    <van-cell-group inset title="最新公告" class="notice-card">
      <van-cell v-for="item in notices" :key="item.id" :title="item.title" :label="item.publishTime ? dayjs(item.publishTime).format('YYYY-MM-DD') : ''" is-link @click="showNoticeDetail(item)" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showLoadingToast, closeToast, showToast } from 'vant'
import { getStudentInfo, getAnnouncements, applyCheckOut } from '@/api/h5'
import dayjs from 'dayjs'

const router = useRouter()
const userInfo = ref({})
const accommodation = ref({})
const notices = ref([])

// 计算属性：判断学生是否已有住宿
const hasAccommodation = computed(() => {
  return accommodation.value && accommodation.value.roomNumber
})

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }
  
  try {
    // 获取学生信息和住宿信息
    const infoRes = await getStudentInfo()
    userInfo.value = infoRes.data
    accommodation.value = infoRes.data.accommodation || {}
    
    // 获取公告列表
    const annRes = await getAnnouncements()
    notices.value = annRes.data || []
  } catch (error) {
    // 未登录或token过期
    if (error.message.includes('401') || error.message.includes('登录')) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
  }
})

const handleCheckOut = async () => {
  if (!accommodation.value || !accommodation.value.id) {
    showToast('您当前没有住宿记录')
    return
  }
  
  try {
    await showConfirmDialog({
      title: '退宿申请',
      message: '确定要申请退宿吗？退宿后需要重新选宿舍或等待分配。'
    })
    
    const loading = showLoadingToast({ message: '处理中...', forbidClick: true })
    await applyCheckOut()
    closeToast()
    
    showToast('退宿申请成功')
    // 刷新数据
    const infoRes = await getStudentInfo()
    accommodation.value = infoRes.data.accommodation || {}
  } catch (error) {
    if (error !== 'cancel') {
      closeToast()
    }
  }
}

const showNoticeDetail = (item) => {
  showConfirmDialog({
    title: item.title,
    message: item.content || '暂无内容',
    confirmButtonText: '关闭',
    showCancelButton: false
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
.home-page {
  .user-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
    color: #fff;
    
    .user-info {
      display: flex;
      align-items: center;
      
      .user-detail {
        margin-left: 15px;
        
        .name {
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 5px;
        }
        
        .info {
          font-size: 14px;
          opacity: 0.8;
        }
      }
    }
  }
  
  .info-card {
    margin: 15px;
  }
  
  .quick-actions {
    margin: 15px;
  }
  
  .notice-card {
    margin: 15px;
  }
}
</style>
