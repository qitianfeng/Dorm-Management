<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalBuildings || 0 }}</div>
              <div class="stat-label">宿舍楼总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon size="32"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.checkedInStudents || 0 }}</div>
              <div class="stat-label">入住学生数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon size="32"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingRepairs || 0 }}</div>
              <div class="stat-label">待处理报修</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon size="32"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.occupancyRate || 0 }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>入住率趋势</span>
            </div>
          </template>
          <div ref="occupancyChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>报修类型分布</span>
            </div>
          </template>
          <div ref="repairChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 楼栋入住情况 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>楼栋入住情况</span>
            </div>
          </template>
          <div ref="buildingChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新公告 -->
    <el-card shadow="hover" class="announcement-card">
      <template #header>
        <div class="card-header">
          <span>最新公告</span>
          <el-button type="primary" link @click="$router.push('/announcement')">查看更多</el-button>
        </div>
      </template>
      <el-table :data="announcements" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAnnouncementType(row.type)">{{ getAnnouncementLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getOccupancyTrend, getRepairDistribution, getBuildingOccupancy } from '@/api/statistics'
import dayjs from 'dayjs'

const stats = ref({})
const announcements = ref([])
const occupancyChart = ref(null)
const repairChart = ref(null)
const buildingChart = ref(null)

onMounted(async () => {
  await loadStatistics()
  await loadCharts()
})

const loadStatistics = async () => {
  try {
    const res = await getOverview()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadCharts = async () => {
  // 入住率趋势图
  const occupancyChartInstance = echarts.init(occupancyChart.value)
  const occupancyData = await getOccupancyTrend()
  occupancyChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: occupancyData.data?.dates || [] },
    yAxis: { type: 'value', name: '入住率(%)' },
    series: [{
      name: '入住率',
      type: 'line',
      smooth: true,
      data: occupancyData.data?.rates || [],
      areaStyle: { opacity: 0.3 }
    }]
  })

  // 报修类型分布图
  const repairChartInstance = echarts.init(repairChart.value)
  const repairData = await getRepairDistribution()
  repairChartInstance.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      name: '报修类型',
      type: 'pie',
      radius: '60%',
      data: Object.entries(repairData.data?.data || {}).map(([name, value]) => ({ name, value }))
    }]
  })

  // 楼栋入住情况
  const buildingChartInstance = echarts.init(buildingChart.value)
  const buildingData = await getBuildingOccupancy()
  buildingChartInstance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['总床位', '已入住'] },
    xAxis: { type: 'category', data: buildingData.data?.buildings?.map(b => b.name) || [] },
    yAxis: { type: 'value' },
    series: [
      { name: '总床位', type: 'bar', data: buildingData.data?.buildings?.map(b => b.total) || [] },
      { name: '已入住', type: 'bar', data: buildingData.data?.buildings?.map(b => b.occupied) || [] }
    ]
  })
}

const getAnnouncementType = (type) => {
  const map = { NOTICE: '', NEWS: 'success', POLICY: 'warning', ACTIVITY: 'info' }
  return map[type] || ''
}

const getAnnouncementLabel = (type) => {
  const map = { NOTICE: '通知', NEWS: '新闻', POLICY: '政策', ACTIVITY: '活动' }
  return map[type] || type
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-cards {
    margin-bottom: 20px;
  }

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
    }

    .stat-icon {
      width: 64px;
      height: 64px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 16px;
    }

    .stat-info {
      flex: 1;
    }

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 4px;
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .announcement-card {
    margin-top: 20px;
  }
}
</style>
