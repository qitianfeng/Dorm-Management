import request from '@/utils/request'

// 获取换宿舍申请列表（管理端）
export function getRoomTransferPage(params) {
  return request({
    url: '/room-transfer/page',
    method: 'get',
    params
  })
}

// 获取换宿舍申请详情
export function getRoomTransferDetail(id) {
  return request({
    url: `/room-transfer/${id}`,
    method: 'get'
  })
}

// 审批换宿舍申请
export function approveRoomTransfer(data) {
  return request({
    url: '/room-transfer/approve',
    method: 'post',
    data
  })
}
