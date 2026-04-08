<template>
  <div class="test-permission">
    <h2>权限测试页面</h2>
    
    <div class="section">
      <h3>当前用户权限</h3>
      <div>用户: {{ userInfo.username }}</div>
      <div>角色: {{ userInfo.roles }}</div>
      <div>权限: {{ userInfo.permissions }}</div>
    </div>

    <div class="section">
      <h3>v-permission 指令测试</h3>
      
      <!-- 系统管理权限（管理员有，学生没有） -->
      <div v-permission="'system:user'" class="permission-item success">
        ✓ 你可以看到这个（需要 system:user 权限）
      </div>
      
      <!-- 仪表盘权限（所有角色都有） -->
      <div v-permission="'dashboard'" class="permission-item success">
        ✓ 你可以看到这个（需要 dashboard 权限）
      </div>
      
      <!-- 不存在的权限 -->
      <div v-permission="'nonexistent:permission'" class="permission-item fail">
        ✗ 你不应该看到这个（需要不存在的权限）
      </div>
    </div>

    <div class="section">
      <h3>v-role 指令测试</h3>
      
      <!-- 管理员专属 -->
      <div v-role="'ADMIN'" class="role-item success">
        ✓ 管理员专属内容（只有ADMIN角色可见）
      </div>
      
      <!-- 学生专属 -->
      <div v-role="'STUDENT'" class="role-item success">
        ✓ 学生专属内容（只有STUDENT角色可见）
      </div>
    </div>

    <div class="section">
      <h3>checkPermission 函数测试</h3>
      <div>
        hasPermission('system:user'): {{ hasPermission('system:user') }}
      </div>
      <div>
        hasPermission('dashboard'): {{ hasPermission('dashboard') }}
      </div>
      <div>
        hasRole('ADMIN'): {{ hasRole('ADMIN') }}
      </div>
      <div>
        hasRole('STUDENT'): {{ hasRole('STUDENT') }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { checkPermission, checkRole } from '@/directives/permission'

const userInfo = computed(() => {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : {}
})

const hasPermission = (permission) => checkPermission(permission)
const hasRole = (role) => checkRole(role)
</script>

<style scoped>
.test-permission {
  padding: 20px;
}

.section {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.permission-item,
.role-item {
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
}

.success {
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}

.fail {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}
</style>
