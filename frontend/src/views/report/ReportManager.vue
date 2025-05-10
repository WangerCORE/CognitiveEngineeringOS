<template>
  <div class="report-manager">
    <div class="report-manager__header">
      <h1>报告管理</h1>
      <div class="report-manager__actions">
        <el-button type="primary" @click="showCreateReportDialog">
          生成报告
        </el-button>
        <el-button @click="showTemplateManager">
          模板管理
        </el-button>
      </div>
    </div>

    <el-card class="report-manager__list">
      <el-table
        v-loading="loading"
        :data="reports"
        style="width: 100%"
      >
        <el-table-column prop="title" label="报告标题" min-width="200" />
        <el-table-column prop="templateName" label="使用模板" width="150" />
        <el-table-column prop="period" label="报告周期" width="150" />
        <el-table-column prop="createdAt" label="生成时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status === 'completed' ? '已完成' : '生成中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                size="small"
                :disabled="row.status !== 'completed'"
                @click="handlePreview(row)"
              >
                预览
              </el-button>
              <el-button
                type="success"
                size="small"
                :disabled="row.status !== 'completed'"
                @click="handleDownload(row, 'markdown')"
              >
                下载MD
              </el-button>
              <el-button
                type="info"
                size="small"
                :disabled="row.status !== 'completed'"
                @click="handleDownload(row, 'html')"
              >
                下载HTML
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建报告对话框 -->
    <el-dialog
      v-model="createReportDialogVisible"
      title="生成报告"
      width="50%"
    >
      <el-form
        ref="createReportForm"
        :model="newReport"
        :rules="reportRules"
        label-width="100px"
      >
        <el-form-item label="报告标题" prop="title">
          <el-input v-model="newReport.title" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告模板" prop="templateId">
          <el-select v-model="newReport.templateId" placeholder="请选择报告模板">
            <el-option
              v-for="template in templates"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报告周期" prop="period">
          <el-date-picker
            v-model="newReport.period"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="dateShortcuts"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createReportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateReport">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览报告对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="currentReport?.title"
      width="80%"
      class="preview-dialog"
    >
      <div v-if="currentReport" class="report-preview">
        <div class="report-preview__content" v-html="currentReport.content"></div>
      </div>
    </el-dialog>

    <!-- 模板管理对话框 -->
    <el-dialog
      v-model="templateManagerVisible"
      title="模板管理"
      width="70%"
    >
      <div class="template-manager">
        <div class="template-manager__header">
          <el-button type="primary" @click="showCreateTemplateDialog">
            新建模板
          </el-button>
        </div>
        <el-table
          :data="templates"
          style="width: 100%"
        >
          <el-table-column prop="name" label="模板名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEditTemplate(row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteTemplate(row)"
                >
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 创建/编辑模板对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="isEditingTemplate ? '编辑模板' : '新建模板'"
      width="70%"
    >
      <el-form
        ref="templateForm"
        :model="currentTemplate"
        :rules="templateRules"
        label-width="100px"
      >
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="currentTemplate.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="currentTemplate.description"
            type="textarea"
            placeholder="请输入模板描述"
          />
        </el-form-item>
        <el-form-item label="模板内容" prop="content">
          <el-input
            v-model="currentTemplate.content"
            type="textarea"
            :rows="15"
            placeholder="请输入模板内容（支持Markdown语法）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTemplate">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useReportStore } from '@/stores/report'
import { formatDate } from '@/utils/date'
import { handleBusinessError } from '@/utils/errorHandler'

const reportStore = useReportStore()
const loading = ref(false)
const reports = ref([])
const templates = ref([])
const currentReport = ref(null)

// 对话框控制
const createReportDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const templateManagerVisible = ref(false)
const templateDialogVisible = ref(false)
const isEditingTemplate = ref(false)

// 表单数据
const newReport = ref({
  title: '',
  templateId: '',
  period: []
})
const currentTemplate = ref({
  name: '',
  description: '',
  content: ''
})

// 表单引用
const createReportForm = ref(null)
const templateForm = ref(null)

// 表单验证规则
const reportRules = {
  title: [
    { required: true, message: '请输入报告标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  templateId: [
    { required: true, message: '请选择报告模板', trigger: 'change' }
  ],
  period: [
    { required: true, message: '请选择报告周期', trigger: 'change' }
  ]
}

const templateRules = {
  name: [
    { required: true, message: '请输入模板名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入模板内容', trigger: 'blur' }
  ]
}

// 日期快捷选项
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      reportStore.getReports(),
      reportStore.getTemplates()
    ])
    reports.value = reportStore.reports
    templates.value = reportStore.templates
  } catch (error) {
    handleBusinessError(error)
  } finally {
    loading.value = false
  }
}

// 显示创建报告对话框
const showCreateReportDialog = () => {
  newReport.value = {
    title: '',
    templateId: '',
    period: []
  }
  createReportDialogVisible.value = true
}

// 创建报告
const handleCreateReport = async () => {
  if (!createReportForm.value) return
  
  try {
    await createReportForm.value.validate()
    const [startDate, endDate] = newReport.value.period
    await reportStore.createReport({
      ...newReport.value,
      startDate: startDate.toISOString(),
      endDate: endDate.toISOString()
    })
    ElMessage.success('报告生成任务已创建')
    createReportDialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

// 预览报告
const handlePreview = async (report) => {
  try {
    await reportStore.getReport(report.id)
    currentReport.value = reportStore.currentReport
    previewDialogVisible.value = true
  } catch (error) {
    handleBusinessError(error)
  }
}

// 下载报告
const handleDownload = async (report, format) => {
  try {
    await reportStore.downloadReport(report.id, format)
    ElMessage.success('下载成功')
  } catch (error) {
    handleBusinessError(error)
  }
}

// 删除报告
const handleDelete = async (report) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除报告"${report.title}"吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    await reportStore.deleteReport(report.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

// 显示模板管理
const showTemplateManager = () => {
  templateManagerVisible.value = true
}

// 显示创建模板对话框
const showCreateTemplateDialog = () => {
  isEditingTemplate.value = false
  currentTemplate.value = {
    name: '',
    description: '',
    content: ''
  }
  templateDialogVisible.value = true
}

// 编辑模板
const handleEditTemplate = (template) => {
  isEditingTemplate.value = true
  currentTemplate.value = { ...template }
  templateDialogVisible.value = true
}

// 保存模板
const handleSaveTemplate = async () => {
  if (!templateForm.value) return
  
  try {
    await templateForm.value.validate()
    if (isEditingTemplate.value) {
      await reportStore.updateTemplate(currentTemplate.value.id, currentTemplate.value)
      ElMessage.success('模板更新成功')
    } else {
      await reportStore.createTemplate(currentTemplate.value)
      ElMessage.success('模板创建成功')
    }
    templateDialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

// 删除模板
const handleDeleteTemplate = async (template) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模板"${template.name}"吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    await reportStore.deleteTemplate(template.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.report-manager {
  padding: 20px;

  &__header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    h1 {
      margin: 0;
    }
  }

  &__list {
    margin-bottom: 20px;
  }
}

.template-manager {
  &__header {
    margin-bottom: 20px;
  }
}

.report-preview {
  &__content {
    max-height: 70vh;
    overflow-y: auto;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
}

:deep(.preview-dialog) {
  .el-dialog__body {
    padding: 0;
  }
}
</style> 