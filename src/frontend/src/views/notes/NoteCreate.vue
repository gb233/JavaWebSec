<template>
  <div class="note-create">
    <div class="page-header">
      <h1>创建新笔记</h1>
      <p>记录您的学习心得和重要发现</p>
    </div>

    <ElCard>
      <ElForm
        ref="formRef"
        :model="noteForm"
        :rules="rules"
        label-width="100px"
      >
        <ElFormItem label="笔记标题" prop="title">
          <ElInput
            v-model="noteForm.title"
            placeholder="请输入笔记标题"
            maxlength="100"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="笔记类型" prop="noteType">
          <ElSelect v-model="noteForm.noteType" placeholder="请选择笔记类型">
            <ElOption label="个人笔记" value="PERSONAL" />
            <ElOption label="分享笔记" value="SHARED" />
            <ElOption label="学习笔记" value="STUDY" />
            <ElOption label="实验记录" value="EXPERIMENT" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="漏洞类型" prop="vulnerabilityCode">
          <ElSelect v-model="noteForm.vulnerabilityCode" placeholder="请选择相关漏洞类型" clearable>
            <ElOption label="A01 - 失效的访问控制" value="A01" />
            <ElOption label="A02 - 加密失败" value="A02" />
            <ElOption label="A03 - 注入" value="A03" />
            <ElOption label="A04 - 不安全设计" value="A04" />
            <ElOption label="A05 - 安全配置错误" value="A05" />
            <ElOption label="A06 - 易受攻击组件" value="A06" />
            <ElOption label="A07 - 身份认证失败" value="A07" />
            <ElOption label="A08 - 软件和数据完整性失败" value="A08" />
            <ElOption label="A09 - 安全日志记录和监控失败" value="A09" />
            <ElOption label="A10 - 服务器端请求伪造" value="A10" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="笔记摘要" prop="summary">
          <ElInput
            v-model="noteForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入笔记摘要"
            maxlength="200"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="笔记内容" prop="content">
          <ElInput
            v-model="noteForm.content"
            type="textarea"
            :rows="15"
            placeholder="请输入笔记内容"
            maxlength="10000"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="标签">
          <ElInput
            v-model="tagsInput"
            placeholder="请输入标签，用逗号分隔"
            @blur="parseTags"
          />
          <div v-if="noteForm.tags.length > 0" class="tags-preview">
            <ElTag
              v-for="tag in noteForm.tags"
              :key="tag"
              closable
              class="tag-item"
              @close="removeTag(tag)"
            >
              {{ tag }}
            </ElTag>
          </div>
        </ElFormItem>

        <ElFormItem label="公开设置">
          <ElSwitch
            v-model="noteForm.isPublic"
            active-text="公开笔记"
            inactive-text="私人笔记"
          />
        </ElFormItem>

        <ElFormItem label="置顶设置">
          <ElSwitch
            v-model="noteForm.isPinned"
            active-text="置顶笔记"
            inactive-text="普通笔记"
          />
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" :loading="saving" @click="saveNote">
            保存笔记
          </ElButton>
          <ElButton :loading="previewing" @click="previewNote">
            预览笔记
          </ElButton>
          <ElButton @click="cancelCreate">
            取消
          </ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <!-- 预览对话框 -->
    <ElDialog
      v-model="showPreview"
      title="笔记预览"
      width="80%"
      top="5vh"
    >
      <div class="note-preview">
        <h2>{{ noteForm.title }}</h2>
        <div class="note-meta">
          <ElTag :type="getNoteTypeColor(noteForm.noteType)">
            {{ getNoteTypeLabel(noteForm.noteType) }}
          </ElTag>
          <ElTag v-if="noteForm.vulnerabilityCode" type="info">
            {{ noteForm.vulnerabilityCode }}
          </ElTag>
          <ElTag v-if="noteForm.isPublic" type="success">
            公开
          </ElTag>
          <ElTag v-if="noteForm.isPinned" type="warning">
            置顶
          </ElTag>
        </div>
        <div v-if="noteForm.summary" class="note-summary">
          <h4>摘要</h4>
          <p>{{ noteForm.summary }}</p>
        </div>
        <div class="note-content">
          <h4>内容</h4>
          <div class="content-text">
            {{ noteForm.content }}
          </div>
        </div>
        <div v-if="noteForm.tags.length > 0" class="note-tags">
          <h4>标签</h4>
          <ElTag v-for="tag in noteForm.tags" :key="tag" class="tag-item">
            {{ tag }}
          </ElTag>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { learningNoteApi } from '@/api/learningNoteApi'
