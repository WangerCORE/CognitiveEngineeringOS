import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/utils/axios'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref(null)
  const token = ref(localStorage.getItem('token'))

  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  const setCurrentUser = (user) => {
    currentUser.value = user
  }

  const login = async (credentials) => {
    try {
      const response = await axios.post('/api/auth/login', credentials)
      setToken(response.data.token)
      setCurrentUser(response.data.user)
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const logout = () => {
    setToken(null)
    setCurrentUser(null)
  }

  const register = async (userData) => {
    try {
      const response = await axios.post('/api/users/register', userData)
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const getAllUsers = async (params) => {
    try {
      const response = await axios.get('/api/users', { params })
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const getUserById = async (id) => {
    try {
      const response = await axios.get(`/api/users/${id}`)
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const createUser = async (userData) => {
    try {
      const response = await axios.post('/api/users', userData)
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const updateUser = async (id, userData) => {
    try {
      const response = await axios.put(`/api/users/${id}`, userData)
      return response.data
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const deleteUser = async (id) => {
    try {
      await axios.delete(`/api/users/${id}`)
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const changePassword = async (id, oldPassword, newPassword) => {
    try {
      await axios.post(`/api/users/${id}/change-password`, null, {
        params: { oldPassword, newPassword }
      })
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const enableUser = async (id) => {
    try {
      await axios.post(`/api/users/${id}/enable`)
    } catch (error) {
      throw error.response?.data || error
    }
  }

  const disableUser = async (id) => {
    try {
      await axios.post(`/api/users/${id}/disable`)
    } catch (error) {
      throw error.response?.data || error
    }
  }

  return {
    currentUser,
    token,
    login,
    logout,
    register,
    getAllUsers,
    getUserById,
    createUser,
    updateUser,
    deleteUser,
    changePassword,
    enableUser,
    disableUser
  }
}) 