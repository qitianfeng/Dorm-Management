import request from '@/utils/request'

// 获取所有选宿舍配置
export function getDormSelectionConfigs() {
  return request({
    url: '/dorm-selection/config/list',
    method: 'get'
  })
}

// 获取配置详情
export function getDormSelectionConfig(id) {
  return request({
    url: `/dorm-selection/config/${id}`,
    method: 'get'
  })
}

// 创建配置
export function createDormSelectionConfig(data) {
  return request({
    url: '/dorm-selection/config',
    method: 'post',
    data
  })
}

// 更新配置
export function updateDormSelectionConfig(id, data) {
  return request({
    url: `/dorm-selection/config/${id}`,
    method: 'put',
    data
  })
}

// 删除配置
export function deleteDormSelectionConfig(id) {
  return request({
    url: `/dorm-selection/config/${id}`,
    method: 'delete'
  })
}

// 启用配置
export function activateDormSelectionConfig(id) {
  return request({
    url: `/dorm-selection/config/${id}/activate`,
    method: 'put'
  })
}

// 停用配置
export function deactivateDormSelectionConfig(id) {
  return request({
    url: `/dorm-selection/config/${id}/deactivate`,
    method: 'put'
  })
}
