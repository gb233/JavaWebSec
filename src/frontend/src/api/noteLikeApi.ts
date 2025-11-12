import request from '@/utils/request'

// 笔记点赞相关API接口

export interface NoteLike {
  id?: number
  userId: number
  noteId: number
  createdAt?: string
}

// 点赞笔记
export const likeNote = (noteId: number) => {
  return request({
    url: `/api/v1/note-likes/${noteId}`,
    method: 'post'
  })
}

// 取消点赞
export const unlikeNote = (noteId: number) => {
  return request({
    url: `/api/v1/note-likes/${noteId}`,
    method: 'delete'
  })
}

// 检查点赞状态
export const isLiked = (noteId: number) => {
  return request({
    url: `/api/v1/note-likes/${noteId}/status`,
    method: 'get'
  })
}

// 获取笔记点赞列表
export const getNoteLikes = (noteId: number) => {
  return request({
    url: `/api/v1/note-likes/${noteId}/list`,
    method: 'get'
  })
}

// 获取我的点赞
export const getMyLikes = () => {
  return request({
    url: '/api/v1/note-likes/my',
    method: 'get'
  })
}

// 获取笔记点赞数
export const countNoteLikes = (noteId: number) => {
  return request({
    url: `/api/v1/note-likes/${noteId}/count`,
    method: 'get'
  })
}

// 获取我的点赞数
export const countMyLikes = () => {
  return request({
    url: '/api/v1/note-likes/my/count',
    method: 'get'
  })
}

// 获取最近点赞的笔记
export const getRecentLikedNotes = (limit = 10) => {
  return request({
    url: '/api/v1/note-likes/recent',
    method: 'get',
    params: { limit }
  })
}

// 获取热门点赞笔记
export const getPopularLikedNotes = (limit = 10) => {
  return request({
    url: '/api/v1/note-likes/popular',
    method: 'get',
    params: { limit }
  })
}
