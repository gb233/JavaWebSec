import request from '@/utils/request'

// 收藏标签相关API接口
export const collectionTagApi = {
  // 创建标签
  createTag: (data: any) => {
    return request({
      url: '/api/v1/collection-tags',
      method: 'post',
      data
    })
  },

  // 更新标签
  updateTag: (tagId: number, data: any) => {
    return request({
      url: `/api/v1/collection-tags/${tagId}`,
      method: 'put',
      data
    })
  },

  // 删除标签
  deleteTag: (tagId: number) => {
    return request({
      url: `/api/v1/collection-tags/${tagId}`,
      method: 'delete'
    })
  },

  // 获取单个标签
  getTagById: (tagId: number) => {
    return request({
      url: `/api/v1/collection-tags/${tagId}`,
      method: 'get'
    })
  },

  // 根据名称获取标签
  getTagByName: (name: string) => {
    return request({
      url: `/api/v1/collection-tags/name/${name}`,
      method: 'get'
    })
  },

  // 获取所有标签
  getAllTags: () => {
    return request({
      url: '/api/v1/collection-tags',
      method: 'get'
    })
  },

  // 搜索标签
  searchTags: (keyword: string) => {
    return request({
      url: '/api/v1/collection-tags/search',
      method: 'get',
      params: { keyword }
    })
  },

  // 获取热门标签
  getPopularTags: () => {
    return request({
      url: '/api/v1/collection-tags/popular',
      method: 'get'
    })
  },

  // 获取未使用的标签
  getUnusedTags: () => {
    return request({
      url: '/api/v1/collection-tags/unused',
      method: 'get'
    })
  },

  // 增加标签使用次数
  incrementTagUsage: (tagId: number) => {
    return request({
      url: `/api/v1/collection-tags/${tagId}/increment`,
      method: 'put'
    })
  },

  // 减少标签使用次数
  decrementTagUsage: (tagId: number) => {
    return request({
      url: `/api/v1/collection-tags/${tagId}/decrement`,
      method: 'put'
    })
  },

  // 获取标签统计
  getTagStats: () => {
    return request({
      url: '/api/v1/collection-tags/stats',
      method: 'get'
    })
  },

  // 批量创建标签
  batchCreateTags: (tags: any[]) => {
    return request({
      url: '/api/v1/collection-tags/batch',
      method: 'post',
      data: tags
    })
  },

  // 清理未使用的标签
  cleanupUnusedTags: () => {
    return request({
      url: '/api/v1/collection-tags/cleanup',
      method: 'delete'
    })
  }
}
