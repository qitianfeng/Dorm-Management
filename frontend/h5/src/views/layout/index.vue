<template>
  <div class="h5-layout">
    <router-view />
    <van-tabbar v-model="active" route>
      <van-tabbar-item to="/home" icon="wap-home">首页</van-tabbar-item>
      <van-tabbar-item to="/repair" icon="orders-o">报修</van-tabbar-item>
      <van-tabbar-item to="/notice" icon="volume-o">公告</van-tabbar-item>
      <van-tabbar-item to="/mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref(0)

watch(() => route.path, (path) => {
  const map = { '/home': 0, '/repair': 1, '/notice': 2, '/mine': 3 }
  active.value = map[path] || 0
}, { immediate: true })
</script>

<style scoped lang="scss">
.h5-layout {
  padding-bottom: 50px;
}
</style>
