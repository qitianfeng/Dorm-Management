<template>
  <div class="permission-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新权限
          </el-button>
        </div>
      </template>

      <el-table :data="permissionList" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="权限名称" />
        <el-table-column prop="code" label="权限标识" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form :model="permissionForm" :rules="rules" ref="permissionFormRef" label-width="100px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="permissionForm.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="code">
          <el-input v-model="permissionForm.code" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="permissionForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="父权限" prop="parentId">
          <el-tree-select
            v-model="permissionForm.parentId"
            :data="permissionTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择父权限"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路径" prop="path" v-if="permissionForm.type === 'MENU'">
          <el-input v-model="permissionForm.path" placeholder="请输入菜单路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="permissionForm.type === 'MENU'">
          <el-input v-model="permissionForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="permissionForm.type === 'MENU'">
          <el-input v-model="permissionForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="permissionForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio value="ACTIVE">启用</el-radio>
            <el-radio value="INACTIVE">禁用</el-radio>
          </el-radio-group>
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
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/permission'
import { hasPermission } from '@/utils/auth'

const loading = ref(false)
const permissionList = ref([])
const permissionTree = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const permissionFormRef = ref(null)

const permissionForm = reactive({
  id: null,
  name: '',
  code: '',
  type: 'MENU',
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 'ACTIVE'
})

const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

onMounted(() => {
  if (!hasPermission('system:permission')) {
    ElMessage.warning('您没有权限管理权限')
    return
  }
  fetchPermissionList()
})

const fetchPermissionList = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    if (res.code === 200) {
      permissionTree.value = res.data
      // 扁平化权限树用于表格显示
      permissionList.value = flattenPermissionTree(res.data)
    }
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

const flattenPermissionTree = (tree, result = []) => {
  tree.forEach(node => {
    result.push(node)
    if (node.children && node.children.length > 0) {
      flattenPermissionTree(node.children, result)
    }
  })
  return result
}

const getTypeName = (type) => {
  const typeMap = {
    MENU: '菜单',
    BUTTON: '按钮',
    API: '接口'
  }
  return typeMap[type] || type
}

const getTypeTag = (type) => {
  const tagMap = {
    MENU: '',
    BUTTON: 'warning',
    API: 'info'
  }
  return tagMap[type] || ''
}

const handleAdd = () => {
  dialogTitle.value = '新增权限'
  permissionForm.id = null
  permissionForm.name = ''
  permissionForm.code = ''
  permissionForm.type = 'MENU'
  permissionForm.parentId = 0
  permissionForm.path = ''
  permissionForm.component = ''
  permissionForm.icon = ''
  permissionForm.sort = 0
  permissionForm.status = 'ACTIVE'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑权限'
  permissionForm.id = row.id
  permissionForm.name = row.name
  permissionForm.code = row.code
  permissionForm.type = row.type
  permissionForm.parentId = row.parentId
  permissionForm.path = row.path || ''
  permissionForm.component = row.component || ''
  permissionForm.icon = row.icon || ''
  permissionForm.sort = row.sort || 0
  permissionForm.status = row.status || 'ACTIVE'
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePermission(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchPermissionList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除权限失败:', error)
      ElMessage.error('删除权限失败')
    }
  })
}

const handleSubmit = async () => {
  if (!permissionFormRef.value) return
  
  await permissionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (permissionForm.id) {
          res = await updatePermission(permissionForm.id, permissionForm)
        } else {
          res = await createPermission(permissionForm)
        }
        
        if (res.code === 200) {
          ElMessage.success(permissionForm.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          fetchPermissionList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDialogClose = () => {
  dialogVisible.value = false
  permissionFormRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.permission-container{
  padding: 20px;
}

.box-card{
  border-radius: 8px;
}

.card-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title{
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
</style>
