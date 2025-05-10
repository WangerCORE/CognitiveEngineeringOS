import api from './config'

export const dashboardApi = {
  // 获取仪表盘概览数据
  getOverview() {
    return api.get('/dashboard/overview')
  },

  // 获取抓取趋势数据
  getFetchTrends(params) {
    return api.get('/dashboard/fetch-trends', { params })
  },

  // 获取内容分析趋势数据
  getAnalysisTrends(params) {
    return api.get('/dashboard/analysis-trends', { params })
  },

  // 获取最近抓取记录
  getRecentFetches(params) {
    return api.get('/dashboard/recent-fetches', { params })
  },

  // 获取系统状态
  getSystemStatus() {
    return api.get('/dashboard/system-status')
  }
} 