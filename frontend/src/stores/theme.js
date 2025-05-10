import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem('theme') === 'dark')

  // 监听主题变化并应用
  watch(isDark, (newValue) => {
    document.documentElement.classList.toggle('dark', newValue)
    localStorage.setItem('theme', newValue ? 'dark' : 'light')
  }, { immediate: true })

  // 切换主题
  const toggleTheme = () => {
    isDark.value = !isDark.value
  }

  return {
    isDark,
    toggleTheme
  }
}) 