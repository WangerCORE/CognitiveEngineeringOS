<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <span v-show="!isCollapse" class="title">CE-OS</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
      >
        <el-menu-item index="/">
          <el-icon><Monitor /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-menu-item index="/sources">
          <el-icon><Link /></el-icon>
          <template #title>RSS源管理</template>
        </el-menu-item>
        
        <el-menu-item index="/formatters">
          <el-icon><Document /></el-icon>
          <template #title>格式化器管理</template>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人信息</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            @click="toggleSidebar"
            class="collapse-btn"
          >
            <el-icon>
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item @click="toggleTheme">
                  <el-icon><Moon v-if="isDarkTheme" /><Sunny v-else /></el-icon>
                  {{ isDarkTheme ? '浅色模式' : '深色模式' }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import {
  Monitor,
  Link,
  Document,
  User,
  Expand,
  Fold,
  Moon,
  Sunny,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const themeStore = useThemeStore()

// 侧边栏折叠状态
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 当前路由信息
const currentRoute = computed(() => route.meta.title || '')
const activeMenu = computed(() => route.path)

// 用户信息
const username = computed(() => authStore.user?.username || '用户')
const userAvatar = computed(() => authStore.user?.avatar || '')

// 主题相关
const isDarkTheme = computed(() => themeStore.isDark)
const toggleTheme = () => {
  themeStore.toggleTheme()
}

// 导航方法
const goToProfile = () => {
  router.push('/profile')
}

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: var(--el-menu-bg-color);
  border-right: 1px solid var(--el-border-color-light);
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  height: 60px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--el-border-color-light);
}

.logo {
  height: 32px;
  width: 32px;
}

.title {
  margin-left: 10px;
  font-size: 18px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  white-space: nowrap;
}

.header {
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.main {
  background-color: var(--el-bg-color-page);
  padding: 20px;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .aside {
    position: fixed;
    z-index: 1000;
    height: 100vh;
    transform: translateX(0);
    transition: transform 0.3s;
  }

  .aside.collapsed {
    transform: translateX(-100%);
  }

  .header {
    padding: 0 10px;
  }

  .username {
    display: none;
  }

  .main {
    padding: 10px;
  }
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 