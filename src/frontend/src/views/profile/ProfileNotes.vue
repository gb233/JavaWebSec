<template>
  <div class="profile-notes">
    <div class="page-header">
      <h1>学习笔记</h1>
      <p>记录和整理您的学习心得，支持标签分类和搜索功能</p>
    </div>

    <div class="notes-stats">
      <ElRow :gutter="20">
        <ElCol :span="6">
          <ElCard class="stat-card">
            <div class="stat-content">
              <div class="stat-number">
                {{ notes.length }}
              </div>
              <div class="stat-label">
                总笔记数
              </div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard class="stat-card">
            <div class="stat-content">
              <div class="stat-number">
                {{ publicNotes.length }}
              </div>
              <div class="stat-label">
                公开笔记
              </div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard class="stat-card">
            <div class="stat-content">
              <div class="stat-number">
                {{ pinnedNotes.length }}
              </div>
              <div class="stat-label">
                置顶笔记
              </div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard class="stat-card">
            <div class="stat-content">
              <div class="stat-number">
                {{ totalWords }}
              </div>
              <div class="stat-label">
                总字数
              </div>
            </div>
          </ElCard>
        </ElCol>
      </ElRow>
    </div>

    <div class="notes-list">
      <ElCard>
        <template #header>
          <div class="card-header">
            <span>我的笔记</span>
            <ElButton type="primary" @click="createNote">
              <ElIcon><Plus /></ElIcon>
              新建笔记
            </ElButton>
          </div>
        </template>

        <div v-if="loading" class="loading">
          <ElSkeleton :rows="3" animated />
        </div>

        <div v-else-if="notes.length === 0" class="empty-state">
          <ElEmpty description="暂无笔记">
            <ElButton type="primary" @click="createNote">
              创建第一篇笔记
            </ElButton>
          </ElEmpty>
        </div>

        <div v-else class="notes-grid">
          <div v-for="note in notes" :key="note.id" class="note-item">
            <ElCard class="note-card" @click="viewNote(note.id)">
              <div class="note-header">
                <h3 class="note-title">
                  {{ note.title }}
                </h3>
                <div class="note-actions">
                  <ElTag v-if="note.isPinned" type="warning" size="small">
                    置顶
                  </ElTag>
                  <ElTag v-if="note.isPublic" type="success" size="small">
                    公开
                  </ElTag>
                </div>
              </div>
              <p class="note-summary">
                {{ note.summary || '暂无摘要' }}
              </p>
              <div class="note-meta">
                <span class="note-type">{{ getNoteTypeLabel(note.noteType) }}</span>
                <span class="note-date">{{ formatDate(note.createdAt) }}</span>
                <span class="note-words">{{ note.wordCount }}字</span>
              </div>
              <div v-if="note.tags" class="note-tags">
                <ElTag
                  v-for="tag in parseTags(note.tags)"
                  :key="tag"
                  size="small"
                  class="note-tag"
                >
                  {{ tag }}
                </ElTag>
              </div>
            </ElCard>
          </div>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyNotes, type LearningNote } from '@/api/learningNoteApi'

const router = useRouter()

const loading = ref(false)
const notes = ref<LearningNote[]>([])

const publicNotes = computed(() => notes.value.filter(note => note.isPublic))
const pinnedNotes = computed(() => notes.value.filter(note => note.isPinned))
const totalWords = computed(() => notes.value.reduce((sum, note) => sum + (note.wordCount || 0), 0))

const loadNotes = async () => {
  try {
    loading.value = true
    const response = await getMyNotes()
    if (response.success) {
      notes.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载笔记失败')
  } finally {
    loading.value = false
  }
}

const createNote = () => {
  // 跳转到笔记创建页面
  router.push('/notes/create')
}

const viewNote = (id: number) => {
  // 跳转到笔记详情页面
  router.push(`/notes/${id}`)
}

const getNoteTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    PERSONAL: '个人笔记',
    SHARED: '分享笔记',
    STUDY: '学习笔记',
    EXPERIMENT: '实验记录'
  }
  return typeMap[type] || type
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const parseTags = (tagsString: string) => {
  try {
    return JSON.parse(tagsString)
  } catch {
    return []
  }
}

onMounted(() => {
  loadNotes()
})
</script>

<style scoped>
.profile-notes {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #606266;
}

.notes-stats {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 16px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading {
  padding: 20px;
}

.empty-state {
  padding: 40px 20px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.note-card {
  cursor: pointer;
  transition: all 0.3s;
}

.note-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.note-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 8px;
}

.note-actions {
  display: flex;
  gap: 4px;
}

.note-summary {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.note-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.note-tag {
  font-size: 12px;
}
</style>
