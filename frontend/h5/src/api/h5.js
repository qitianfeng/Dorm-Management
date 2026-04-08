import request from '@/utils/request'

// 获取当前学生信息（含住宿信息）
export function getStudentInfo() {
  return request({
    url: '/h5/student/info',
    method: 'get'
  })
}

// 获取已发布公告
export function getAnnouncements() {
  return request({
    url: '/h5/announcements',
    method: 'get'
  })
}

// 提交报修申请
export function submitRepair(data) {
  return request({
    url: '/h5/repair',
    method: 'post',
    data
  })
}

// 获取我的报修记录
export function getMyRepairs() {
  return request({
    url: '/h5/repairs',
    method: 'get'
  })
}

// ==================== 选宿舍 ====================

// 获取当前选宿舍配置
export function getDormSelectionConfig() {
  return request({
    url: '/h5/dorm-selection/config',
    method: 'get'
  })
}

// 获取可选房间列表
export function getAvailableRooms(buildingId) {
  return request({
    url: '/h5/dorm-selection/rooms',
    method: 'get',
    params: { buildingId }
  })
}

// 学生选择宿舍
export function selectRoom(data) {
  return request({
    url: '/h5/dorm-selection/select',
    method: 'post',
    data
  })
}

// 获取我的选宿舍记录
export function getMySelection() {
  return request({
    url: '/h5/dorm-selection/my',
    method: 'get'
  })
}

// 取消选宿舍
export function cancelSelection() {
  return request({
    url: '/h5/dorm-selection/cancel',
    method: 'delete'
  })
}

// 自动分配宿舍
export function autoAllocate() {
  return request({
    url: '/h5/dorm-selection/auto-allocate',
    method: 'post'
  })
}

// ==================== 退宿 ====================

// 申请退宿
export function applyCheckOut() {
  return request({
    url: '/h5/check-out',
    method: 'post'
  })
}

// ==================== 换宿舍 ====================

// 申请换宿舍
export function applyRoomTransfer(data) {
  return request({
    url: '/room-transfer/apply',
    method: 'post',
    data
  })
}

// 取消换宿舍申请
export function cancelRoomTransfer(id) {
  return request({
    url: `/room-transfer/cancel/${id}`,
    method: 'delete'
  })
}

// 获取我的换宿舍申请
export function getMyRoomTransfer() {
  return request({
    url: '/room-transfer/my',
    method: 'get'
  })
}

// 获取我的换宿舍申请历史
export function getMyRoomTransferHistory() {
  return request({
    url: '/room-transfer/my/history',
    method: 'get'
  })
}

// 获取可选房间列表（换宿舍用）
export function getAvailableRoomsForTransfer(buildingId) {
  return request({
    url: '/h5/dorm-selection/rooms',
    method: 'get',
    params: { buildingId }
  })
}
