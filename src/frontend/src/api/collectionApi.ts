import request from '@/utils/request'
import { collectionItemApi } from './collectionItemApi'

// 收藏夹相关API接口
export const collectionApi = {
  // 创建收藏夹
  createCollection: (data: any) => {
    return request({
      url: '/api/v1/collections',
      method: 'post',
      data
    })
  },

  // 更新收藏夹
  updateCollection: (collectionId: number, data: any) => {
    return request({
      url: `/api/v1/collections/${collectionId}`,
      method: 'put',
      data
    })
  },

  // 删除收藏夹
  deleteCollection: (collectionId: number) => {
    return request({
      url: `/api/v1/collections/${collectionId}`,
      method: 'delete'
    })
  },

  // 获取单个收藏夹
  getCollectionById: (collectionId: number) => {
    return request({
      url: `/api/v1/collections/${collectionId}`,
      method: 'get'
    })
  },

  // 获取我的所有收藏夹
  getMyCollections: () => {
    return request({
      url: '/api/v1/collections/my',
      method: 'get'
    })
  },

  // 分页获取我的收藏夹
  getMyCollectionsPaged: (params: any) => {
    return request({
      url: '/api/v1/collections/my/page',
      method: 'get',
      params
    })
  },

  // 获取我的公开收藏夹
  getMyPublicCollections: () => {
    return request({
      url: '/api/v1/collections/my/public',
      method: 'get'
    })
  },

  // 获取我的默认收藏夹
  getMyDefaultCollection: () => {
    return request({
      url: '/api/v1/collections/my/default',
      method: 'get'
    })
  },

  // 快速收藏漏洞
  quickCollectVulnerability: (vulnerabilityId: number, vulnerabilityTitle: string, vulnerabilityCode: string, collectionId?: number) => {
    return collectionItemApi.quickAddItem({
      itemType: 'vulnerability',
      itemId: vulnerabilityId,
      itemTitle: vulnerabilityTitle,
      itemDescription: `漏洞分类：${vulnerabilityCode}`,
      itemUrl: `/knowledge/vulnerability/${vulnerabilityCode}`,
      collectionId
    })
  },

  // 获取所有公开收藏夹
  getPublicCollections: (params: any) => {
    return request({
      url: '/api/v1/collections/public',
      method: 'get',
      params
    })
  },

  // 搜索我的收藏夹
  searchMyCollections: (keyword: string) => {
    return request({
      url: '/api/v1/collections/search',
      method: 'get',
      params: { keyword }
    })
  },

  // 获取我的收藏夹统计
  getMyCollectionStats: () => {
    return request({
      url: '/api/v1/collections/stats/my',
      method: 'get'
    })
  },

  // 获取全局收藏夹统计
  getGlobalCollectionStats: () => {
    return request({
      url: '/api/v1/collections/stats/global',
      method: 'get'
    })
  },

  // 设置默认收藏夹
  setDefaultCollection: (collectionId: number) => {
    return request({
      url: `/api/v1/collections/${collectionId}/set-default`,
      method: 'put'
    })
  },

  // 获取我的最近收藏夹
  getMyRecentCollections: (params: any) => {
    return request({
      url: '/api/v1/collections/my/recent',
      method: 'get',
      params
    })
  },

  // 获取热门收藏夹
  getPopularCollections: (params: any) => {
    return request({
      url: '/api/v1/collections/popular',
      method: 'get',
      params
    })
  }
}
