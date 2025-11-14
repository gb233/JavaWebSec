<template>
  <div class="note-detail">
    <div v-if="loading" class="loading">
      <ElSkeleton :rows="5" animated />
    </div>

    <div v-else-if="note" class="note-content">
      <div class="note-header">
        <div class="note-title-section">
          <h1>{{ note.title }}</h1>
          <div class="note-meta">
            <ElTag :type="getNoteTypeColor(note.noteType)">
              {{ getNoteTypeLabel(note.noteType) }}
            </ElTag>
            <ElTag v-if="note.vulnerabilityCode" type="info">
              {{ note.vulnerabilityCode }}
            </ElTag>
            <ElTag v-if="note.isPublic" type="success">
              公开
            </ElTag>
            <ElTag v-if="note.isPinned" type="warning">
              置顶
            </ElTag>
            <span class="note-date">{{ formatDate(note.createdAt) }}</span>
            <span class="note-words">{{ note.wordCount }}字</span>
          </div>
        </div>

        <div class="note-actions">
          <ElButton v-if="canEdit" type="primary" @click="editNote">
            <ElIcon><Edit /></ElIcon>
            编辑
          </ElButton>
          <ElButton v-if="note.isPublic" @click="shareNote">
            <ElIcon><Share /></ElIcon>
            分享
          </ElButton>
          <ElButton v-if="canEdit" @click="togglePin">
            <ElIcon><Star /></ElIcon>
            {{ note.isPinned ? '取消置顶' : '置顶' }}
          </ElButton>
          <ElButton v-if="canEdit" type="danger" @click="deleteNote">
            <ElIcon><Delete /></ElIcon>
            删除
          </ElButton>
        </div>
      </div>

      <div v-if="note.summary" class="note-summary">
        <h3>摘要</h3>
        <p>{{ note.summary }}</p>
      </div>

      <div class="note-body">
        <h3>内容</h3>
        <div class="content-text">
          {{ note.content }}
        </div>
      </div>

      <div v-if="note.tags && parseTags(note.tags).length > 0" class="note-tags">
        <h3>标签</h3>
        <ElTag v-for="tag in parseTags(note.tags)" :key="tag" class="tag-item">
          {{ tag }}
        </ElTag>
      </div>

      <div class="note-stats">
        <div class="stat-item">
          <ElIcon><View /></ElIcon>
          <span>{{ note.viewCount || 0 }} 次浏览</span>
        </div>
        <div class="stat-item">
          <ElIcon><ChatDotRound /></ElIcon>
          <span>{{ note.commentCount || 0 }} 条评论</span>
        </div>
        <div class="stat-item">
          <ElIcon><Star /></ElIcon>
          <span>{{ note.likeCount || 0 }} 个赞</span>
        </div>
        <div class="stat-item">
          <ElIcon><Share /></ElIcon>
          <span>{{ note.shareCount || 0 }} 次分享</span>
        </div>
      </div>
    </div>

    <div v-else class="error-state">
      <ElEmpty description="笔记不存在或已被删除">
        <ElButton type="primary" @click="goBack">
          返回
        </ElButton>
      </ElEmpty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Share, Star, Delete, View, ChatDotRound } from '@element-plus/icons-vue'
import { learningNoteApi, type LearningNote } from '@/api/learningNoteApi'
import { useAuthStore } from '@/stores/modules/auth'
import { isSuccessResponse } from '@/utils/api-helpers'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const note = ref<LearningNote | null>(null)

const canEdit = computed(() => {
  return note.value && authStore.user?.id === note.value.userId
})

const loadNote = async () => {
  try {
    loading.value = true
    const noteId = route.params.id as string
    const response = await learningNoteApi.getNoteById(parseInt(noteId))

    if (isSuccessResponse(response) && response.data) {
      note.value = response.data
    } else {
      ElMessage.error(response.message || '笔记不存在')
    }
  } catch (error) {
    ElMessage.error('加载笔记失败')
  } finally {
    loading.value = false
  }
}

const editNote = () => {
  router.push(`/notes/${note.value?.id}/edit`)
}

const shareNote = () => {
  if (note.value) {
    const url = `${window.location.origin}/notes/${note.value.id}`
    navigator.clipboard.writeText(url).then(() => {
      ElMessage.success('分享链接已复制到剪贴板')
    }).catch(() => {
      ElMessage.info(`分享链接：${url}`)
    })
  }
}

const togglePin = async () => {
  if (!note.value) return

  try {
    const response = await learningNoteApi.updateNote(note.value.id, {
      isPinned: !note.value.isPinned
    })

    if (isSuccessResponse(response)) {
      note.value.isPinned = !note.value.isPinned
      ElMessage.success(note.value.isPinned ? '已置顶' : '已取消置顶')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteNote = async () => {
  if (!note.value) return

  try {
    await ElMessageBox.confirm('确定要删除这篇笔记吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await learningNoteApi.deleteNote(note.value.id)

    if (isSuccessResponse(response)) {
      ElMessage.success('笔记已删除')
      router.push('/profile/notes')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
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

const getNoteTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    PERSONAL: 'primary',
    SHARED: 'success',
    STUDY: 'info',
    EXPERIMENT: 'warning'
  }
  return colorMap[type] || 'primary'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const parseTags = (tagsString: string) => {
  try {
    return JSON.parse(tagsString)
  } catch {
    return []
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadNote()
})
</script>

<style scoped>
.note-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.note-title-section h1 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 28px;
  line-height: 1.4;
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.note-meta .el-tag {
  margin-right: 8px;
}

.note-date,
.note-words {
  color: #909399;
  font-size: 14px;
}

.note-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.note-summary,
.note-body,
.note-tags {
  margin-bottom: 24px;
}

.note-summary h3,
.note-body h3,
.note-tags h3 {
  color: #606266;
  margin-bottom: 12px;
  font-size: 16px;
}

.note-summary p {
  color: #606266;
  line-height: 1.6;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  margin: 0;
}

.content-text {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #303133;
  font-size: 15px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 4px;
}

.note-stats {
  display: flex;
  gap: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-top: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.loading {
  padding: 40px;
}

.error-state {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .note-header {
    flex-direction: column;
    gap: 16px;
  }

  .note-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .note-stats {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
