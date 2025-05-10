import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useReportStore = defineStore('report', () => {
  const loading = ref(false)
  const reports = ref([])
  const templates = ref([])
  const currentReport = ref(null)

  // 获取报告列表
  const getReports = async (params) => {
    try {
      const response = await axios.get('/api/reports', { params })
      reports.value = response.data.data
      return reports.value
    } catch (error) {
      throw error
    }
  }

  // 获取单个报告
  const getReport = async (id) => {
    try {
      const response = await axios.get(`/api/reports/${id}`)
      currentReport.value = response.data.data
      return currentReport.value
    } catch (error) {
      throw error
    }
  }

  // 创建报告
  const createReport = async (data) => {
    try {
      const response = await axios.post('/api/reports', data)
      reports.value.unshift(response.data.data)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 删除报告
  const deleteReport = async (id) => {
    try {
      await axios.delete(`/api/reports/${id}`)
      reports.value = reports.value.filter(report => report.id !== id)
    } catch (error) {
      throw error
    }
  }

  // 获取报告模板列表
  const getTemplates = async () => {
    try {
      const response = await axios.get('/api/reports/templates')
      templates.value = response.data.data
      return templates.value
    } catch (error) {
      throw error
    }
  }

  // 创建报告模板
  const createTemplate = async (data) => {
    try {
      const response = await axios.post('/api/reports/templates', data)
      templates.value.push(response.data.data)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 更新报告模板
  const updateTemplate = async (id, data) => {
    try {
      const response = await axios.put(`/api/reports/templates/${id}`, data)
      const index = templates.value.findIndex(t => t.id === id)
      if (index !== -1) {
        templates.value[index] = response.data.data
      }
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 删除报告模板
  const deleteTemplate = async (id) => {
    try {
      await axios.delete(`/api/reports/templates/${id}`)
      templates.value = templates.value.filter(template => template.id !== id)
    } catch (error) {
      throw error
    }
  }

  // 下载报告
  const downloadReport = async (id, format = 'markdown') => {
    try {
      const response = await axios.get(`/api/reports/${id}/download`, {
        params: { format },
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `report-${id}.${format}`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      throw error
    }
  }

  return {
    loading,
    reports,
    templates,
    currentReport,
    getReports,
    getReport,
    createReport,
    deleteReport,
    getTemplates,
    createTemplate,
    updateTemplate,
    deleteTemplate,
    downloadReport
  }
}) 