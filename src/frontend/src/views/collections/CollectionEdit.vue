<template>
  <div class="collection-edit">
    <div class="page-header">
      <h1>编辑收藏夹</h1>
      <p>修改您的收藏夹信息</p>
    </div>

    <div v-if="loading" class="loading">
      <ElSkeleton :rows="5" animated />
    </div>

    <ElCard v-else-if="collection">
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
            @blur="parseTagsInput"
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
            保存修改
          </ElButton>
          <ElButton :loading="previewing" @click="previewCollection">
            预览收藏夹
          </ElButton>
          <ElButton @click="cancelEdit">
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { collectionApi, type Collection } from '@/api/collectionApi'
import { isSuccessResponse } from '@/utils/api-helpers'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const previewing = ref(false)
const showPreview = ref(false)
const tagsInput = ref('')
const collection = ref<Collection | null>(null)

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

const loadCollection = async () => {
  try {
    loading.value = true
    const collectionId = route.params.id as string
    const response = await collectionApi.getCollectionById(parseInt(collectionId))

    if (isSuccessResponse(response) && response.data) {
      collection.value = response.data
      // 填充表单
      Object.assign(collectionForm, {
        name: collection.value.name,
        description: collection.value.description || '',
        tags: parseTags(collection.value.tags || '[]'),
        isPublic: collection.value.isPublic,
        isDefault: collection.value.isDefault
      })
    } else {
      ElMessage.error(response.message || '收藏夹不存在')
      router.push('/profile/collections')
    }
  } catch (error) {
    ElMessage.error('加载收藏夹失败')
    router.push('/profile/collections')
  } finally {
    loading.value = false
  }
}

const parseTags = (tagsString: string) => {
  try {
    return JSON.parse(tagsString)
  } catch {
    return []
  }
}

const parseTagsInput = () => {
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

    const response = await collectionApi.updateCollection(collection.value!.id!, {
      name: collectionForm.name,
      description: collectionForm.description,
      tags: JSON.stringify(collectionForm.tags),
      isPublic: collectionForm.isPublic,
      isDefault: collectionForm.isDefault
    })

    if (isSuccessResponse(response)) {
      ElMessage.success('收藏夹修改成功')
      router.push(`/collections/${collection.value!.id}`)
    } else {
      ElMessage.error(response.message || '收藏夹修改失败')
    }
  } catch (error) {
    ElMessage.error('收藏夹修改失败')
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

const cancelEdit = () => {
  router.push(`/collections/${collection.value?.id}`)
}

onMounted(() => {
  loadCollection()
})
</script>

<style scoped>
.collection-edit {
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

.loading {
  padding: 40px;
}
</style>
