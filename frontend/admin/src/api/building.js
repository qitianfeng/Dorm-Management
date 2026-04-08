import request from '@/utils/request'

// 获取宿舍楼列表
export function getBuildingList() {
  return request({
    url: '/building/list',
    method: 'get'
  })
}

// 分页查询宿舍楼
export function getBuildingPage(params) {
  return request({
    url: '/building/page',
    method: 'get',
    params
  })
}

// 获取宿舍楼详情
export function getBuildingById(id) {
  return request({
    url: `/building/${id}`,
    method: 'get'
  })
}

// 创建宿舍楼
export function createBuilding(data) {
  return request({
    url: '/building',
    method: 'post',
    data
  })
}

// 更新宿舍楼
export function updateBuilding(id, data) {
  return request({
    url: `/building/${id}`,
    method: 'put',
    data
  })
}

// 删除宿舍楼
export function deleteBuilding(id) {
  return request({
    url: `/building/${id}`,
    method: 'delete'
  })
}

// 获取房间列表（根据楼栋ID）
export function getRoomList(buildingId) {
  return request({
    url: `/building/${buildingId}/rooms`,
    method: 'get'
  })
}

// 分页查询房间
export function getRoomPage(params) {
  return request({
    url: '/building/room/page',
    method: 'get',
    params
  })
}

// 获取房间详情
export function getRoomById(id) {
  return request({
    url: `/building/room/${id}`,
    method: 'get'
  })
}

// 创建房间
export function createRoom(data) {
  return request({
    url: '/building/room',
    method: 'post',
    data
  })
}

// 更新房间
export function updateRoom(id, data) {
  return request({
    url: `/building/room/${id}`,
    method: 'put',
    data
  })
}

// 删除房间
export function deleteRoom(id) {
  return request({
    url: `/building/room/${id}`,
    method: 'delete'
  })
}
