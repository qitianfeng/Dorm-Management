import request from '@/utils/request'

// 获取所有角色
export function getRoles() {
  return request({
    url: '/role',
    method: 'get'
  })
}

// 获取角色详情
export function getRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

// 获取用户角色
export function getUserRoles(userId) {
  return request({
    url: `/role/user/${userId}`,
    method: 'get'
  })
}
