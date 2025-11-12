<template>
  <div class="profile-collections">
    <div class="page-header">
      <h1>我的收藏</h1>
      <p>管理您收藏的漏洞学习内容和学习资源</p>
    </div>
    
    <div class="collections-stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ collections.length }}</div>
              <div class="stat-label">收藏夹数量</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalItems }}</div>
              <div class="stat-label">收藏项总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ publicCollections.length }}</div>
              <div class="stat-label">公开收藏夹</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalViews }}</div>
              <div class="stat-label">总浏览次数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="collections-list">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>我的收藏夹</span>
            <el-button type="primary" @click="createCollection">
              <el-icon><Plus /></el-icon>
              新建收藏夹
            </el-button>
          </div>
        </template>
        
        <div v-if="loading" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="collections.length === 0" class="empty-state">
          <el-empty description="暂无收藏夹">
            <el-button type="primary" @click="createCollection">创建第一个收藏夹</el-button>
          </el-empty>
        </div>
        
        <div v-else class="collections-grid">
          <div v-for="collection in collections" :key="collection.id" class="collection-item">
            <el-card class="collection-card" @click="viewCollection(collection.id)">
              <div class="collection-header">
                <h3 class="collection-title">{{ collection.name }}</h3>
                <div class="collection-actions">
                  <el-tag v-if="collection.isPublic" type="success" size="small">公开</el-tag>
                  <el-tag v-if="collection.isDefault" type="warning" size="small">默认</el-tag>
                </div>
              </div>
              <p class="collection-description">{{ collection.description || '暂无描述' }}</p>
              <div class="collection-meta">
                <span class="collection-items">{{ collection.itemCount || 0 }} 项</span>
                <span class="collection-views">{{ collection.viewCount || 0 }} 次浏览</span>
                <span class="collection-date">{{ formatDate(collection.createdAt) }}</span>
              </div>
              <div class="collection-tags" v-if="collection.tags">
                <el-tag 
                  v-for="tag in parseTags(collection.tags)" 
                  :key="tag" 
                  size="small" 
                  class="collection-tag"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </el-card>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { collectionApi, type Collection } from '@/api/collectionApi'

const router = useRouter()

const loading = ref(false)
const collections = ref<Collection[]>([])

const publicCollections = computed(() => collections.value.filter(collection => collection.isPublic))
const totalItems = computed(() => collections.value.reduce((sum, collection) => sum + (collection.itemCount || 0), 0))
const totalViews = computed(() => collections.value.reduce((sum, collection) => sum + (collection.viewCount || 0), 0))

const loadCollections = async () => {
  try {
    loading.value = true
    const response = await collectionApi.getMyCollections()
    if (response.success) {
      collections.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载收藏夹失败')
  } finally {
    loading.value = false
  }
}

const createCollection = () => {
  // 跳转到收藏夹创建页面
  router.push('/collections/create')
}

const viewCollection = (id: number) => {
  // 跳转到收藏夹详情页面
  router.push(`/collections/${id}`)
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
  loadCollections()
})
</script>

<style scoped>
.profile-collections {
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

.collections-stats {
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

.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.collection-card {
  cursor: pointer;
  transition: all 0.3s;
}

.collection-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.collection-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 8px;
}

.collection-actions {
  display: flex;
  gap: 4px;
}

.collection-description {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.collection-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.collection-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.collection-tag {
  font-size: 12px;
}
</style>
