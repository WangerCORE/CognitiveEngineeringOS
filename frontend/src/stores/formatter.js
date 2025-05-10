import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { request } from '@/utils/request'

export const useFormatterStore = defineStore('formatter', {
  state: () => ({
    formatters: ref([]),
    currentFormatter: ref(null),
    loading: ref(false),
    stats: ref(null),
    testResult: ref(null),
    total: ref(0)
  }),

  actions: {
    // 获取所有格式化器
    async getFormatters(params) {
      const response = await request.get('/api/formatters', { params })
      return response.data
    },

    // 获取单个格式化器
    async fetchFormatter(id) {
      try {
        this.loading = true
        const response = await request.get(`/api/formatters/${id}`)
        this.currentFormatter = response.data
        return response.data
      } finally {
        this.loading = false
      }
    },

    // 创建格式化器
    async createFormatter(data) {
      const response = await request.post('/api/formatters', data)
      return response.data
    },

    // 更新格式化器
    async updateFormatter(data) {
      const response = await request.put(`/api/formatters/${data.id}`, data)
      return response.data
    },

    // 删除格式化器
    async deleteFormatter(id) {
      const response = await request.delete(`/api/formatters/${id}`)
      return response.data
    },

    // 批量删除格式化器
    async batchDeleteFormatters(ids) {
      const response = await request.delete('/api/formatters/batch', {
        data: { ids }
      })
      return response.data
    },

    // 更新格式化器状态
    async updateFormatterStatus(id, status) {
      const response = await request.patch(`/api/formatters/${id}/status`, {
        status
      })
      return response.data
    },

    // 测试格式化器
    async testFormatter(id) {
      const response = await request.post(`/api/formatters/${id}/test`)
      return response.data
    },

    // 获取格式化器统计
    async fetchStats() {
      try {
        this.loading = true
        const response = await request.get('/api/formatters/stats')
        this.stats = response.data
        return response.data
      } finally {
        this.loading = false
      }
    }
  },

  // 计算属性
  get activeFormatters() {
    return this.formatters.filter(f => f.active)
  },
  get inactiveFormatters() {
    return this.formatters.filter(f => !f.active)
  },
  get markdownFormatters() {
    return this.formatters.filter(f => f.type === 'markdown')
  },
  get htmlFormatters() {
    return this.formatters.filter(f => f.type === 'html')
  },
  get textFormatters() {
    return this.formatters.filter(f => f.type === 'text')
  }
}) 