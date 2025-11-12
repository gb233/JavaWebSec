<template>
  <div class="collection-detail">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="collection" class="collection-content">
      <div class="collection-header">
        <div class="collection-title-section">
          <h1>{{ collection.name }}</h1>
          <div class="collection-meta">
            <el-tag v-if="collection.isPublic" type="success">公开</el-tag>
            <el-tag v-if="collection.isDefault" type="warning">默认</el-tag>
            <span class="collection-date">{{ formatDate(collection.createdAt) }}</span>
            <span class="collection-views">{{ collection.viewCount || 0 }} 次浏览</span>
          </div>
        </div>
        
        <div class="collection-actions">
          <el-button type="primary" @click="addItem" v-if="canEdit">
            <el-icon><Plus /></el-icon>
            添加项目
          </el-button>
          <el-button @click="editCollection" v-if="canEdit">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button @click="shareCollection" v-if="collection.isPublic">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
          <el-button @click="deleteCollection" type="danger" v-if="canEdit">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
      
      <div class="collection-description" v-if="collection.description">
        <h3>描述</h3>
        <p>{{ collection.description }}</p>
      </div>
      
      <div class="collection-tags" v-if="collection.tags && parseTags(collection.tags).length > 0">
        <h3>标签</h3>
        <el-tag v-for="tag in parseTags(collection.tags)" :key="tag" class="tag-item">{{ tag }}</el-tag>
      </div>
      
      <div class="collection-items">
        <div class="items-header">
          <h3>收藏项目 ({{ items.length }})</h3>
          <div class="items-actions">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索项目"
              style="width: 200px; margin-right: 12px;"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="filterType" placeholder="筛选类型" style="width: 120px;" clearable>
              <el-option label="全部" value="" />
              <el-option label="漏洞" value="VULNERABILITY" />
              <el-option label="笔记" value="NOTE" />
              <el-option label="链接" value="LINK" />
              <el-option label="文件" value="FILE" />
            </el-select>
          </div>
        </div>
        
        <div v-if="filteredItems.length === 0" class="empty-items">
          <el-empty description="暂无收藏项目">
            <el-button type="primary" @click="addItem" v-if="canEdit">添加第一个项目</el-button>
          </el-empty>
        </div>
        
        <div v-else class="items-grid">
          <div v-for="item in filteredItems" :key="item.id" class="item-card">
            <div class="item-header">
              <h4 class="item-title">{{ item.itemTitle }}</h4>
              <div class="item-actions">
                <el-button size="small" @click="viewItem(item)">查看</el-button>
                <el-button size="small" @click="editItem(item)" v-if="canEdit">编辑</el-button>
                <el-button size="small" type="danger" @click="removeItem(item)" v-if="canEdit">移除</el-button>
              </div>
            </div>
            <p class="item-description" v-if="item.itemDescription">{{ item.itemDescription }}</p>
            <div class="item-meta">
              <el-tag size="small" :type="getItemTypeColor(item.itemType)">{{ getItemTypeLabel(item.itemType) }}</el-tag>
              <span class="item-date">{{ formatDate(item.addedAt) }}</span>
            </div>
            <div class="item-url" v-if="item.itemUrl">
              <el-link :href="item.itemUrl" target="_blank" type="primary">
                <el-icon><Link /></el-icon>
                访问链接
              </el-link>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-state">
      <el-empty description="收藏夹不存在或已被删除">
        <el-button type="primary" @click="goBack">返回</el-button>
      </el-empty>
    </div>
    
    <!-- 添加项目对话框 -->
    <el-dialog v-model="showAddItemDialog" title="添加收藏项目" width="500px">
      <el-form :model="newItemForm" label-width="100px">
        <el-form-item label="项目标题" required>
          <el-input v-model="newItemForm.itemTitle" placeholder="请输入项目标题" />
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="newItemForm.itemType" placeholder="请选择项目类型">
            <el-option label="链接" value="LINK" />
            <el-option label="笔记" value="NOTE" />
            <el-option label="漏洞" value="VULNERABILITY" />
            <el-option label="文件" value="FILE" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="newItemForm.itemDescription" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="项目链接">
          <el-input v-model="newItemForm.itemUrl" placeholder="请输入项目链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddItemDialog = false">取消</el-button>
        <el-button type="primary" @click="addNewItem">添加</el-button>
      </template>
    </el-dialog>
    
    <!-- 编辑项目对话框 -->
    <el-dialog v-model="showEditItemDialog" title="编辑收藏项目" width="500px">
      <el-form :model="editingItem" label-width="100px" v-if="editingItem">
        <el-form-item label="项目标题" required>
          <el-input v-model="editingItem.itemTitle" placeholder="请输入项目标题" />
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="editingItem.itemType" placeholder="请选择项目类型">
            <el-option label="链接" value="LINK" />
            <el-option label="笔记" value="NOTE" />
            <el-option label="漏洞" value="VULNERABILITY" />
            <el-option label="文件" value="FILE" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="editingItem.itemDescription" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="项目链接">
          <el-input v-model="editingItem.itemUrl" placeholder="请输入项目链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditItemDialog = false">取消</el-button>
        <el-button type="primary" @click="updateItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Share, Delete, Search, Link } from '@element-plus/icons-vue'
