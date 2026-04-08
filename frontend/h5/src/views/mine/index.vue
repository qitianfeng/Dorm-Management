<template>
  <div class="mine-page">
    <van-nav-bar title="我的" />
    
    <div class="user-section">
      <van-image round width="80" height="80" src="https://via.placeholder.com/80" />
      <div class="name">{{ userInfo.realName || '未登录' }}</div>
      <div class="id">学号：{{ userInfo.studentId || '-' }}</div>
    </div>

    <van-cell-group inset>
      <van-cell title="个人信息" icon="user-o" is-link />
      <van-cell title="修改密码" icon="lock" is-link />
      <van-cell title="入住记录" icon="logistics" is-link />
      <van-cell title="缴费记录" icon="balance-list-o" is-link />
    </van-cell-group>

    <van-cell-group inset style="margin-top: 15px;">
      <van-cell title="关于我们" icon="info-o" is-link />
      <van-cell title="帮助中心" icon="question-o" is-link />
    </van-cell-group>

    <div style="margin: 20px;">
      <van-button round block type="danger" @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'

const router = useRouter()
const userInfo = ref({})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})

const handleLogout = () => {
  showConfirmDialog({
    title: '提示',
    message: '确定要退出登录吗？'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  })
}
</script>

<style scoped lang="scss">
.mine-page {
  min-height: 100vh;
  background: #f5f5f5;
  
  .user-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 30px;
    text-align: center;
    color: #fff;
    
    .name {
      font-size: 20px;
      font-weight: bold;
      margin: 15px 0 5px;
    }
    
    .id {
      font-size: 14px;
      opacity: 0.8;
    }
  }
}
</style>
