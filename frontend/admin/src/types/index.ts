// 统一响应结构
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应
export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 用户信息
export interface User {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  studentId: string
  department: string
  className: string
  userType: 'STUDENT' | 'DORM_MANAGER' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'LOCKED'
  avatar: string
  createTime: string
}

// 宿舍楼
export interface Building {
  id: number
  buildingCode: string
  buildingName: string
  buildingType: string
  totalFloors: number
  totalRooms: number
  totalBeds: number
  availableBeds: number
  location: string
  managerName: string
  managerPhone: string
  status: 'ACTIVE' | 'MAINTENANCE' | 'CLOSED'
  remark: string
  createTime: string
  updateTime: string
}

// 房间
export interface Room {
  id: number
  buildingId: number
  buildingName: string
  roomNumber: string
  floor: number
  capacity: number
  occupiedBeds: number
  availableBeds: number
  roomType: 'FOUR_PERSON' | 'SIX_PERSON' | 'EIGHT_PERSON'
  status: 'AVAILABLE' | 'PARTIAL' | 'FULL' | 'MAINTENANCE'
  facilities: string
  monthlyFee: number
  remark: string
  createTime: string
  updateTime: string
}

// 住宿记录
export interface Accommodation {
  id: number
  studentId: number
  studentName: string
  studentIdNumber: string
  roomId: number
  roomNumber: string
  buildingId: number
  buildingName: string
  bedNumber: string
  checkInDate: string
  expectedCheckOutDate: string
  actualCheckOutDate: string
  status: 'ACTIVE' | 'TRANSFERRED' | 'CHECKED_OUT' | 'SUSPENDED'
  deposit: number
  remark: string
  createTime: string
  updateTime: string
}

// 报修记录
export interface Repair {
  id: number
  studentId: number
  studentName: string
  roomId: number
  roomNumber: string
  buildingName: string
  repairType: string
  description: string
  images: string
  priority: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT'
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED'
  handlerId: number
  handlerName: string
  handleResult: string
  handleTime: string
  repairCost: number
  createTime: string
  updateTime: string
}

// 公告
export interface Announcement {
  id: number
  title: string
  content: string
  type: 'NOTICE' | 'NEWS' | 'POLICY' | 'ACTIVITY'
  priority: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT'
  status: 'DRAFT' | 'PUBLISHED' | 'EXPIRED'
  publishTime: string
  expireTime: string
  targetAudience: string
  attachments: string
  publisherId: number
  publisherName: string
  viewCount: number
  isTop: boolean
  createTime: string
  updateTime: string
}

// 统计数据
export interface Statistics {
  totalBuildings: number
  totalRooms: number
  totalBeds: number
  availableBeds: number
  occupancyRate: number
  totalStudents: number
  checkedInStudents: number
  totalRepairs: number
  pendingRepairs: number
  processingRepairs: number
  completedRepairs: number
  totalInspections: number
  excellentCount: number
  goodCount: number
  qualifiedCount: number
  unqualifiedCount: number
  totalFees: number
  paidFees: number
  unpaidFees: number
  totalVisitors: number
  visitingCount: number
  totalViolations: number
}