import { collectionApi, type Collection } from '@/api/collectionApi'
import { collectionItemApi, type CollectionItem } from '@/api/collectionItemApi'
import { useAuthStore } from '@/stores/modules/auth'
import { isSuccessResponse } from '@/utils/api-helpers'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const collection = ref<Collection | null>(null)
const items = ref<CollectionItem[]>([])
const searchKeyword = ref('')
const filterType = ref('')
const showAddItemDialog = ref(false)
const showEditItemDialog = ref(false)
const editingItem = ref<CollectionItem | null>(null)
const newItemForm = ref({
  itemTitle: '',
  itemDescription: '',
  itemType: 'LINK',
  itemUrl: ''
})

const canEdit = computed(() => {
  return collection.value && authStore.user?.id === collection.value.userId
})

const filteredItems = computed(() => {
  let filtered = items.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item => 
      item.itemTitle.toLowerCase().includes(keyword) ||
      (item.itemDescription && item.itemDescription.toLowerCase().includes(keyword))
    )
  }
  
  if (filterType.value) {
    filtered = filtered.filter(item => item.itemType === filterType.value)
  }
  
  return filtered
})

const loadCollection = async () => {
  try {
    loading.value = true
    const collectionId = route.params.id as string
    const response = await collectionApi.getCollectionById(parseInt(collectionId))
    
    if (isSuccessResponse(response) && response.data) {
      collection.value = response.data
      await loadItems()
    } else {
      ElMessage.error(response.message || '收藏夹不存在')
    }
  } catch (error) {
    ElMessage.error('加载收藏夹失败')
  } finally {
    loading.value = false
  }
}

const loadItems = async () => {
  if (!collection.value) return
  
  try {
    const response = await collectionItemApi.getItemsByCollection(collection.value.id)
    if (isSuccessResponse(response) && response.data) {
      items.value = response.data
    }
  } catch (error) {
    console.error('加载收藏项目失败:', error)
  }
}

const addItem = () => {
  // 显示添加项目对话框
  showAddItemDialog.value = true
}

const editCollection = () => {
  router.push(`/collections/${collection.value?.id}/edit`)
}

const shareCollection = () => {
  if (collection.value) {
    const url = `${window.location.origin}/collections/${collection.value.id}`
    navigator.clipboard.writeText(url).then(() => {
      ElMessage.success('分享链接已复制到剪贴板')
    }).catch(() => {
      ElMessage.info(`分享链接：${url}`)
    })
  }
}