import { isSuccessResponse } from '@/utils/api-helpers'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const saving = ref(false)
const previewing = ref(false)
const showPreview = ref(false)
const tagsInput = ref('')

const noteForm = reactive({
  title: '',
  noteType: 'PERSONAL',
  vulnerabilityCode: '',
  summary: '',
  content: '',
  tags: [] as string[],
  isPublic: false,
  isPinned: false
})

const rules = {
  title: [
    { required: true, message: '请输入笔记标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在1到100个字符', trigger: 'blur' }
  ],
  noteType: [
    { required: true, message: '请选择笔记类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入笔记内容', trigger: 'blur' },
    { min: 10, max: 10000, message: '内容长度在10到10000个字符', trigger: 'blur' }
  ]
}

const parseTags = () => {
  if (tagsInput.value.trim()) {
    const tags = tagsInput.value.split(',').map(tag => tag.trim()).filter(tag => tag)
    noteForm.tags = [...new Set([...noteForm.tags, ...tags])]
    tagsInput.value = ''
  }
}

const removeTag = (tag: string) => {
  noteForm.tags = noteForm.tags.filter(t => t !== tag)
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

const saveNote = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true

    const response = await learningNoteApi.createNote({
      userId: 0, // 后端会自动从认证信息中获取用户ID
      title: noteForm.title,
      noteType: noteForm.noteType,
      vulnerabilityCode: noteForm.vulnerabilityCode || null,
      summary: noteForm.summary,
      content: noteForm.content,
      tags: JSON.stringify(noteForm.tags),
      isPublic: noteForm.isPublic,
      isPinned: noteForm.isPinned
    })

    if (isSuccessResponse(response)) {
      ElMessage.success('笔记创建成功')
      router.push('/profile/notes')
    } else {
      ElMessage.error(response.message || '笔记创建失败')
    }
  } catch (error) {
    ElMessage.error('笔记创建失败')
  } finally {
    saving.value = false
  }
}

const previewNote = async () => {
  try {
    await formRef.value?.validate()
    previewing.value = true
    showPreview.value = true
  } catch (error) {
    ElMessage.error('请完善必填信息后再预览')
  } finally {
    previewing.value = false
  }
}

const cancelCreate = () => {
  router.push('/profile/notes')
}

// 从路由参数预填充漏洞代码
onMounted(() => {
  const vulnerabilityCode = route.query.vulnerabilityCode as string
  if (vulnerabilityCode) {
    noteForm.vulnerabilityCode = vulnerabilityCode
    // 如果标题为空，自动填充标题
    if (!noteForm.title) {
      noteForm.title = `${vulnerabilityCode} - 学习笔记`
    }
  }
})
</script>

<style scoped>
.note-create {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #606266;
}

.tags-preview {
  margin-top: 8px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 4px;
}

.note-preview h2 {
  color: #303133;
  margin-bottom: 16px;
}

.note-meta {
  margin-bottom: 16px;
}

.note-meta .el-tag {
  margin-right: 8px;
}

.note-summary,
.note-content,
.note-tags {
  margin-bottom: 20px;
}

.note-summary h4,
.note-content h4,
.note-tags h4 {
  color: #606266;
  margin-bottom: 8px;
}

.content-text {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #303133;
}
</style>
