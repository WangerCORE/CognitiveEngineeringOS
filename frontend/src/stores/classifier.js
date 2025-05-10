import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useClassifierStore = defineStore('classifier', () => {
  const loading = ref(false)
  const tags = ref([])
  const confidenceThreshold = ref(0.6)

  // 获取所有标签
  const getTags = async () => {
    try {
      const response = await axios.get('/api/classifier/tags')
      tags.value = response.data.data
      return tags.value
    } catch (error) {
      throw error
    }
  }

  // 添加新标签
  const addTag = async (tag) => {
    try {
      const response = await axios.post('/api/classifier/tags', { name: tag })
      tags.value.push(response.data.data)
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 删除标签
  const deleteTag = async (tagId) => {
    try {
      await axios.delete(`/api/classifier/tags/${tagId}`)
      tags.value = tags.value.filter(tag => tag.id !== tagId)
    } catch (error) {
      throw error
    }
  }

  // 更新置信度阈值
  const updateConfidenceThreshold = async (threshold) => {
    try {
      await axios.put('/api/classifier/settings', { confidenceThreshold: threshold })
      confidenceThreshold.value = threshold
    } catch (error) {
      throw error
    }
  }

  // 获取分类器设置
  const getSettings = async () => {
    try {
      const response = await axios.get('/api/classifier/settings')
      confidenceThreshold.value = response.data.data.confidenceThreshold
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  // 手动分类内容
  const classifyContent = async (content) => {
    try {
      const response = await axios.post('/api/classifier/classify', { content })
      return response.data.data
    } catch (error) {
      throw error
    }
  }

  return {
    loading,
    tags,
    confidenceThreshold,
    getTags,
    addTag,
    deleteTag,
    updateConfidenceThreshold,
    getSettings,
    classifyContent
  }
}) 