<template>
  <div class="classifier-manager">
    <div class="classifier-manager__header">
      <h1>AI分类器管理</h1>
      <div class="classifier-manager__actions">
        <el-button type="primary" @click="showAddTagDialog">
          添加标签
        </el-button>
      </div>
    </div>

    <el-card class="classifier-manager__settings">
      <template #header>
        <div class="card-header">
          <span>分类器设置</span>
        </div>
      </template>
      <div class="settings-content">
        <div class="setting-item">
          <span class="label">置信度阈值：</span>
          <el-slider
            v-model="confidenceThreshold"
            :min="0"
            :max="1"
            :step="0.1"
            :format-tooltip="formatConfidence"
            @change="handleThresholdChange"
          />
        </div>
      </div>
    </el-card>

    <el-card class="classifier-manager__tags">
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="tags"
        style="width: 100%"
      >
        <el-table-column prop="name" label="标签名称" />
        <el-table-column prop="count" label="使用次数" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteTag(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="addTagDialogVisible"
      title="添加标签"
      width="30%"
    >
      <el-form
        ref="addTagForm"
        :model="newTag"
        :rules="tagRules"
        label-width="80px"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="newTag.name" placeholder="请输入标签名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addTagDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddTag">
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
import { useClassifierStore } from '@/stores/classifier'
import { formatDate } from '@/utils/date'
import { handleBusinessError } from '@/utils/errorHandler'

const classifierStore = useClassifierStore()
const loading = ref(false)
const tags = ref([])
const confidenceThreshold = ref(0.6)
const addTagDialogVisible = ref(false)
const newTag = ref({ name: '' })
const addTagForm = ref(null)

const tagRules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

const formatConfidence = (val) => {
  return `${(val * 100).toFixed(0)}%`
}

const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      classifierStore.getTags(),
      classifierStore.getSettings()
    ])
    tags.value = classifierStore.tags
    confidenceThreshold.value = classifierStore.confidenceThreshold
  } catch (error) {
    handleBusinessError(error)
  } finally {
    loading.value = false
  }
}

const showAddTagDialog = () => {
  newTag.value = { name: '' }
  addTagDialogVisible.value = true
}

const handleAddTag = async () => {
  if (!addTagForm.value) return
  
  try {
    await addTagForm.value.validate()
    await classifierStore.addTag(newTag.value.name)
    ElMessage.success('添加标签成功')
    addTagDialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

const handleDeleteTag = async (tag) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除标签"${tag.name}"吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    await classifierStore.deleteTag(tag.id)
    ElMessage.success('删除标签成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

const handleThresholdChange = async (value) => {
  try {
    await classifierStore.updateConfidenceThreshold(value)
    ElMessage.success('更新置信度阈值成功')
  } catch (error) {
    handleBusinessError(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.classifier-manager {
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

  &__settings {
    margin-bottom: 20px;
  }

  &__tags {
    margin-bottom: 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.settings-content {
  .setting-item {
    display: flex;
    align-items: center;
    gap: 20px;

    .label {
      min-width: 100px;
    }
  }
}
</style> 