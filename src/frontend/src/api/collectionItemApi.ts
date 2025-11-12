import request from '@/utils/request'

// 收藏项目相关API接口

export interface CollectionItem {
  id?: number
  collectionId: number
  itemId: number | string  // 后端是Long，JSON序列化后可能是number或string
  itemType: string
  itemTitle: string
  itemDescription?: string
  itemUrl?: string
  itemMetadata?: string
  addedAt?: string
}

export const collectionItemApi = {
  // 创建收藏项目
  createItem: (item: CollectionItem) => {
    return request({
      url: '/api/v1/collection-items',
      method: 'post',
      data: item
    })
  },

  // 更新收藏项目
  updateItem: (itemId: number, item: Partial<CollectionItem>) => {
    return request({
      url: `/api/v1/collection-items/${itemId}`,
      method: 'put',
      data: item
    })
  },

  // 删除收藏项目
  deleteItem: (itemId: number) => {
    return request({
      url: `/api/v1/collection-items/${itemId}`,
      method: 'delete'
    })
  },

  // 获取收藏项目详情
  getItemById: (itemId: number) => {
    return request({
      url: `/api/v1/collection-items/${itemId}`,
      method: 'get'
    })
  },

  // 根据收藏夹ID获取项目列表
  getItemsByCollection: (collectionId: number, params?: any) => {
    return request({
      url: `/api/v1/collection-items/collection/${collectionId}`,
      method: 'get',
      params
    })
  },

  // 搜索收藏项目
  searchItems: (params: any) => {
    return request({
      url: '/api/v1/collection-items/search',
      method: 'get',
      params
    })
  },

  // 快速添加收藏项
  quickAddItem: (params: {
    itemType: string
    itemId: number
    itemTitle: string
    itemDescription?: string
    itemUrl?: string
    collectionId?: number
  }) => {
    return request({
      url: '/api/v1/collection-items/quick-add',
      method: 'post',
      params
    })
  },

  // 获取我的所有收藏项
  getMyCollectionItems: () => {
    return request({
      url: '/api/v1/collection-items/my',
      method: 'get'
    })
  }
}