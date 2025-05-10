<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总格式化器数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.total }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>启用格式化器</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.enabled }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总使用次数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.usageCount }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-formatters">
      <template #header>
        <div class="card-header">
          <span>最近使用的格式化器</span>
          <el-button type="primary" @click="$router.push('/formatters')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentFormatters" style="width: 100%">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="usageCount" label="使用次数" width="100" />
        <el-table-column prop="lastUsedTime" label="最后使用时间" width="180" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="testFormatter(row)">
              测试
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="testDialogVisible"
      title="测试格式化器"
      width="50%"
    >
      <el-form :model="testForm" label-width="100px">
        <el-form-item label="输入内容">
          <el-input
            v-model="testForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入要格式化的内容"
          />
        </el-form-item>
        <el-form-item label="格式化结果">
          <el-input
            v-model="testForm.result"
            type="textarea"
            :rows="5"
            readonly
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="formatContent">
            格式化
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const stats = ref({
  total: 0,
  enabled: 0,
  usageCount: 0
})

const recentFormatters = ref([])
const testDialogVisible = ref(false)
const testForm = ref({
  content: '',
  result: ''
})
const currentFormatter = ref(null)

const loadStats = async () => {
  try {
    const response = await axios.get('/api/formatters/stats')
    stats.value = response.data
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const loadRecentFormatters = async () => {
  try {
    const response = await axios.get('/api/formatters', {
      params: {
        page: 0,
        size: 5,
        sort: 'lastUsedTime,desc'
      }
    })
    recentFormatters.value = response.data.content
  } catch (error) {
    ElMessage.error('加载最近使用的格式化器失败')
  }
}

const testFormatter = (formatter) => {
  currentFormatter.value = formatter
  testForm.value = {
    content: '',
    result: ''
  }
  testDialogVisible.value = true
}

const formatContent = async () => {
  if (!testForm.value.content) {
    ElMessage.warning('请输入要格式化的内容')
    return
  }

  try {
    const response = await axios.post(`/api/formatters/${currentFormatter.value.id}/test`, {
      content: testForm.value.content
    })
    testForm.value.result = response.data.formattedContent
  } catch (error) {
    ElMessage.error('格式化失败')
  }
}

onMounted(() => {
  loadStats()
  loadRecentFormatters()
})
</script>

<style scoped>
.home {
  .stat-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .card-content {
      text-align: center;
      padding: 20px 0;

      h2 {
        margin: 0;
        font-size: 36px;
        color: #409eff;
      }
    }
  }

  .recent-formatters {
    margin-top: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style> 