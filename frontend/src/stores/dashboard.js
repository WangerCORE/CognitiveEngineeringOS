import { defineStore } from 'pinia'
import { ref } from 'vue'
import { dashboardApi } from '@/api/dashboard'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    overview: null,
    recentFetches: [],
    fetchTrend: null,
    analysisTrend: null
  }),

  actions: {
    async getOverview() {
      try {
        const response = await dashboardApi.getOverview()
        this.overview = response.data
        return response.data
      } catch (error) {
        console.error('获取概览数据失败:', error)
        throw error
      }
    },

    async getRecentFetches() {
      try {
        const response = await dashboardApi.getRecentFetches()
        this.recentFetches = response.data
        return response.data
      } catch (error) {
        console.error('获取最近抓取记录失败:', error)
        throw error
      }
    },

    async getFetchTrend(period) {
      try {
        const response = await dashboardApi.getFetchTrend(period)
        this.fetchTrend = response.data
        return response.data
      } catch (error) {
        console.error('获取抓取趋势失败:', error)
        throw error
      }
    },

    async getAnalysisTrend(period) {
      try {
        const response = await dashboardApi.getAnalysisTrend(period)
        this.analysisTrend = response.data
        return response.data
      } catch (error) {
        console.error('获取分析趋势失败:', error)
        throw error
      }
    }
  }
}) 