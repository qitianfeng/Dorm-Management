<template>
  <div class="page-container">
    <div class="search-form">
      <el-button type="primary" @click="handleAdd">新增配置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configName" label="配置名称" min-width="150" />
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column label="选宿舍时间" min-width="200">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="性别限制" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.genderRestriction === 'ALL'" type="success">不限</el-tag>
            <el-tag v-else-if="row.genderRestriction === 'MALE'" type="primary">男生</el-tag>
            <el-tag v-else-if="row.genderRestriction === 'FEMALE'" type="danger">女生</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="年级限制" min-width="120">
          <template #default="{ row }">
            {{ row.allowedGrades || '不限' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">进行中</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="info">未开始</el-tag>
            <el-tag v-else type="warning">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 'ACTIVE'" type="success" link @click="handleActivate(row)">启用</el-button>
            <el-button v-else type="warning" link @click="handleDeactivate(row)">停用</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学年" prop="academicYear">
              <el-input v-model="form.academicYear" placeholder="如：2024-2025" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="form.semester" placeholder="请选择" style="width: 100%;">
                <el-option label="第一学期" value="第一学期" />
                <el-option label="第二学期" value="第二学期" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="性别限制" prop="genderRestriction">
          <el-radio-group v-model="form.genderRestriction">
            <el-radio label="ALL">不限</el-radio>
            <el-radio label="MALE">男生</el-radio>
            <el-radio label="FEMALE">女生</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="年级限制" prop="allowedGrades">
          <el-input v-model="form.allowedGrades" placeholder="如：2024,2023（多个用逗号分隔）" />
        </el-form-item>
        
        <el-form-item label="学院限制" prop="allowedColleges">
          <el-input v-model="form.allowedColleges" type="textarea" :rows="2" placeholder="如：计算机学院,外国语学院（多个用逗号分隔）" />
        </el-form-item>
        
        <el-form-item label="可选楼栋" prop="buildingIds">
          <el-input v-model="form.buildingIds" placeholder="如：1,2,3（多个用逗号分隔）" />
        </el-form-item>
        
        <el-form-item label="描述说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDormSelectionConfigs, createDormSelectionConfig, updateDormSelectionConfig, deleteDormSelectionConfig, activateDormSelectionConfig, deactivateDormSelectionConfig } from '@/api/dormSelection'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增配置')
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  id: null,
  configName: '',
  startTime: '',
  endTime: '',
  academicYear: '',
  semester: '',
  genderRestriction: 'ALL',
  allowedGrades: '',
  allowedColleges: '',
  buildingIds: '',
  description: ''
})

const rules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDormSelectionConfigs()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    configName: '',
    startTime: '',
    endTime: '',
    academicYear: '',
    semester: '',
    genderRestriction: 'ALL',
    allowedGrades: '',
    allowedColleges: '',
    buildingIds: '',
    description: ''
  })
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogTitle.value = '新增配置'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑配置'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (isEdit.value) {
      await updateDormSelectionConfig(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createDormSelectionConfig(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该配置吗？', '提示', { type: 'warning' })
    await deleteDormSelectionConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleActivate = async (row) => {
  try {
    await activateDormSelectionConfig(row.id)
    ElMessage.success('启用成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '启用失败')
  }
}

const handleDeactivate = async (row) => {
  try {
    await deactivateDormSelectionConfig(row.id)
    ElMessage.success('停用成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '停用失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
</style>
