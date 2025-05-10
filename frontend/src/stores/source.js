import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useSourceStore = defineStore('source', () => {
  const sources = ref([])
  const loading = ref(false)

  // 获取RSS源列表
  const getSources = async (params) => {
    try {
      const response = await axios.get('/api/sources', { params })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 创建RSS源
  const createSource = async (data) => {
    try {
      const response = await axios.post('/api/sources', data)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 更新RSS源
  const updateSource = async (id, data) => {
    try {
      const response = await axios.put(`/api/sources/${id}`, data)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 删除RSS源
  const deleteSource = async (id) => {
    try {
      await axios.delete(`/api/sources/${id}`)
    } catch (error) {
      throw error
    }
  }

  // 测试RSS源
  const testSource = async (id) => {
    try {
      const response = await axios.post(`/api/sources/${id}/test`)
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 导入RSS源
  const importSources = async (file) => {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const response = await axios.post('/api/sources/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 导出RSS源
  const exportSources = async () => {
    try {
      const response = await axios.get('/api/sources/export', {
        responseType: 'blob'
      })
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', 'rss-sources.json')
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (error) {
      throw error
    }
  }

  return {
    sources,
    loading,
    getSources,
    createSource,
    updateSource,
    deleteSource,
    testSource,
    importSources,
    exportSources
  }
}) 