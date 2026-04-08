import request from '@/utils/request'

// 分页查询报修
export function getRepairPage(params) {
  return request({
    url: '/repair/page',
    method: 'get',
    params
  })
}

// 提交报修
export function createRepair(data) {
  return request({
    url: '/repair',
    method: 'post',
    data
  })
}

// 处理报修
export function handleRepair(id, data) {
  return request({
    url: `/repair/${id}/handle`,
    method: 'put',
    data
  })
}

// 取消报修
export function cancelRepair(id) {
  return request({
    url: `/repair/${id}/cancel`,
    method: 'put'
  })
}

// 获取报修详情
export function getRepairById(id) {
  return request({
    url: `/repair/${id}`,
    method: 'get'
  })
}
