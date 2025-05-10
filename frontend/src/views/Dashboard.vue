<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>RSS源总数</span>
              <el-icon><Link /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalSources }}</div>
          <div class="stat-trend">
            较上月
            <span :class="stats.sourceTrend >= 0 ? 'up' : 'down'">
              {{ Math.abs(stats.sourceTrend) }}%
              <el-icon>
                <component :is="stats.sourceTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>今日抓取数</span>
              <el-icon><Download /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.todayFetches }}</div>
          <div class="stat-trend">
            较昨日
            <span :class="stats.fetchTrend >= 0 ? 'up' : 'down'">
              {{ Math.abs(stats.fetchTrend) }}%
              <el-icon>
                <component :is="stats.fetchTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>内容分析数</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalAnalysis }}</div>
          <div class="stat-trend">
            较上月
            <span :class="stats.analysisTrend >= 0 ? 'up' : 'down'">
              {{ Math.abs(stats.analysisTrend) }}%
              <el-icon>
                <component :is="stats.analysisTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>格式化器数</span>
              <el-icon><Tools /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalFormatters }}</div>
          <div class="stat-trend">
            较上月
            <span :class="stats.formatterTrend >= 0 ? 'up' : 'down'">
              {{ Math.abs(stats.formatterTrend) }}%
              <el-icon>
                <component :is="stats.formatterTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>抓取趋势</span>
              <el-radio-group v-model="fetchChartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="fetchChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>内容分析趋势</span>
              <el-radio-group v-model="analysisChartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="analysisChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近记录 -->
    <el-card class="recent-records">
      <template #header>
        <div class="card-header">
          <span>最近抓取记录</span>
          <el-button type="primary" link @click="router.push('/sources')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentFetches" style="width: 100%">
        <el-table-column prop="sourceName" label="RSS源" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fetchTime" label="抓取时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import {
  Link,
  Download,
  Document,
  Tools,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()
const dashboardStore = useDashboardStore()
const fetchChartRef = ref(null)
const analysisChartRef = ref(null)
const fetchChartPeriod = ref('week')
const analysisChartPeriod = ref('week')

const stats = ref({
  totalSources: 0,
  sourceTrend: 0,
  todayFetches: 0,
  fetchTrend: 0,
  totalAnalysis: 0,
  analysisTrend: 0,
  totalFormatters: 0,
  formatterTrend: 0
})

const recentFetches = ref([])

let fetchChart = null
let analysisChart = null

const initCharts = () => {
  if (fetchChartRef.value) {
    fetchChart = echarts.init(fetchChartRef.value)
  }
  if (analysisChartRef.value) {
    analysisChart = echarts.init(analysisChartRef.value)
  }
}

const updateCharts = async () => {
  const fetchData = await dashboardStore.getFetchTrend(fetchChartPeriod.value)
  const analysisData = await dashboardStore.getAnalysisTrend(analysisChartPeriod.value)

  if (fetchChart) {
    fetchChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: fetchData.dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '抓取数',
          type: 'line',
          smooth: true,
          data: fetchData.values,
          areaStyle: {
            opacity: 0.1
          }
        }
      ]
    })
  }

  if (analysisChart) {
    analysisChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: analysisData.dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '分析数',
          type: 'line',
          smooth: true,
          data: analysisData.values,
          areaStyle: {
            opacity: 0.1
          }
        }
      ]
    })
  }
}

const loadData = async () => {
  const [overview, recent] = await Promise.all([
    dashboardStore.getOverview(),
    dashboardStore.getRecentFetches()
  ])
  stats.value = overview
  recentFetches.value = recent
}

watch([fetchChartPeriod, analysisChartPeriod], () => {
  updateCharts()
})

onMounted(async () => {
  await loadData()
  initCharts()
  updateCharts()

  window.addEventListener('resize', () => {
    fetchChart?.resize()
    analysisChart?.resize()
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 160px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-trend {
  font-size: 14px;
  color: #909399;
}

.stat-trend .up {
  color: #67c23a;
}

.stat-trend .down {
  color: #f56c6c;
}

.charts {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart {
  height: 320px;
}

.recent-records {
  margin-bottom: 20px;
}
</style> 