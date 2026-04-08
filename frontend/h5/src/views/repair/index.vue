<template>
  <div class="repair-page">
    <van-nav-bar title="报修申请" left-arrow @click-left="$router.back()" />
    
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="form.roomNumber"
          label="房间号"
          placeholder="请输入房间号"
          :rules="[{ required: true, message: '请输入房间号' }]"
        />
        
        <van-field
          v-model="form.repairType"
          is-link
          readonly
          label="报修类型"
          placeholder="请选择"
          @click="showTypePicker = true"
          :rules="[{ required: true, message: '请选择报修类型' }]"
        />
        
        <van-field
          v-model="form.description"
          rows="3"
          autosize
          label="问题描述"
          type="textarea"
          placeholder="请描述您遇到的问题"
          :rules="[{ required: true, message: '请描述问题' }]"
        />
        
        <van-field name="uploader" label="图片上传">
          <template #input>
            <van-uploader v-model="fileList" multiple :max-count="3" />
          </template>
        </van-field>
      </van-cell-group>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">提交申请</van-button>
      </div>
    </van-form>

    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="repairTypes"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { submitRepair } from '@/api/h5'

const router = useRouter()
const showTypePicker = ref(false)
const fileList = ref([])

const form = reactive({
  roomNumber: '',
  repairType: '',
  description: ''
})

const repairTypes = [
  { text: '水电维修', value: '水电维修' },
  { text: '门窗维修', value: '门窗维修' },
  { text: '家具维修', value: '家具维修' },
  { text: '空调维修', value: '空调维修' },
  { text: '网络故障', value: '网络故障' },
  { text: '其他', value: '其他' }
]

const onTypeConfirm = ({ selectedOptions }) => {
  form.repairType = selectedOptions[0]?.text
  showTypePicker.value = false
}

const onSubmit = async () => {
  if (!form.roomNumber || !form.repairType || !form.description) {
    showToast('请填写完整信息')
    return
  }
  
  const loading = showLoadingToast({ message: '提交中...', forbidClick: true })
  
  try {
    await submitRepair({
      roomNumber: form.roomNumber,
      repairType: form.repairType,
      description: form.description,
      images: fileList.value.map(f => f.url || f.objectUrl).filter(Boolean).join(','),
      priority: 'NORMAL'
    })
    
    closeToast()
    showToast('提交成功')
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
.repair-page {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>
