import { getPermissions } from '@/utils/auth'

/**
 * 权限指令
 * 使用方式：v-permission="'system:user'" 或 v-permission="['system:user', 'system:role']"
 */
export const permission = {
  mounted(el, binding) {
    const permissions = getPermissions()
    const requiredPermissions = binding.value
    
    if (!requiredPermissions) {
      return
    }
    
    let hasPermission = false
    
    if (Array.isArray(requiredPermissions)) {
      // 数组形式：有任一权限即可
      hasPermission = requiredPermissions.some(p => permissions.includes(p))
    } else if (typeof requiredPermissions === 'string') {
      // 字符串形式
      hasPermission = permissions.includes(requiredPermissions)
    }
    
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 角色指令
 * 使用方式：v-role="'ADMIN'" 或 v-role="['ADMIN', 'DORM_MANAGER']"
 */
export const role = {
  mounted(el, binding) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userRoles = userInfo.roles || []
    const requiredRoles = binding.value
    
    if (!requiredRoles) {
      return
    }
    
    let hasRole = false
    
    if (Array.isArray(requiredRoles)) {
      hasRole = requiredRoles.some(r => userRoles.includes(r))
    } else if (typeof requiredRoles === 'string') {
      hasRole = userRoles.includes(requiredRoles)
    }
    
    if (!hasRole) {
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 权限检查函数
 */
export function checkPermission(permission) {
  const permissions = getPermissions()
  if (Array.isArray(permission)) {
    return permission.some(p => permissions.includes(p))
  }
  return permissions.includes(permission)
}

/**
 * 角色检查函数
 */
export function checkRole(role) {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userRoles = userInfo.roles || []
  if (Array.isArray(role)) {
    return role.some(r => userRoles.includes(r))
  }
  return userRoles.includes(role)
}

export default {
  install(app) {
    app.directive('permission', permission)
    app.directive('role', role)
    
    // 全局属性
    app.config.globalProperties.$hasPermission = checkPermission
    app.config.globalProperties.$hasRole = checkRole
  }
}
