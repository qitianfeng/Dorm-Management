<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon size="24"><HomeFilled /></el-icon>
        <span v-show="!isCollapse">宿舍管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="menu"
      >
        <template v-for="menu in filteredMenus" :key="menu.path">
          <!-- 有子菜单的情况 -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item 
              v-for="child in menu.children" 
              :key="child.path" 
              :index="child.path"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 没有子菜单的情况 -->
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userInfo?.realName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
    
    <!-- 个人信息对话框 -->
    <el-dialog v-model="profileDialogVisible" title="个人信息" width="500px">
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-tag>{{ getUserTypeText(profileForm.userType) }}</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  Setting, HomeFilled, OfficeBuilding, UserFilled, 
  Tools, DocumentChecked, BellFilled, DataAnalysis,
  Fold, Expand, ArrowDown, Grid, Wallet, WarningFilled, Lock
} from '@element-plus/icons-vue'
import { getUserInfo, getPermissions, removeToken, removeUserInfo, removePermissions } from '@/utils/auth'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const userInfo = ref(getUserInfo())
const permissions = getPermissions()

// 个人信息对话框
const profileDialogVisible = ref(false)
const profileForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  userType: ''
})

// 修改密码对话框
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 图标映射
const iconMap = {
  'HomeFilled': HomeFilled,
  'OfficeBuilding': OfficeBuilding,
  'House': OfficeBuilding,
  'Grid': Grid,
  'UserFilled': UserFilled,
  'Tools': Tools,
  'DocumentChecked': DocumentChecked,
  'BellFilled': BellFilled,
  'DataAnalysis': DataAnalysis,
  'Setting': Setting,
  'Wallet': Wallet,
  'WarningFilled': WarningFilled,
  'Lock': Lock
}

// 所有菜单配置
const allMenus = [
  {
    name: '首页',
    path: '/dashboard',
    icon: 'HomeFilled',
    permission: 'dashboard'
  },
  {
    name: '楼栋管理',
    path: '/building',
    icon: 'OfficeBuilding',
    permission: 'building'
  },
  {
    name: '房间管理',
    path: '/room',
    icon: 'Grid',
    permission: 'room'
  },
  {
    name: '住宿管理',
    path: '/accommodation',
    icon: 'UserFilled',
    permission: 'accommodation'
  },
  {
    name: '选宿舍配置',
    path: '/dorm-selection',
    icon: 'Setting',
    permission: 'selection'
  },
  {
    name: '换宿舍审批',
    path: '/room-transfer',
    icon: 'Setting',
    permission: 'room-transfer'
  },
  {
    name: '报修管理',
    path: '/repair',
    icon: 'Tools',
    permission: 'repair'
  },
  {
    name: '卫生检查',
    path: '/hygiene',
    icon: 'DocumentChecked',
    permission: 'hygiene'
  },
  {
    name: '访客登记',
    path: '/visitor',
    icon: 'UserFilled',
    permission: 'visitor'
  },
  {
    name: '门禁记录',
    path: '/access-record',
    icon: 'Lock',
    permission: 'access-record'
  },
  {
    name: '缴费管理',
    path: '/fee',
    icon: 'Wallet',
    permission: 'fee'
  },
  {
    name: '违纪记录',
    path: '/disciplinary',
    icon: 'WarningFilled',
    permission: 'disciplinary'
  },
  {
    name: '公告管理',
    path: '/announcement',
    icon: 'BellFilled',
    permission: 'announcement'
  },
  {
    name: '统计报表',
    path: '/statistics',
    icon: 'DataAnalysis',
    permission: 'statistics'
  },
  {
    name: '系统管理',
    path: '/system',
    icon: 'Setting',
    permission: 'system',
    children: [
      { name: '用户管理', path: '/system/user', icon: 'UserFilled', permission: 'user' },
      { name: '角色管理', path: '/system/role', icon: 'UserFilled', permission: 'role' },
      { name: '权限管理', path: '/system/permission', icon: 'Lock', permission: 'permission' }
    ]
  }
]

// 检查是否有权限
const hasPermission = (permissionCode) => {
  // 管理员拥有所有权限
  if (userInfo.value?.userType === 'ADMIN') return true
  return permissions.includes(permissionCode)
}

// 过滤菜单
const filteredMenus = computed(() => {
  const filterMenu = (menus) => {
    return menus.filter(menu => {
      if (!hasPermission(menu.permission)) return false
      
      if (menu.children && menu.children.length > 0) {
        menu.children = filterMenu(menu.children)
        return menu.children.length > 0
      }
      return true
    }).map(menu => ({
      ...menu,
      icon: iconMap[menu.icon] || Setting
    }))
  }
  
  return filterMenu(JSON.parse(JSON.stringify(allMenus)))
})

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta.title)
})

const getUserTypeText = (type) => {
  const map = { ADMIN: '管理员', DORM_MANAGER: '宿管', STUDENT: '学生' }
  return map[type] || type
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      // 打开个人信息对话框
      Object.assign(profileForm, {
        id: userInfo.value.id,
        username: userInfo.value.username,
        realName: userInfo.value.realName || '',
        phone: userInfo.value.phone || '',
        email: userInfo.value.email || '',
        userType: userInfo.value.userType
      })
      profileDialogVisible.value = true
      break
    case 'password':
      // 打开修改密码对话框
      Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
      passwordDialogVisible.value = true
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeToken()
        removeUserInfo()
        removePermissions()
        router.push('/login')
      })
      break
  }
}

const handleSaveProfile = async () => {
  try {
    await request.put(`/user/${profileForm.id}`, {
      realName: profileForm.realName,
      phone: profileForm.phone,
      email: profileForm.email
    })
    ElMessage.success('保存成功')
    // 更新本地存储的用户信息
    const updatedUserInfo = { ...userInfo.value, ...profileForm }
    localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
    userInfo.value = updatedUserInfo
    profileDialogVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    // 清除登录状态，跳转到登录页
    removeToken()
    removeUserInfo()
    removePermissions()
    router.push('/login')
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.response?.data?.message || '密码修改失败')
    }
  }
}
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
}

.aside {
  background: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5b;
  
  .el-icon {
    margin-right: 8px;
  }
}

.menu {
  border-right: none;
  background: #304156;
  
  &:not(.el-menu--collapse) {
    width: 220px;
  }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  
  .collapse-btn {
    font-size: 20px;
    cursor: pointer;
    margin-right: 16px;
    
    &:hover {
      color: #409eff;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .username {
    margin: 0 8px;
  }
}

.main {
  background: #f5f7fa;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
