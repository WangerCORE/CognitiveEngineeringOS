<template>
  <div class="formatter-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑格式化器' : '新建格式化器' }}</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="form"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入格式化器名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入格式化器描述"
          />
        </el-form-item>

        <el-form-item label="提示词" prop="prompt">
          <el-input
            v-model="form.prompt"
            type="textarea"
            :rows="5"
            placeholder="请输入提示词"
          />
        </el-form-item>

        <el-form-item label="示例" prop="example">
          <el-input
            v-model="form.example"
            type="textarea"
            :rows="5"
            placeholder="请输入示例内容"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="'ENABLED'"
            :inactive-value="'DISABLED'"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="test-card" v-if="isEdit">
      <template #header>
        <div class="card-header">
          <span>测试格式化器</span>
        </div>
      </template>

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
        <el-form-item>
          <el-button type="primary" @click="formatContent">
            格式化
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

const isEdit = computed(() => route.params.id !== undefined)

const form = ref({
  name: '',
  description: '',
  prompt: '',
  example: '',
  status: 'ENABLED'
})

const testForm = ref({
  content: '',
  result: ''
})

const rules = {
  name: [
    { required: true, message: '请输入格式化器名称', trigger: 'blur' },
    { max: 50, message: '名称长度不能超过50个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入格式化器描述', trigger: 'blur' },
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ],
  prompt: [
    { required: true, message: '请输入提示词', trigger: 'blur' }
  ],
  example: [
    { required: true, message: '请输入示例内容', trigger: 'blur' }
  ]
}

const loadFormatter = async () => {
  try {
    const response = await axios.get(`/api/formatters/${route.params.id}`)
    Object.assign(form.value, response.data)
  } catch (error) {
    ElMessage.error('加载格式化器信息失败')
    router.back()
  }
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await axios.put(`/api/formatters/${route.params.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/formatters', form.value)
      ElMessage.success('创建成功')
    }
    router.push('/formatters')
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '保存失败')
    }
  }
}

const formatContent = async () => {
  if (!testForm.value.content) {
    ElMessage.warning('请输入要格式化的内容')
    return
  }

  try {
    const response = await axios.post(`/api/formatters/${route.params.id}/test`, {
      content: testForm.value.content
    })
    testForm.value.result = response.data.formattedContent
  } catch (error) {
    ElMessage.error('格式化失败')
  }
}

onMounted(() => {
  if (isEdit.value) {
    loadFormatter()
  }
})
</script>

<style scoped>
.formatter-edit {
  .form {
    max-width: 800px;
  }

  .test-card {
    margin-top: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style> 