import request from '@/utils/request'

// 笔记标签相关API接口

export interface NoteTag {
  id?: number
  tagName: string
  description?: string
  color?: string
  isSystem: boolean
  usageCount?: number
  createdAt?: string
  updatedAt?: string
}

// 创建标签
export const createTag = (tag: NoteTag) => {
  return request({
    url: '/api/v1/note-tags',
    method: 'post',
    data: tag
  })
}

// 更新标签
export const updateTag = (tagId: number, tag: NoteTag) => {
  return request({
    url: `/api/v1/note-tags/${tagId}`,
    method: 'put',
    data: tag
  })
}

// 删除标签
export const deleteTag = (tagId: number) => {
  return request({
    url: `/api/v1/note-tags/${tagId}`,
    method: 'delete'
  })
}

// 获取标签详情
export const getTagById = (tagId: number) => {
  return request({
    url: `/api/v1/note-tags/${tagId}`,
    method: 'get'
  })
}

// 根据名称获取标签
export const getTagByName = (tagName: string) => {
  return request({
    url: `/api/v1/note-tags/name/${tagName}`,
    method: 'get'
  })
}

// 获取所有标签
export const getAllTags = () => {
  return request({
    url: '/api/v1/note-tags',
    method: 'get'
  })
}

// 获取系统标签
export const getSystemTags = () => {
  return request({
    url: '/api/v1/note-tags/system',
    method: 'get'
  })
}

// 获取用户标签
export const getUserTags = () => {
  return request({
    url: '/api/v1/note-tags/user',
    method: 'get'
  })
}

// 获取热门标签
export const getPopularTags = () => {
  return request({
    url: '/api/v1/note-tags/popular',
    method: 'get'
  })
}

// 搜索标签
export const searchTags = (keyword: string) => {
  return request({
    url: '/api/v1/note-tags/search',
    method: 'get',
    params: { keyword }
  })
}

// 根据颜色获取标签
export const getTagsByColor = (color: string) => {
  return request({
    url: `/api/v1/note-tags/color/${color}`,
    method: 'get'
  })
}

// 获取最常用标签
export const getMostUsedTags = () => {
  return request({
    url: '/api/v1/note-tags/most-used',
    method: 'get'
  })
}

// 获取标签总数
export const getTagCount = () => {
  return request({
    url: '/api/v1/note-tags/count',
    method: 'get'
  })
}

// 增加标签使用次数
export const incrementTagUsage = (tagName: string) => {
  return request({
    url: `/api/v1/note-tags/${tagName}/increment`,
    method: 'post'
  })
}

// 减少标签使用次数
export const decrementTagUsage = (tagName: string) => {
  return request({
    url: `/api/v1/note-tags/${tagName}/decrement`,
    method: 'post'
  })
}

// 删除未使用标签
export const deleteUnusedTags = () => {
  return request({
    url: '/api/v1/note-tags/unused',
    method: 'delete'
  })
}

// 根据名称删除标签
export const deleteTagByName = (tagName: string) => {
  return request({
    url: `/api/v1/note-tags/name/${tagName}`,
    method: 'delete'
  })
}
