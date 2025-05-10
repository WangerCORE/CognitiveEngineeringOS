<template>
  <div class="entry-list">
    <div class="entry-list__header">
      <h1>RSS条目</h1>
      <div class="entry-list__filters">
        <el-input
          v-model="query"
          placeholder="搜索标题或描述"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="sourceId" placeholder="选择RSS源" clearable @change="handleSearch">
          <el-option
            v-for="source in sources"
            :key="source.id"
            :label="source.name"
            :value="source.id"
          />
        </el-select>
        <el-select v-model="isRead" placeholder="阅读状态" clearable @change="handleSearch">
          <el-option label="未读" :value="false" />
          <el-option label="已读" :value="true" />
        </el-select>
        <el-select v-model="isStarred" placeholder="收藏状态" clearable @change="handleSearch">
          <el-option label="未收藏" :value="false" />
          <el-option label="已收藏" :value="true" />
        </el-select>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="entries"
      style="width: 100%"
      @row-click="handleRowClick"
    >
      <el-table-column prop="title" label="标题" min-width="300">
        <template #default="{ row }">
          <div class="entry-title">
            <el-icon v-if="!row.isRead" class="unread-icon"><Circle /></el-icon>
            <el-icon v-if="row.isStarred" class="starred-icon"><Star /></el-icon>
            <span>{{ row.title }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="sourceName" label="来源" width="150" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="pubDate" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.pubDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button
              :type="row.isRead ? 'info' : 'primary'"
              size="small"
              @click.stop="handleToggleRead(row)"
            >
              {{ row.isRead ? '标记未读' : '标记已读' }}
            </el-button>
            <el-button
              :type="row.isStarred ? 'warning' : 'default'"
              size="small"
              @click.stop="handleToggleStar(row)"
            >
              {{ row.isStarred ? '取消收藏' : '收藏' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click.stop="handleDelete(row)"
            >
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <div class="entry-list__pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="currentEntry?.title"
      width="80%"
      destroy-on-close
    >
      <div class="entry-detail">
        <div class="entry-detail__meta">
          <span>来源：{{ currentEntry?.sourceName }}</span>
          <span>作者：{{ currentEntry?.author || '未知' }}</span>
          <span>发布时间：{{ formatDate(currentEntry?.pubDate) }}</span>
        </div>
        <div class="entry-detail__content" v-html="currentEntry?.content"></div>
        <div class="entry-detail__actions">
          <el-button type="primary" @click="openInNewTab(currentEntry?.link)">
            查看原文
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Circle, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useEntryStore } from '@/stores/entry'
import { useSourceStore } from '@/stores/source'
import { formatDate } from '@/utils/date'
import { handleBusinessError } from '@/utils/errorHandler'

const entryStore = useEntryStore()
const sourceStore = useSourceStore()

const loading = ref(false)
const entries = ref([])
const sources = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const query = ref('')
const sourceId = ref(null)
const isRead = ref(null)
const isStarred = ref(null)
const dialogVisible = ref(false)
const currentEntry = ref(null)

const loadEntries = async () => {
  loading.value = true
  try {
    const result = await entryStore.getEntries({
      page: currentPage.value - 1,
      size: pageSize.value,
      query: query.value,
      sourceId: sourceId.value,
      isRead: isRead.value,
      isStarred: isStarred.value
    })
    entries.value = result.content
    total.value = result.totalElements
  } catch (error) {
    handleBusinessError(error)
  } finally {
    loading.value = false
  }
}

const loadSources = async () => {
  try {
    const result = await sourceStore.getSources()
    sources.value = result.content
  } catch (error) {
    handleBusinessError(error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadEntries()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadEntries()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadEntries()
}

const handleRowClick = (row) => {
  currentEntry.value = row
  dialogVisible.value = true
  if (!row.isRead) {
    handleToggleRead(row)
  }
}

const handleToggleRead = async (row) => {
  try {
    if (row.isRead) {
      await entryStore.markAsUnread(row.id)
    } else {
      await entryStore.markAsRead(row.id)
    }
    row.isRead = !row.isRead
    ElMessage.success(row.isRead ? '已标记为已读' : '已标记为未读')
  } catch (error) {
    handleBusinessError(error)
  }
}

const handleToggleStar = async (row) => {
  try {
    await entryStore.toggleStar(row.id)
    row.isStarred = !row.isStarred
    ElMessage.success(row.isStarred ? '已收藏' : '已取消收藏')
  } catch (error) {
    handleBusinessError(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条RSS条目吗？', '提示', {
      type: 'warning'
    })
    await entryStore.deleteEntry(row.id)
    ElMessage.success('删除成功')
    loadEntries()
  } catch (error) {
    if (error !== 'cancel') {
      handleBusinessError(error)
    }
  }
}

const openInNewTab = (url) => {
  window.open(url, '_blank')
}

onMounted(() => {
  loadEntries()
  loadSources()
})
</script>

<style lang="scss" scoped>
.entry-list {
  padding: 20px;

  &__header {
    margin-bottom: 20px;

    h1 {
      margin-bottom: 20px;
    }
  }

  &__filters {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }

  &__pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.entry-title {
  display: flex;
  align-items: center;
  gap: 8px;

  .unread-icon {
    color: var(--el-color-primary);
  }

  .starred-icon {
    color: var(--el-color-warning);
  }
}

.entry-detail {
  &__meta {
    margin-bottom: 20px;
    color: var(--el-text-color-secondary);
    display: flex;
    gap: 20px;
  }

  &__content {
    margin-bottom: 20px;
    line-height: 1.6;
  }

  &__actions {
    display: flex;
    justify-content: flex-end;
  }
}
</style> 