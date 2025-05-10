import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api/config'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value)

  // 登录
  const login = async (credentials) => {
    try {
      loading.value = true
      const response = await api.post('/auth/login', credentials)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      return response
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      loading.value = true
      const response = await api.get('/auth/me')
      user.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 更新用户信息
  const updateUser = async (data) => {
    try {
      loading.value = true
      const response = await api.put('/auth/profile', data)
      user.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    login,
    logout,
    fetchCurrentUser,
    updateUser
  }
}) 