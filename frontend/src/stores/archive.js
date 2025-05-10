import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useArchiveStore = defineStore('archive', () => {
  const loading = ref(false)
  const archives = ref([])
  const currentArchive = ref(null)
  const currentFile = ref(null)

  // 获取归档列表
  const getArchives = async (params) => {
    try {
      const response = await axios.get('/api/archives', { params })
      archives.value = response.data.data
      return archives.value
    } catch (error) {
      throw error
    }
  }

  // 获取单个归档
  const getArchive = async (id) => {
    try {
      const response = await axios.get(`/api/archives/${id}`)
      currentArchive.value = response.data.data
      return currentArchive.value
    } catch (error) {
      throw error
    }
  }

  // 创建归档
  const createArchive = async (data) => {
    try {
      const formData = new FormData()
      formData.append('name', data.name)
      formData.append('description', data.description)
      data.files.forEach(file => {
        formData.append('files', file.raw)
      })

      const response = await axios.post('/api/archives', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      archives.value.unshift(response.data.data)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 删除归档
  const deleteArchive = async (id) => {
    try {
      await axios.delete(`/api/archives/${id}`)
      archives.value = archives.value.filter(archive => archive.id !== id)
    } catch (error) {
      throw error
    }
  }

  // 下载归档
  const downloadArchive = async (id) => {
    try {
      const response = await axios.get(`/api/archives/${id}/download`, {
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `archive-${id}.zip`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      throw error
    }
  }

  // 获取文件版本历史
  const getFileVersions = async (fileId) => {
    try {
      const response = await axios.get(`/api/archives/files/${fileId}/versions`)
      currentFile.value = response.data.data
      return currentFile.value
    } catch (error) {
      throw error
    }
  }

  // 下载文件
  const downloadFile = async (fileId) => {
    try {
      const response = await axios.get(`/api/archives/files/${fileId}/download`, {
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `file-${fileId}`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      throw error
    }
  }

  // 下载特定版本
  const downloadVersion = async (versionId) => {
    try {
      const response = await axios.get(`/api/archives/versions/${versionId}/download`, {
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `version-${versionId}`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      throw error
    }
  }

  // 恢复版本
  const restoreVersion = async (versionId) => {
    try {
      const response = await axios.post(`/api/archives/versions/${versionId}/restore`)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  return {
    loading,
    archives,
    currentArchive,
    currentFile,
    getArchives,
    getArchive,
    createArchive,
    deleteArchive,
    downloadArchive,
    getFileVersions,
    downloadFile,
    downloadVersion,
    restoreVersion
  }
}) 