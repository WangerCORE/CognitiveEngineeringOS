import api from './config'

export const formatterApi = {
  // 获取格式化器列表
  getFormatters() {
    return api.get('/formatters')
  },

  // 获取单个格式化器
  getFormatter(id) {
    return api.get(`/formatters/${id}`)
  },

  // 创建格式化器
  createFormatter(data) {
    return api.post('/formatters', data)
  },

  // 更新格式化器
  updateFormatter(id, data) {
    return api.put(`/formatters/${id}`, data)
  },

  // 删除格式化器
  deleteFormatter(id) {
    return api.delete(`/formatters/${id}`)
  },

  // 更新格式化器状态
  updateFormatterStatus(id, active) {
    return api.patch(`/formatters/${id}/status`, { active })
  },

  // 测试格式化器
  testFormatter(id, content) {
    return api.post(`/formatters/${id}/test`, { content })
  },

  // 获取格式化器统计
  getFormatterStats() {
    return api.get('/formatters/stats')
  }
} 