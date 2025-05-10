import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import FormatterManager from '@/views/FormatterManager.vue'
import { useFormatterStore } from '@/stores/formatter'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟 Element Plus 组件
vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
    success: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn()
  }
}))

describe('FormatterManager.vue', () => {
  let wrapper
  let store

  beforeEach(() => {
    wrapper = mount(FormatterManager, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn
          })
        ],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-tag': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-card': true
        }
      }
    })
    store = useFormatterStore()
  })

  it('应该正确渲染页面标题', () => {
    expect(wrapper.find('.formatter-manager').exists()).toBe(true)
  })

  it('应该加载格式化器列表', async () => {
    const mockData = {
      data: [
        {
          id: 1,
          name: '测试格式化器',
          description: '测试描述',
          status: 'enabled',
          lastUsedTime: '2025-05-04 10:00:00',
          usageCount: 10
        }
      ],
      total: 1
    }

    store.getFormatters.mockResolvedValue(mockData)
    await wrapper.vm.loadData()

    expect(store.getFormatters).toHaveBeenCalled()
    expect(wrapper.vm.formatters).toEqual(mockData.data)
    expect(wrapper.vm.total).toBe(mockData.total)
  })

  it('应该处理搜索', async () => {
    const searchForm = {
      keyword: '测试',
      status: 'enabled'
    }

    wrapper.vm.searchForm.keyword = searchForm.keyword
    wrapper.vm.searchForm.status = searchForm.status
    await wrapper.vm.handleSearch()

    expect(store.getFormatters).toHaveBeenCalledWith({
      page: 1,
      size: 10,
      ...searchForm
    })
  })

  it('应该处理添加格式化器', async () => {
    const formData = {
      name: '新格式化器',
      description: '新描述',
      prompt: '新提示词',
      example: '新示例'
    }

    store.createFormatter.mockResolvedValue({ id: 1, ...formData })
    wrapper.vm.form = formData
    await wrapper.vm.handleSubmit()

    expect(store.createFormatter).toHaveBeenCalledWith(formData)
    expect(ElMessage.success).toHaveBeenCalledWith('添加成功')
  })

  it('应该处理编辑格式化器', async () => {
    const formData = {
      id: 1,
      name: '更新格式化器',
      description: '更新描述',
      prompt: '更新提示词',
      example: '更新示例'
    }

    store.updateFormatter.mockResolvedValue(formData)
    wrapper.vm.form = formData
    wrapper.vm.dialogType = 'edit'
    await wrapper.vm.handleSubmit()

    expect(store.updateFormatter).toHaveBeenCalledWith(formData)
    expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
  })

  it('应该处理删除格式化器', async () => {
    ElMessageBox.confirm.mockResolvedValue()
    store.deleteFormatter.mockResolvedValue()

    await wrapper.vm.handleDelete({ id: 1 })

    expect(ElMessageBox.confirm).toHaveBeenCalled()
    expect(store.deleteFormatter).toHaveBeenCalledWith(1)
    expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
  })

  it('应该处理批量删除', async () => {
    ElMessageBox.confirm.mockResolvedValue()
    store.batchDeleteFormatters.mockResolvedValue()

    wrapper.vm.selectedFormatters = [
      { id: 1 },
      { id: 2 }
    ]

    await wrapper.vm.handleBatchDelete()

    expect(ElMessageBox.confirm).toHaveBeenCalled()
    expect(store.batchDeleteFormatters).toHaveBeenCalledWith([1, 2])
    expect(ElMessage.success).toHaveBeenCalledWith('批量删除成功')
  })

  it('应该处理状态切换', async () => {
    store.updateFormatterStatus.mockResolvedValue()

    await wrapper.vm.handleToggleStatus({
      id: 1,
      status: 'enabled'
    })

    expect(store.updateFormatterStatus).toHaveBeenCalledWith(1, 'disabled')
    expect(ElMessage.success).toHaveBeenCalledWith('状态更新成功')
  })

  it('应该处理测试格式化器', async () => {
    const testResult = {
      success: true,
      message: '测试成功',
      formattedContent: '<p>测试内容</p>'
    }

    store.testFormatter.mockResolvedValue(testResult)
    await wrapper.vm.handleTest({ id: 1 })

    expect(store.testFormatter).toHaveBeenCalledWith(1)
    expect(wrapper.vm.testResult).toEqual(testResult)
  })
}) 