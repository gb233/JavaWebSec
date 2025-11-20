<template>
  <div class="collection-create">
    <div class="page-header">
      <h1>创建新收藏夹</h1>
      <p>整理您收藏的漏洞学习内容和学习资源</p>
    </div>

    <ElCard>
      <ElForm
        ref="formRef"
        :model="collectionForm"
        :rules="rules"
        label-width="100px"
      >
        <ElFormItem label="收藏夹名称" prop="name">
          <ElInput
            v-model="collectionForm.name"
            placeholder="请输入收藏夹名称"
            maxlength="100"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="收藏夹描述" prop="description">
          <ElInput
            v-model="collectionForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入收藏夹描述"
            maxlength="500"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="标签">
          <ElInput
            v-model="tagsInput"
            placeholder="请输入标签，用逗号分隔"
            @blur="parseTags"
          />
          <div v-if="collectionForm.tags.length > 0" class="tags-preview">
            <ElTag
              v-for="tag in collectionForm.tags"
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
            v-model="collectionForm.isPublic"
            active-text="公开收藏夹"
            inactive-text="私人收藏夹"
          />
          <div class="setting-tip">
            <ElText type="info" size="small">
              公开收藏夹可以被其他用户查看和搜索
            </ElText>
          </div>
        </ElFormItem>

        <ElFormItem label="默认收藏夹">
          <ElSwitch
            v-model="collectionForm.isDefault"
            active-text="设为默认收藏夹"
            inactive-text="普通收藏夹"
          />
          <div class="setting-tip">
            <ElText type="info" size="small">
              默认收藏夹将作为快速收藏的默认目标
            </ElText>
          </div>
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" :loading="saving" @click="saveCollection">
            创建收藏夹
          </ElButton>
          <ElButton :loading="previewing" @click="previewCollection">
            预览收藏夹
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
      title="收藏夹预览"
      width="60%"
      top="5vh"
    >
      <div class="collection-preview">
        <h2>{{ collectionForm.name }}</h2>
        <div class="collection-meta">
          <ElTag v-if="collectionForm.isPublic" type="success">
            公开
          </ElTag>
          <ElTag v-if="collectionForm.isDefault" type="warning">
            默认
          </ElTag>
        </div>
        <div v-if="collectionForm.description" class="collection-description">
          <h4>描述</h4>
          <p>{{ collectionForm.description }}</p>
        </div>
        <div v-if="collectionForm.tags.length > 0" class="collection-tags">
          <h4>标签</h4>
          <ElTag v-for="tag in collectionForm.tags" :key="tag" class="tag-item">
            {{ tag }}
          </ElTag>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { collectionApi } from '@/api/collectionApi'
import { isSuccessResponse } from '@/utils/api-helpers'

const router = useRouter()

const formRef = ref()
const saving = ref(false)
const previewing = ref(false)
const showPreview = ref(false)
const tagsInput = ref('')

const collectionForm = reactive({
  name: '',
  description: '',
  tags: [] as string[],
  isPublic: false,
  isDefault: false
})

const rules = {
  name: [
    { required: true, message: '请输入收藏夹名称', trigger: 'blur' },
    { min: 1, max: 100, message: '名称长度在1到100个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ]
}

const parseTags = () => {
  if (tagsInput.value.trim()) {
    const tags = tagsInput.value.split(',').map(tag => tag.trim()).filter(tag => tag)
    collectionForm.tags = [...new Set([...collectionForm.tags, ...tags])]
    tagsInput.value = ''
  }
}

const removeTag = (tag: string) => {
  collectionForm.tags = collectionForm.tags.filter(t => t !== tag)
}

const saveCollection = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true

    const response = await collectionApi.createCollection({
      name: collectionForm.name,
      description: collectionForm.description,
      tags: JSON.stringify(collectionForm.tags),
      isPublic: collectionForm.isPublic,
      isDefault: collectionForm.isDefault
    })

    if (isSuccessResponse(response)) {
      ElMessage.success('收藏夹创建成功')
      router.push('/profile/collections')
    } else {
      ElMessage.error(response.message || '收藏夹创建失败')
    }
  } catch (error) {
    ElMessage.error('收藏夹创建失败')
  } finally {
    saving.value = false
  }
}

const previewCollection = async () => {
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
  router.push('/profile/collections')
}
</script>

<style scoped>
.collection-create {
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

.setting-tip {
  margin-top: 4px;
}

.collection-preview h2 {
  color: #303133;
  margin-bottom: 16px;
}

.collection-meta {
  margin-bottom: 16px;
}

.collection-meta .el-tag {
  margin-right: 8px;
}

.collection-description,
.collection-tags {
  margin-bottom: 20px;
}

.collection-description h4,
.collection-tags h4 {
  color: #606266;
  margin-bottom: 8px;
}

.collection-description p {
  color: #303133;
  line-height: 1.6;
  margin: 0;
}
</style>
