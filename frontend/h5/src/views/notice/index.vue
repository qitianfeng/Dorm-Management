<template>
  <div class="notice-page">
    <van-nav-bar title="公告通知" left-arrow @click-left="$router.back()" />
    
    <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
      <van-cell-group inset>
        <van-cell v-for="item in list" :key="item.id" :title="item.title" :label="item.publishTime" is-link />
      </van-cell-group>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnnouncements } from '@/api/h5'
import dayjs from 'dayjs'

const loading = ref(false)
const finished = ref(true) // 一次性加载全部
const list = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    list.value = (res.data || []).map(item => ({
      ...item,
      publishTime: item.publishTime ? dayjs(item.publishTime).format('YYYY-MM-DD') : ''
    }))
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.notice-page {
  min-height: 100vh;
  background: #f5f5f5;
  
  .van-cell-group {
    margin: 10px;
  }
}
</style>
