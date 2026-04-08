import request from '@/utils/request'

// 获取总体统计
export function getOverview() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

// 获取入住率趋势
export function getOccupancyTrend() {
  return request({
    url: '/statistics/occupancy-trend',
    method: 'get'
  })
}

// 获取报修类型分布
export function getRepairDistribution() {
  return request({
    url: '/statistics/repair-distribution',
    method: 'get'
  })
}

// 获取卫生评分趋势
export function getHygieneTrend() {
  return request({
    url: '/statistics/hygiene-trend',
    method: 'get'
  })
}

// 获取楼栋入住情况
export function getBuildingOccupancy() {
  return request({
    url: '/statistics/building-occupancy',
    method: 'get'
  })
}
