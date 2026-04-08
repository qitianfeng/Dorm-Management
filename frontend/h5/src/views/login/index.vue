<template>
  <div class="login-page">
    <div class="logo">
      <van-icon name="wap-home" size="60" color="#667eea" />
      <h2>宿舍管理系统</h2>
      <p>学生端</p>
    </div>
    
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">登录</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { login } from '@/api/auth'

const router = useRouter()
const username = ref('')
const password = ref('')

const onSubmit = async () => {
  if (!username.value || !password.value) {
    showToast('请输入用户名和密码')
    return
  }
  
  const loading = showLoadingToast({ message: '登录中...', forbidClick: true })
  
  try {
    const res = await login({ username: username.value, password: password.value })
    closeToast()
    
    const token = res.data.token
    const userInfo = {
      id: res.data.id,
      username: res.data.username,
      realName: res.data.realName,
      userType: res.data.userType
    }
    
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    
    showToast('登录成功')
    setTimeout(() => {
      router.push('/home')
    }, 1500)
  } catch (error) {
    closeToast()
    // 错误已在拦截器中显示
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  .logo {
    text-align: center;
    margin-bottom: 40px;
    
    h2 {
      margin: 20px 0 10px;
      color: #333;
    }
    
    p {
      color: #999;
      font-size: 14px;
    }
  }
}
</style>
