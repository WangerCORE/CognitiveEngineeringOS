import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useEntryStore = defineStore('entry', () => {
  const entries = ref([])
  const loading = ref(false)

  // 获取RSS条目列表
  const getEntries = async (params) => {
    try {
      const response = await axios.get('/api/entries', { params })
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 获取单个RSS条目
  const getEntry = async (id) => {
    try {
      const response = await axios.get(`/api/entries/${id}`)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 标记为已读
  const markAsRead = async (id) => {
    try {
      const response = await axios.post(`/api/entries/${id}/read`)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 标记为未读
  const markAsUnread = async (id) => {
    try {
      const response = await axios.post(`/api/entries/${id}/unread`)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 切换收藏状态
  const toggleStar = async (id) => {
    try {
      const response = await axios.post(`/api/entries/${id}/star`)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 删除RSS条目
  const deleteEntry = async (id) => {
    try {
      await axios.delete(`/api/entries/${id}`)
    } catch (error) {
      throw error
    }
  }

  return {
    entries,
    loading,
    getEntries,
    getEntry,
    markAsRead,
    markAsUnread,
    toggleStar,
    deleteEntry
  }
}) 