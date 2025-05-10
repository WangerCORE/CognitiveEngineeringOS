import { ElMessage } from 'element-plus'
import router from '@/router'

// 错误类型映射
const ERROR_MESSAGES = {
  400: '请求参数错误',
  401: '未授权，请重新登录',
  403: '拒绝访问',
  404: '请求的资源不存在',
  500: '服务器内部错误',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时'
}

// 处理HTTP错误
export const handleHttpError = (error) => {
  if (error.response) {
    const { status } = error.response
    
    // 处理401错误
    if (status === 401) {
      // 清除用户信息
      localStorage.removeItem('token')
      // 重定向到登录页
      router.push('/login')
    }
    
    // 显示错误消息
    const message = ERROR_MESSAGES[status] || '请求失败'
    ElMessage.error(message)
    
    // 记录错误日志
    logError('HTTP错误', {
      status,
      url: error.config?.url,
      method: error.config?.method,
      message: error.message
    })
  } else if (error.request) {
    // 请求已发出但没有收到响应
    ElMessage.error('网络错误，请检查网络连接')
    logError('网络错误', {
      message: error.message
    })
  } else {
    // 请求配置出错
    ElMessage.error('请求配置错误')
    logError('请求错误', {
      message: error.message
    })
  }
  
  return Promise.reject(error)
}

// 处理业务逻辑错误
export const handleBusinessError = (error) => {
  ElMessage.error(error.message || '操作失败')
  logError('业务错误', {
    message: error.message,
    code: error.code
  })
  return Promise.reject(error)
}

// 记录错误日志
export const logError = (type, data) => {
  const log = {
    type,
    timestamp: new Date().toISOString(),
    ...data
  }
  
  // 在开发环境下打印到控制台
  if (process.env.NODE_ENV === 'development') {
    console.error('错误日志:', log)
  }
  
  // TODO: 在生产环境下发送到日志服务器
  // sendToLogServer(log)
}

// 设置全局错误处理
export const setupGlobalErrorHandler = () => {
  // 处理未捕获的Promise错误
  window.addEventListener('unhandledrejection', (event) => {
    handleHttpError(event.reason)
  })
  
  // 处理未捕获的JavaScript错误
  window.addEventListener('error', (event) => {
    logError('未捕获的错误', {
      message: event.message,
      filename: event.filename,
      lineno: event.lineno,
      colno: event.colno,
      error: event.error?.stack
    })
  })
} 