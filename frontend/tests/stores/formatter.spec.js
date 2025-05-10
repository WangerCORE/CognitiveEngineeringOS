import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useFormatterStore } from '@/stores/formatter'

describe('Formatter Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('应该正确初始化状态', () => {
    const store = useFormatterStore()
    expect(store.formatters).toEqual([])
    expect(store.currentFormatter).toBeNull()
    expect(store.loading).toBe(false)
    expect(store.stats).toBeNull()
    expect(store.testResult).toBeNull()
    expect(store.total).toBe(0)
  })

  it('应该获取格式化器列表', async () => {
    const store = useFormatterStore()
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

    store.getFormatters = vi.fn().mockResolvedValue(mockData)
    const result = await store.getFormatters({ page: 1, size: 10 })

    expect(store.getFormatters).toHaveBeenCalledWith({ page: 1, size: 10 })
    expect(result).toEqual(mockData)
  })

  it('应该获取单个格式化器', async () => {
    const store = useFormatterStore()
    const mockData = {
      id: 1,
      name: '测试格式化器',
      description: '测试描述',
      status: 'enabled'
    }

    store.fetchFormatter = vi.fn().mockResolvedValue(mockData)
    const result = await store.fetchFormatter(1)

    expect(store.fetchFormatter).toHaveBeenCalledWith(1)
    expect(result).toEqual(mockData)
    expect(store.currentFormatter).toEqual(mockData)
  })

  it('应该创建格式化器', async () => {
    const store = useFormatterStore()
    const formData = {
      name: '新格式化器',
      description: '新描述',
      prompt: '新提示词',
      example: '新示例'
    }

    store.createFormatter = vi.fn().mockResolvedValue({ id: 1, ...formData })
    const result = await store.createFormatter(formData)

    expect(store.createFormatter).toHaveBeenCalledWith(formData)
    expect(result).toEqual({ id: 1, ...formData })
  })

  it('应该更新格式化器', async () => {
    const store = useFormatterStore()
    const formData = {
      id: 1,
      name: '更新格式化器',
      description: '更新描述',
      prompt: '更新提示词',
      example: '更新示例'
    }

    store.updateFormatter = vi.fn().mockResolvedValue(formData)
    const result = await store.updateFormatter(formData)

    expect(store.updateFormatter).toHaveBeenCalledWith(formData)
    expect(result).toEqual(formData)
  })

  it('应该删除格式化器', async () => {
    const store = useFormatterStore()
    store.deleteFormatter = vi.fn().mockResolvedValue({ success: true })
    const result = await store.deleteFormatter(1)

    expect(store.deleteFormatter).toHaveBeenCalledWith(1)
    expect(result).toEqual({ success: true })
  })

  it('应该批量删除格式化器', async () => {
    const store = useFormatterStore()
    store.batchDeleteFormatters = vi.fn().mockResolvedValue({ success: true })
    const result = await store.batchDeleteFormatters([1, 2])

    expect(store.batchDeleteFormatters).toHaveBeenCalledWith([1, 2])
    expect(result).toEqual({ success: true })
  })

  it('应该更新格式化器状态', async () => {
    const store = useFormatterStore()
    store.updateFormatterStatus = vi.fn().mockResolvedValue({ success: true })
    const result = await store.updateFormatterStatus(1, 'disabled')

    expect(store.updateFormatterStatus).toHaveBeenCalledWith(1, 'disabled')
    expect(result).toEqual({ success: true })
  })

  it('应该测试格式化器', async () => {
    const store = useFormatterStore()
    const testResult = {
      success: true,
      message: '测试成功',
      formattedContent: '<p>测试内容</p>'
    }

    store.testFormatter = vi.fn().mockResolvedValue(testResult)
    const result = await store.testFormatter(1)

    expect(store.testFormatter).toHaveBeenCalledWith(1)
    expect(result).toEqual(testResult)
  })

  it('应该获取格式化器统计', async () => {
    const store = useFormatterStore()
    const mockStats = {
      total: 10,
      active: 8,
      disabled: 2,
      usageCount: 100
    }

    store.fetchStats = vi.fn().mockResolvedValue(mockStats)
    const result = await store.fetchStats()

    expect(store.fetchStats).toHaveBeenCalled()
    expect(result).toEqual(mockStats)
    expect(store.stats).toEqual(mockStats)
  })

  it('应该正确计算格式化器分类', () => {
    const store = useFormatterStore()
    store.formatters = [
      { id: 1, active: true, type: 'markdown' },
      { id: 2, active: false, type: 'html' },
      { id: 3, active: true, type: 'text' }
    ]

    expect(store.activeFormatters).toHaveLength(2)
    expect(store.inactiveFormatters).toHaveLength(1)
    expect(store.markdownFormatters).toHaveLength(1)
    expect(store.htmlFormatters).toHaveLength(1)
    expect(store.textFormatters).toHaveLength(1)
  })
}) 