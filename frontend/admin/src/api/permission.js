import request from '@/utils/request'

// 获取当前用户菜单
export function getUserMenus() {
  return request({
    url: '/permission/menus',
    method: 'get'
  })
}

// 获取所有权限树
export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

// 获取角色权限
export function getRolePermissions(roleId) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

// 分配角色权限
export function assignRolePermissions(roleId, permissionIds) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'post',
    data: permissionIds
  })
}

// 获取权限详情
export function getPermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}
