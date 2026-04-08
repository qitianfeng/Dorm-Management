<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="评级">
          <el-select v-model="searchForm.grade" placeholder="请选择" clearable>
            <el-option label="优秀" value="EXCELLENT" />
            <el-option label="良好" value="GOOD" />
            <el-option label="合格" value="QUALIFIED" />
            <el-option label="不合格" value="UNQUALIFIED" />
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
        <el-button type="primary" @click="handleAdd">新增检查记录</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="buildingName" label="楼栋" width="120" />
        <el-table-column prop="inspectionDate" label="检查日期" width="120" />
        <el-table-column prop="score" label="评分" width="80">
          <template #default="{ row }">
            <el-tag :type="getScoreColor(row.score)">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="评级" width="100">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.grade)">{{ getGradeLabel(row.grade) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inspectorName" label="检查人" width="100" />
        <el-table-column prop="problems" label="存在问题" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="房间" prop="roomId">
          <el-cascader
            v-model="roomCascader"
            :options="buildingOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择楼栋和房间"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="检查日期" prop="inspectionDate">
          <el-date-picker v-model="form.inspectionDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-slider v-model="form.score" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="存在问题">
          <el-input v-model="form.problems" type="textarea" :rows="3" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingList } from '@/api/building'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增检查记录')
const tableData = ref([])
const buildings = ref([])
const formRef = ref(null)
const roomCascader = ref([])

const searchForm = reactive({ buildingId: null, grade: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  roomId: null,
  buildingId: null,
  inspectionDate: '',
  score: 85,
  problems: '',
  inspectorId: null,
  inspectorName: '',
  remark: ''
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  inspectionDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  score: [{ required: true, message: '请输入评分', trigger: 'change' }]
}

const buildingOptions = computed(() => {
  return buildings.value.map(b => ({
    id: b.id,
    name: b.buildingName,
    children: []
  }))
})

onMounted(() => {
  loadBuildings()
  loadData()
})

const loadBuildings = async () => {
  try {
    const res = await getBuildingList()
    buildings.value = res.data
  } catch (error) {
    console.error('加载楼栋失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/hygiene/page',
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
  Object.assign(searchForm, { buildingId: null, grade: '' })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增检查记录'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑检查记录'
  Object.assign(form, row)
  roomCascader.value = [row.buildingId, row.roomId]
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该检查记录吗？', '提示', { type: 'warning' }).then(async () => {
    await request({ url: `/hygiene/${row.id}`, method: 'delete' })
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleRoomChange = (value) => {
  if (value && value.length === 2) {
    form.roomId = value[1]
    form.buildingId = value[0]
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        form.inspectorId = userInfo.id
        form.inspectorName = userInfo.realName
        
        if (form.id) {
          await request({ url: `/hygiene/${form.id}`, method: 'put', data: form })
          ElMessage.success('更新成功')
        } else {
          await request({ url: '/hygiene', method: 'post', data: form })
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

const resetForm = () => {
  Object.assign(form, {
    id: null,
    roomId: null,
    buildingId: null,
    inspectionDate: '',
    score: 85,
    problems: '',
    inspectorId: null,
    inspectorName: '',
    remark: ''
  })
  roomCascader.value = []
}

const getScoreColor = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 60) return 'warning'
  return 'danger'
}

const getGradeType = (grade) => {
  const map = { EXCELLENT: 'success', GOOD: '', QUALIFIED: 'warning', UNQUALIFIED: 'danger' }
  return map[grade] || ''
}

const getGradeLabel = (grade) => {
  const map = { EXCELLENT: '优秀', GOOD: '良好', QUALIFIED: '合格', UNQUALIFIED: '不合格' }
  return map[grade] || grade
}
</script>

<style scoped lang="scss">
.table-header { margin-bottom: 16px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
