<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalBuildings }}</div>
              <div class="stat-label">宿舍楼总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">入住学生数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingRepairs }}</div>
              <div class="stat-label">待处理报修</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ (stats.occupancyRate * 100).toFixed(1) }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>入住率统计</span>
            </div>
          </template>
          <div ref="occupancyChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>报修类型分布</span>
            </div>
          </template>
          <div ref="repairChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>卫生检查统计</span>
            </div>
          </template>
          <div ref="hygieneChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>缴费统计</span>
            </div>
          </template>
          <div ref="feeChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card shadow="hover" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>宿舍楼入住情况</span>
        </div>
      </template>
      <el-table :data="buildingData" border stripe>
        <el-table-column prop="buildingName" label="楼栋名称" />
        <el-table-column prop="buildingType" label="楼栋类型" />
        <el-table-column prop="totalRooms" label="房间数" />
        <el-table-column prop="totalBeds" label="总床位" />
        <el-table-column prop="occupiedBeds" label="已入住" />
        <el-table-column prop="availableBeds" label="空床位" />
        <el-table-column prop="occupancyRate" label="入住率">
          <template #default="{ row }">
            <el-progress :percentage="Number((row.occupancyRate * 100).toFixed(1))" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const stats = reactive({
  totalBuildings: 0,
  totalStudents: 0,
  pendingRepairs: 0,
  occupancyRate: 0,
  totalBeds: 0,
  availableBeds: 0
})

const buildingData = ref([])

const occupancyChart = ref(null)
const repairChart = ref(null)
const hygieneChart = ref(null)
const feeChart = ref(null)

let occupancyChartInstance = null
let repairChartInstance = null
let hygieneChartInstance = null
let feeChartInstance = null

const loadOverview = async () => {
  try {
    const response = await fetch('/api/statistics/overview', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const result = await response.json()
    if (result.code === 200 && result.data) {
      Object.assign(stats, {
        ...result.data,
        totalBuildings: result.data.totalBuildings ?? 0,
        totalStudents: result.data.totalStudents ?? 0,
        pendingRepairs: result.data.pendingRepairs ?? 0,
        occupancyRate: result.data.occupancyRate ?? 0,
        totalBeds: result.data.totalBeds ?? 0,
        availableBeds: result.data.availableBeds ?? 0
      })
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadBuildings = async () => {
  try {
    const response = await fetch('/api/building/list', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const result = await response.json()
    if (result.code === 200 && Array.isArray(result.data)) {
      buildingData.value = result.data.map(item => ({
        ...item,
        occupiedBeds: (item.totalBeds ?? 0) - (item.availableBeds ?? 0),
        occupancyRate: (item.totalBeds ?? 0) > 0 ? (item.availableBeds ?? 0) / (item.totalBeds ?? 1) : 0
      }))
    }
  } catch (error) {
    console.error('加载宿舍楼数据失败', error)
  }
}

const initCharts = () => {
  nextTick(() => {
    // 入住率饼图
    if (occupancyChart.value) {
      occupancyChartInstance = echarts.init(occupancyChart.value)
      const occupiedBeds = (stats.totalBeds || 0) - (stats.availableBeds || 0)
      const emptyBeds = stats.availableBeds || 0
      occupancyChartInstance.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
          data: [
            { value: occupiedBeds, name: '已入住', itemStyle: { color: '#409eff' } },
            { value: emptyBeds, name: '空床位', itemStyle: { color: '#67c23a' } }
          ]
        }]
      })
    }

    // 报修类型柱状图
    if (repairChart.value) {
      repairChartInstance = echarts.init(repairChart.value)
      repairChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: ['水暖', '电路', '门窗', '家具', '其他'] },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: [
            { value: 12, itemStyle: { color: '#409eff' } },
            { value: 8, itemStyle: { color: '#67c23a' } },
            { value: 5, itemStyle: { color: '#e6a23c' } },
            { value: 3, itemStyle: { color: '#f56c6c' } },
            { value: 2, itemStyle: { color: '#909399' } }
          ],
          barWidth: '50%'
        }]
      })
    }

    // 卫生检查折线图
    if (hygieneChart.value) {
      hygieneChartInstance = echarts.init(hygieneChart.value)
      hygieneChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['优秀', '良好', '合格'], bottom: '5%' },
        xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
        yAxis: { type: 'value' },
        series: [
          { name: '优秀', type: 'line', smooth: true, data: [15, 18, 20, 22, 25, 28], itemStyle: { color: '#67c23a' } },
          { name: '良好', type: 'line', smooth: true, data: [30, 32, 35, 38, 40, 42], itemStyle: { color: '#409eff' } },
          { name: '合格', type: 'line', smooth: true, data: [10, 8, 5, 3, 2, 1], itemStyle: { color: '#e6a23c' } }
        ]
      })
    }

    // 缴费统计柱状图
    if (feeChart.value) {
      feeChartInstance = echarts.init(feeChart.value)
      feeChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['已缴', '未缴'], bottom: '5%' },
        xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
        yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
        series: [
          { name: '已缴', type: 'bar', data: [5000, 4800, 5200, 5100, 5300, 5500], itemStyle: { color: '#67c23a' } },
          { name: '未缴', type: 'bar', data: [500, 300, 400, 200, 100, 50], itemStyle: { color: '#f56c6c' } }
        ]
      })
    }
  })
}

onMounted(async () => {
  await loadOverview()
  await loadBuildings()
  initCharts()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: #f5f7fa;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-item {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}
.stat-icon .el-icon {
  font-size: 28px;
  color: #fff;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.charts-row {
  margin-bottom: 20px;
}
.detail-card {
  margin-bottom: 20px;
}
.card-header {
  font-weight: bold;
}
</style>
