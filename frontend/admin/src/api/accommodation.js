import request from '@/utils/request'

// 分页查询住宿记录
export function getAccommodationPage(params) {
  return request({
    url: '/accommodation/page',
    method: 'get',
    params
  })
}

// 学生入住
export function checkIn(data) {
  return request({
    url: '/accommodation/check-in',
    method: 'post',
    data
  })
}

// 学生调宿
export function transfer(data) {
  return request({
    url: '/accommodation/transfer',
    method: 'post',
    data
  })
}

// 学生退宿
export function checkOut(data) {
  return request({
    url: '/accommodation/check-out',
    method: 'post',
    data
  })
}

// 获取空床位列表
export function getAvailableBeds(buildingId) {
  return request({
    url: '/accommodation/available-beds',
    method: 'get',
    params: { buildingId }
  })
}

// 获取房间入住学生
export function getRoomAccommodations(roomId) {
  return request({
    url: `/accommodation/room/${roomId}`,
    method: 'get'
  })
}
