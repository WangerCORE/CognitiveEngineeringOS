<template>
  <div class="formatter-manager">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索格式化器名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="状态" clearable>
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加格式化器
        </el-button>
        <el-button
          type="danger"
          :disabled="!selectedFormatters.length"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="formatters"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastUsedTime" label="最后使用时间" width="180" />
      <el-table-column prop="usageCount" label="使用次数" width="100" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button
              type="primary"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              @click="handleTest(row)"
            >
              测试
            </el-button>
            <el-button
              :type="row.status === 'enabled' ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'enabled' ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
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
      :title="dialogType === 'add' ? '添加格式化器' : '编辑格式化器'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入格式化器名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="请输入格式化器描述"
          />
        </el-form-item>
        <el-form-item label="提示词" prop="prompt">
          <el-input
            v-model="form.prompt"
            type="textarea"
            rows="5"
            placeholder="请输入格式化器提示词"
          />
        </el-form-item>
        <el-form-item label="示例" prop="example">
          <el-input
            v-model="form.example"
            type="textarea"
            rows="5"
            placeholder="请输入格式化器示例"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 测试结果对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="测试结果"
      width="800px"
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
        </el-descriptions>

        <div v-if="testResult.formattedContent" class="test-result">
          <h4>格式化结果</h4>
          <el-card class="formatted-content">
            <div v-html="testResult.formattedContent"></div>
          </el-card>
        </div>
      </div>
      <template #footer>
        <el-button @click="testDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useFormatterStore } from '@/stores/formatter'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete
} from '@element-plus/icons-vue'

const formatterStore = useFormatterStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const testDialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const selectedFormatters = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const testResult = ref(null)

const searchForm = reactive({
  keyword: '',
  status: ''
})

const form = reactive({
  id: null,
  name: '',
  description: '',
  prompt: '',
  example: ''
})

const rules = {
  name: [
    { required: true, message: '请输入格式化器名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入格式化器描述', trigger: 'blur' }
  ],
  prompt: [
    { required: true, message: '请输入格式化器提示词', trigger: 'blur' }
  ],
  example: [
    { required: true, message: '请输入格式化器示例', trigger: 'blur' }
  ]
}

const formatters = ref([])

const getStatusType = (status) => {
  const map = {
    enabled: 'success',
    disabled: 'warning'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    enabled: '启用',
    disabled: '禁用'
  }
  return map[status] || '未知'
}

const loadData = async () => {
  try {
    loading.value = true
    const { data, total: totalCount } = await formatterStore.getFormatters({
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    })
    formatters.value = data
    total.value = totalCount
  } catch (error) {
    ElMessage.error('加载格式化器列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.id = null
  form.name = ''
  form.description = ''
  form.prompt = ''
  form.example = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        if (dialogType.value === 'add') {
          await formatterStore.createFormatter(form)
          ElMessage.success('添加成功')
        } else {
          await formatterStore.updateFormatter(form)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该格式化器吗？', '提示', {
      type: 'warning'
    })
    await formatterStore.deleteFormatter(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedFormatters.value.length} 个格式化器吗？`,
      '提示',
      { type: 'warning' }
    )
    await formatterStore.batchDeleteFormatters(
      selectedFormatters.value.map(item => item.id)
    )
    ElMessage.success('批量删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'
    await formatterStore.updateFormatterStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadData()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleTest = async (row) => {
  try {
    loading.value = true
    testResult.value = await formatterStore.testFormatter(row.id)
    testDialogVisible.value = true
  } catch (error) {
    ElMessage.error('测试失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedFormatters.value = selection
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.formatter-manager {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  display: flex;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.test-result {
  margin-top: 20px;
}

.test-result h4 {
  margin: 0 0 10px;
  color: #606266;
}

.formatted-content {
  max-height: 400px;
  overflow-y: auto;
}
</style> 