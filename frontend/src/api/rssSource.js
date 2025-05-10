import api from './config'

export const rssSourceApi = {
  // 获取RSS源列表
  getSources() {
    return api.get('/rss-sources')
  },

  // 获取单个RSS源
  getSource(id) {
    return api.get(`/rss-sources/${id}`)
  },

  // 创建RSS源
  createSource(data) {
    return api.post('/rss-sources', data)
  },

  // 更新RSS源
  updateSource(id, data) {
    return api.put(`/rss-sources/${id}`, data)
  },

  // 删除RSS源
  deleteSource(id) {
    return api.delete(`/rss-sources/${id}`)
  },

  // 更新RSS源状态
  updateSourceStatus(id, active) {
    return api.patch(`/rss-sources/${id}/status`, { active })
  },

  // 获取RSS源抓取统计
  getSourceStats() {
    return api.get('/rss-sources/stats')
  },

  // 获取RSS源抓取历史
  getSourceHistory(id, params) {
    return api.get(`/rss-sources/${id}/history`, { params })
  }
} 