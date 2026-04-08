import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', permission: 'dashboard' }
      },
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/building/index.vue'),
        meta: { title: '宿舍楼管理', icon: 'OfficeBuilding', permission: 'dorm:building' }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/room/index.vue'),
        meta: { title: '房间管理', icon: 'House', permission: 'dorm:room' }
      },
      {
        path: 'accommodation',
        name: 'Accommodation',
        component: () => import('@/views/accommodation/index.vue'),
        meta: { title: '住宿管理', icon: 'UserFilled', permission: 'dorm:accommodation' }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/repair/index.vue'),
        meta: { title: '报修管理', icon: 'Tools', permission: 'repair' }
      },
      {
        path: 'hygiene',
        name: 'Hygiene',
        component: () => import('@/views/hygiene/index.vue'),
        meta: { title: '卫生检查', icon: 'DocumentChecked', permission: 'hygiene' }
      },
      {
        path: 'visitor',
        name: 'Visitor',
        component: () => import('@/views/visitor/index.vue'),
        meta: { title: '访客登记', icon: 'Avatar', permission: 'visitor' }
      },
      {
        path: 'fee',
        name: 'Fee',
        component: () => import('@/views/fee/index.vue'),
        meta: { title: '缴费管理', icon: 'Wallet', permission: 'fee' }
      },
      {
        path: 'disciplinary',
        name: 'Disciplinary',
        component: () => import('@/views/disciplinary/index.vue'),
        meta: { title: '违纪记录', icon: 'WarningFilled', permission: 'disciplinary' }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/announcement/index.vue'),
        meta: { title: '公告管理', icon: 'BellFilled', permission: 'announcement' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '统计报表', icon: 'DataAnalysis', permission: 'statistics' }
      },
      {
        path: 'dorm-selection',
        name: 'DormSelection',
        component: () => import('@/views/dorm-selection/index.vue'),
        meta: { title: '选宿舍配置', icon: 'Setting', permission: 'dorm:selection' }
      },
      {
        path: 'room-transfer',
        name: 'RoomTransfer',
        component: () => import('@/views/room-transfer/index.vue'),
        meta: { title: '换宿舍审批', icon: 'Sort', permission: 'dorm:room-transfer' }
      },
      {
        path: 'access-record',
        name: 'AccessRecord',
        component: () => import('@/views/access-record/index.vue'),
        meta: { title: '门禁记录', icon: 'Lock', permission: 'access-record' }
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/role',
        meta: { title: '系统管理', icon: 'Setting', permission: 'system' },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/system/user.vue'),
            meta: { title: '用户管理', icon: 'User', permission: 'system:user' }
          },
          {
            path: 'role',
            name: 'Role',
            component: () => import('@/views/system/role.vue'),
            meta: { title: '角色管理', icon: 'Avatar', permission: 'system:role' }
          },
          {
            path: 'permission',
            name: 'Permission',
            component: () => import('@/views/system/permission.vue'),
            meta: { title: '权限管理', icon: 'Lock', permission: 'system:permission' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 宿舍管理系统` : '宿舍管理系统'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth !== false) {
    const token = getToken()
    if (!token) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router
