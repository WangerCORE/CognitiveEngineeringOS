<template>
  <div class="archive-manager">
    <div class="archive-manager__header">
      <h1>文件归档管理</h1>
      <div class="archive-manager__actions">
        <el-button type="primary" @click="showCreateArchiveDialog">
          新建归档
        </el-button>
      </div>
    </div>

    <el-card class="archive-manager__list">
      <el-table
        v-loading="loading"
        :data="archives"
        style="width: 100%"
      >
        <el-table-column prop="name" label="归档名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="300" />
        <el-table-column prop="fileCount" label="文件数量" width="100" />
        <el-table-column prop="size" label="总大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
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
              <el-button
                type="primary"
                size="small"
                :disabled="row.status !== 'completed'"
                @click="handleViewFiles(row)"
              >
                查看文件
              </el-button>
              <el-button
                type="success"
                size="small"
                :disabled="row.status !== 'completed'"
                @click="handleDownload(row)"
              >
                下载
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

    <!-- 创建归档对话框 -->
    <el-dialog
      v-model="createArchiveDialogVisible"
      title="新建归档"
      width="50%"
    >
      <el-form
        ref="createArchiveForm"
        :model="newArchive"
        :rules="archiveRules"
        label-width="100px"
      >
        <el-form-item label="归档名称" prop="name">
          <el-input v-model="newArchive.name" placeholder="请输入归档名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="newArchive.description"
            type="textarea"
            placeholder="请输入归档描述"
          />
        </el-form-item>
        <el-form-item label="文件选择" prop="files">
          <el-upload
            v-model:file-list="newArchive.files"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            multiple
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createArchiveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateArchive">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 文件列表对话框 -->
    <el-dialog
      v-model="fileListDialogVisible"
      :title="currentArchive?.name + ' - 文件列表'"
      width="70%"
    >
      <el-table
        :data="currentArchive?.files || []"
        style="width: 100%"
      >
        <el-table-column prop="name" label="文件名" min-width="200" />
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                size="small"
                @click="handleViewVersion(row)"
              >
                版本历史
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="handleDownloadFile(row)"
              >
                下载
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 版本历史对话框 -->
    <el-dialog
      v-model="versionHistoryDialogVisible"
      :title="currentFile?.name + ' - 版本历史'"
      width="60%"
    >
      <el-timeline>
        <el-timeline-item
          v-for="version in currentFile?.versions"
          :key="version.id"
          :timestamp="formatDate(version.createdAt)"
          :type="version.isCurrent ? 'primary' : 'info'"
        >
          <h4>版本 {{ version.number }}</h4>
          <p>{{ version.description }}</p>
          <div class="version-actions">
            <el-button
              type="primary"
              size="small"
              @click="handleDownloadVersion(version)"
            >
              下载此版本
            </el-button>
            <el-button
              v-if="!version.isCurrent"
              type="success"
              size="small"
              @click="handleRestoreVersion(version)"
            >
              恢复此版本
            </el-button>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import { formatFileSize } from '@/utils/file'
import { handleBusinessError } from '@/utils/errorHandler'

const loading = ref(false)
const archives = ref([])
const currentArchive = ref(null)
const currentFile = ref(null)

// 对话框控制
const createArchiveDialogVisible = ref(false)
const fileListDialogVisible = ref(false)
const versionHistoryDialogVisible = ref(false)

// 表单数据
const newArchive = ref({
  name: '',
  description: '',
  files: []
})

// 表单引用
const createArchiveForm = ref(null)

// 表单验证规则
const archiveRules = {
  name: [
    { required: true, message: '请输入归档名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入归档描述', trigger: 'blur' }
  ],
  files: [
    { required: true, message: '请选择要归档的文件', trigger: 'change' }
  ]
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    processing: 'info',
    completed: 'success',
    failed: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '等待中',
    processing: '处理中',
    completed: '已完成',
    failed: '失败'
  }
  return texts[status] || status
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取归档列表
    // const response = await archiveStore.getArchives()
    // archives.value = response.data
  } catch (error) {
    handleBusinessError(error)
  } finally {
    loading.value = false
  }
}

// 显示创建归档对话框
const showCreateArchiveDialog = () => {
  newArchive.value = {
    name: '',
    description: '',
    files: []
  }
  createArchiveDialogVisible.value = true
}

// 处理文件变更
const handleFileChange = (file) => {
  // TODO: 处理文件变更
}

// 处理文件移除
const handleFileRemove = (file) => {
  // TODO: 处理文件移除
}

// 创建归档
const handleCreateArchive = async () => {
  if (!createArchiveForm.value) return
  
  try {
    await createArchiveForm.value.validate()
    // TODO: 调用API创建归档
    // await archiveStore.createArchive(newArchive.value)
    ElMessage.success('归档创建成功')
    createArchiveDialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

// 查看文件列表
const handleViewFiles = (archive) => {
  currentArchive.value = archive
  fileListDialogVisible.value = true
}

// 下载归档
const handleDownload = async (archive) => {
  try {
    // TODO: 调用API下载归档
    // await archiveStore.downloadArchive(archive.id)
    ElMessage.success('下载成功')
  } catch (error) {
    handleBusinessError(error)
  }
}

// 删除归档
const handleDelete = async (archive) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除归档"${archive.name}"吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    // TODO: 调用API删除归档
    // await archiveStore.deleteArchive(archive.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

// 查看版本历史
const handleViewVersion = (file) => {
  currentFile.value = file
  versionHistoryDialogVisible.value = true
}

// 下载文件
const handleDownloadFile = async (file) => {
  try {
    // TODO: 调用API下载文件
    // await archiveStore.downloadFile(file.id)
    ElMessage.success('下载成功')
  } catch (error) {
    handleBusinessError(error)
  }
}

// 下载特定版本
const handleDownloadVersion = async (version) => {
  try {
    // TODO: 调用API下载特定版本
    // await archiveStore.downloadVersion(version.id)
    ElMessage.success('下载成功')
  } catch (error) {
    handleBusinessError(error)
  }
}

// 恢复版本
const handleRestoreVersion = async (version) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复到此版本吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    // TODO: 调用API恢复版本
    // await archiveStore.restoreVersion(version.id)
    ElMessage.success('版本恢复成功')
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
.archive-manager {
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

.version-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style> 