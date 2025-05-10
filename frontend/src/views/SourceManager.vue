<template>
  <div class="source-manager">
    <div class="page-header">
      <h2>RSS源管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>添加源
        </el-button>
        <el-upload
          class="upload-btn"
          action="/api/sources/import"
          :show-file-list="false"
          :on-success="handleImportSuccess"
          :on-error="handleImportError"
          accept=".json,.xml"
        >
          <el-button>
            <el-icon><Upload /></el-icon>导入
          </el-button>
        </el-upload>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索RSS源..."
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="filterStatus" placeholder="状态" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="正常" value="active" />
        <el-option label="错误" value="error" />
        <el-option label="禁用" value="disabled" />
      </el-select>
    </div>

    <!-- RSS源列表 -->
    <el-table
      v-loading="loading"
      :data="sources"
      border
      style="width: 100%"
    >
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="url" label="URL" min-width="250" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="lastUpdate" label="最后更新" width="180">
        <template #default="{ row }">
          {{ formatDate(row.lastUpdate) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button size="small" @click="handleTest(row)">
              <el-icon><Connection /></el-icon>测试
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加RSS源' : '编辑RSS源'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入RSS源名称" />
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="form.url" placeholder="请输入RSS源URL" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="更新间隔" prop="updateInterval">
          <el-input-number
            v-model="form.updateInterval"
            :min="5"
            :max="1440"
            :step="5"
          />
          <span class="unit">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 测试结果对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="RSS源测试结果"
      width="500px"
    >
      <div v-if="testResult">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="状态">
            <el-tag :type="testResult.success ? 'success' : 'danger'">
              {{ testResult.success ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="消息">
            {{ testResult.message }}
          </el-descriptions-item>
          <el-descriptions-item v-if="testResult.items" label="文章数量">
            {{ testResult.items.length }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="testResult.items" class="test-items">
          <h4>最新文章</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in testResult.items.slice(0, 5)"
              :key="item.guid"
              :timestamp="formatDate(item.pubDate)"
            >
              {{ item.title }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Edit,
  Delete,
  Search,
  Upload,
  Download,
  Connection
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import { useSourceStore } from '@/stores/source'

const sourceStore = useSourceStore()

// 数据列表
const loading = ref(false)
const sources = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const filterStatus = ref('')

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  name: '',
  url: '',
  category: '',
  description: '',
  updateInterval: 30
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入RSS源名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入RSS源URL', trigger: 'blur' },
    { type: 'url', message: '请输入正确的URL格式', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请输入分类', trigger: 'blur' }
  ],
  updateInterval: [
    { required: true, message: '请输入更新间隔', trigger: 'blur' }
  ]
}

// 测试结果
const testDialogVisible = ref(false)
const testResult = ref(null)

// 获取RSS源列表
const fetchSources = async () => {
  loading.value = true
  try {
    const response = await sourceStore.getSources({
      page: currentPage.value,
      size: pageSize.value,
      query: searchQuery.value,
      status: filterStatus.value
    })
    sources.value = response.items
    total.value = response.total
  } catch (error) {
    ElMessage.error('获取RSS源列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索和筛选
const handleSearch = () => {
  currentPage.value = 1
  fetchSources()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchSources()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchSources()
}

// 添加RSS源
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    name: '',
    url: '',
    category: '',
    description: '',
    updateInterval: 30
  }
  dialogVisible.value = true
}

// 编辑RSS源
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除RSS源
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该RSS源吗？', '提示', {
      type: 'warning'
    })
    await sourceStore.deleteSource(row.id)
    ElMessage.success('删除成功')
    fetchSources()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    if (dialogType.value === 'add') {
      await sourceStore.createSource(form.value)
      ElMessage.success('添加成功')
    } else {
      await sourceStore.updateSource(form.value.id, form.value)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchSources()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

// 测试RSS源
const handleTest = async (row) => {
  try {
    testResult.value = await sourceStore.testSource(row.id)
    testDialogVisible.value = true
  } catch (error) {
    ElMessage.error('测试失败')
  }
}

// 导入/导出
const handleImportSuccess = () => {
  ElMessage.success('导入成功')
  fetchSources()
}

const handleImportError = () => {
  ElMessage.error('导入失败')
}

const handleExport = async () => {
  try {
    await sourceStore.exportSources()
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 状态相关
const getStatusType = (status) => {
  switch (status) {
    case 'active':
      return 'success'
    case 'error':
      return 'danger'
    case 'disabled':
      return 'info'
    default:
      return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'active':
      return '正常'
    case 'error':
      return '错误'
    case 'disabled':
      return '禁用'
    default:
      return '未知'
  }
}

onMounted(() => {
  fetchSources()
})
</script>

<style scoped>
.source-manager {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.unit {
  margin-left: 10px;
  color: var(--el-text-color-secondary);
}

.test-items {
  margin-top: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.test-items h4 {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

:deep(.el-upload) {
  display: inline-block;
}
</style> 