<template>
  <el-config-provider :locale="zhCn" :message="messageConfig">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </el-config-provider>
</template>

<script setup>
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { useThemeStore } from '@/stores/theme'

// 配置Element Plus消息提示
const messageConfig = {
  max: 3
}

// 初始化主题
const themeStore = useThemeStore()
</script>

<style>
:root {
  --el-color-primary: #409eff;
}

html.dark {
  --el-color-primary: #409eff;
  --el-bg-color: #141414;
  --el-bg-color-overlay: #1d1e1f;
  --el-text-color-primary: #e5eaf3;
  --el-text-color-regular: #cfd3dc;
  --el-border-color: #4c4d4f;
  --el-border-color-light: #363637;
}

/* 全局过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: var(--el-bg-color-page);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--el-border-color-darker);
}
</style> 