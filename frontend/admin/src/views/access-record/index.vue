<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="进出类型">
          <el-select v-model="searchForm.accessType" placeholder="请选择" clearable>
            <el-option label="进入" value="进入" />
            <el-option label="离开" value="离开" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/楼栋/设备位置" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="userName" label="用户姓名" width="100" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="accessType" label="进出类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.accessType === '进入' ? 'success' : 'warning'">
              {{ row.accessType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accessTime" label="进出时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.accessTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="deviceLocation" label="设备位置" show-overflow-tooltip />
        <el-table-column prop="deviceId" label="设备编号" width="120" />
        <el-table-column prop="result" label="识别结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === '成功' ? 'success' : 'danger'">
              {{ row.result || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({ accessType: '', keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/access-record/page',
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
  Object.assign(searchForm, { accessType: '', keyword: '' })
  handleSearch()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该门禁记录吗？', '提示', { type: 'warning' }).then(async () => {
    await request({ url: `/access-record/${row.id}`, method: 'delete' })
    ElMessage.success('删除成功')
    loadData()
  })
}

const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}
</script>

<style scoped lang="scss">
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