const deleteCollection = async () => {
  if (!collection.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这个收藏夹吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await collectionApi.deleteCollection(collection.value.id)
    
    if (isSuccessResponse(response)) {
      ElMessage.success('收藏夹已删除')
      router.push('/profile/collections')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewItem = (item: CollectionItem) => {
  if (item.itemUrl) {
    window.open(item.itemUrl, '_blank')
  } else {
    // 显示项目详情对话框
    ElMessageBox.alert(
      `<div style="text-align: left;">
        <h3>${item.itemTitle}</h3>
        <p><strong>类型:</strong> ${item.itemType}</p>
        <p><strong>描述:</strong> ${item.itemDescription || '无描述'}</p>
        <p><strong>添加时间:</strong> ${new Date(item.addedAt).toLocaleString()}</p>
      </div>`,
      '项目详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭'
      }
    )
  }
}

const editItem = (item: CollectionItem) => {
  // 显示编辑项目对话框
  editingItem.value = item
  showEditItemDialog.value = true
}

const removeItem = async (item: CollectionItem) => {
  try {
    await ElMessageBox.confirm('确定要移除这个项目吗？', '确认移除', {
      confirmButtonText: '移除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await collectionItemApi.deleteItem(item.id)
    
    if (isSuccessResponse(response)) {
      ElMessage.success('项目已移除')
      await loadItems()
    } else {
      ElMessage.error(response.message || '移除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

const getItemTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'VULNERABILITY': '漏洞',
    'NOTE': '笔记',
    'LINK': '链接',
    'FILE': '文件'
  }
  return typeMap[type] || type
}

const getItemTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    'VULNERABILITY': 'danger',
    'NOTE': 'primary',
    'LINK': 'success',
    'FILE': 'info'
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

const addNewItem = async () => {
  if (!collection.value) return
  
  try {
    const response = await collectionItemApi.createItem({
      collectionId: collection.value.id,
      itemId: Date.now().toString(),
      itemType: newItemForm.value.itemType,
      itemTitle: newItemForm.value.itemTitle,
      itemDescription: newItemForm.value.itemDescription,
      itemUrl: newItemForm.value.itemUrl
    })
    
    if (isSuccessResponse(response)) {
      ElMessage.success('项目添加成功')
      showAddItemDialog.value = false
      newItemForm.value = {
        itemTitle: '',
        itemDescription: '',
        itemType: 'LINK',
        itemUrl: ''
      }
      await loadItems()
    } else {
      ElMessage.error(response.message || '添加项目失败')
    }
  } catch (error) {
    ElMessage.error('添加项目失败')
  }
}

const updateItem = async () => {
  if (!editingItem.value) return
  
  try {
    const response = await collectionItemApi.updateItem(editingItem.value.id!, {
      itemTitle: editingItem.value.itemTitle,
      itemDescription: editingItem.value.itemDescription,
      itemType: editingItem.value.itemType,
      itemUrl: editingItem.value.itemUrl
    })
    
    if (isSuccessResponse(response)) {
      ElMessage.success('项目更新成功')
      showEditItemDialog.value = false
      editingItem.value = null
      await loadItems()
    } else {
      ElMessage.error(response.message || '更新项目失败')
    }
  } catch (error) {
    ElMessage.error('更新项目失败')
  }
}

onMounted(() => {
  loadCollection()
})
</script>

<style scoped>
.collection-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.collection-title-section h1 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 28px;
  line-height: 1.4;
}

.collection-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.collection-meta .el-tag {
  margin-right: 8px;
}

.collection-date,
.collection-views {
  color: #909399;
  font-size: 14px;
}

.collection-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.collection-description,
.collection-tags {
  margin-bottom: 24px;
}

.collection-description h3,
.collection-tags h3 {
  color: #606266;
  margin-bottom: 12px;
  font-size: 16px;
}

.collection-description p {
  color: #303133;
  line-height: 1.6;
  margin: 0;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 4px;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.items-header h3 {
  margin: 0;
  color: #303133;
}

.items-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.item-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  background: #fff;
  transition: all 0.3s;
}

.item-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-title {
  margin: 0;
  color: #303133;
  font-size: 16px;
  line-height: 1.4;
  flex: 1;
  margin-right: 8px;
}

.item-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.item-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 8px 0;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.item-date {
  color: #909399;
  font-size: 12px;
}

.item-url {
  margin-top: 8px;
}

.empty-items {
  padding: 40px;
  text-align: center;
}

.loading {
  padding: 40px;
}

.error-state {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .collection-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .collection-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .items-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .items-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
}
</style>
