<template>
  <div class="error-page">
    <el-result
      :icon="errorIcon"
      :title="errorTitle"
      :sub-title="errorMessage"
    >
      <template #extra>
        <el-button type="primary" @click="handleBack">返回上一页</el-button>
        <el-button @click="handleHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const errorCode = computed(() => route.params.code || '404')
const errorIcon = computed(() => {
  switch (errorCode.value) {
    case '403':
      return 'warning'
    case '404':
      return 'error'
    case '500':
      return 'error'
    default:
      return 'warning'
  }
})

const errorTitle = computed(() => {
  switch (errorCode.value) {
    case '403':
      return '访问被拒绝'
    case '404':
      return '页面不存在'
    case '500':
      return '服务器错误'
    default:
      return '发生错误'
  }
})

const errorMessage = computed(() => {
  switch (errorCode.value) {
    case '403':
      return '您没有权限访问此页面'
    case '404':
      return '抱歉，您访问的页面不存在'
    case '500':
      return '抱歉，服务器出现错误'
    default:
      return '抱歉，发生了一些错误'
  }
})

const handleBack = () => {
  router.back()
}

const handleHome = () => {
  router.push('/')
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}
</style> 