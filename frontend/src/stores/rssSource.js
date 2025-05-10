import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { rssSourceApi } from '@/api/rssSource'

export const useRssSourceStore = defineStore('rssSource', () => {
  const sources = ref([])
  const currentSource = ref(null)
  const loading = ref(false)
  const stats = ref(null)

  // 获取所有RSS源
  const fetchSources = async () => {
    try {
      loading.value = true
      const response = await rssSourceApi.getSources()
      sources.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取单个RSS源
  const fetchSource = async (id) => {
    try {
      loading.value = true
      const response = await rssSourceApi.getSource(id)
      currentSource.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 创建RSS源
  const createSource = async (data) => {
    try {
      loading.value = true
      const response = await rssSourceApi.createSource(data)
      sources.value.push(response)
      return response
    } finally {
      loading.value = false
    }
  }

  // 更新RSS源
  const updateSource = async (id, data) => {
    try {
      loading.value = true
      const response = await rssSourceApi.updateSource(id, data)
      const index = sources.value.findIndex(s => s.id === id)
      if (index > -1) {
        sources.value[index] = response
      }
      if (currentSource.value?.id === id) {
        currentSource.value = response
      }
      return response
    } finally {
      loading.value = false
    }
  }

  // 删除RSS源
  const deleteSource = async (id) => {
    try {
      loading.value = true
      await rssSourceApi.deleteSource(id)
      const index = sources.value.findIndex(s => s.id === id)
      if (index > -1) {
        sources.value.splice(index, 1)
      }
      if (currentSource.value?.id === id) {
        currentSource.value = null
      }
    } finally {
      loading.value = false
    }
  }

  // 更新RSS源状态
  const updateSourceStatus = async (id, active) => {
    try {
      loading.value = true
      const response = await rssSourceApi.updateSourceStatus(id, active)
      const index = sources.value.findIndex(s => s.id === id)
      if (index > -1) {
        sources.value[index] = response
      }
      if (currentSource.value?.id === id) {
        currentSource.value = response
      }
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取RSS源统计
  const fetchStats = async () => {
    try {
      loading.value = true
      const response = await rssSourceApi.getSourceStats()
      stats.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取RSS源历史
  const fetchHistory = async (id, params) => {
    try {
      loading.value = true
      return await rssSourceApi.getSourceHistory(id, params)
    } finally {
      loading.value = false
    }
  }

  // 计算属性
  const activeSources = computed(() => sources.value.filter(s => s.active))
  const inactiveSources = computed(() => sources.value.filter(s => !s.active))

  return {
    sources,
    currentSource,
    loading,
    stats,
    activeSources,
    inactiveSources,
    fetchSources,
    fetchSource,
    createSource,
    updateSource,
    deleteSource,
    updateSourceStatus,
    fetchStats,
    fetchHistory
  }
}) 