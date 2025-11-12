import request from '@/utils/request'

// 笔记评论相关API接口

export interface NoteComment {
  id?: number
  noteId: number
  userId: number
  parentId?: number
  content: string
  isDeleted?: boolean
  likeCount?: number
  createdAt?: string
  updatedAt?: string
}

// 创建评论
export const createComment = (comment: NoteComment) => {
  return request({
    url: '/api/v1/note-comments',
    method: 'post',
    data: comment
  })
}

// 更新评论
export const updateComment = (commentId: number, comment: NoteComment) => {
  return request({
    url: `/api/v1/note-comments/${commentId}`,
    method: 'put',
    data: comment
  })
}

// 删除评论
export const deleteComment = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}`,
    method: 'delete'
  })
}

// 软删除评论
export const softDeleteComment = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}/soft-delete`,
    method: 'post'
  })
}

// 恢复评论
export const restoreComment = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}/restore`,
    method: 'post'
  })
}

// 获取评论详情
export const getCommentById = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}`,
    method: 'get'
  })
}

// 获取笔记评论
export const getNoteComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}`,
    method: 'get'
  })
}

// 分页获取笔记评论
export const getNoteCommentsPage = (noteId: number, page = 0, size = 20) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/page`,
    method: 'get',
    params: { page, size }
  })
}

// 获取笔记顶级评论
export const getNoteTopComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/top`,
    method: 'get'
  })
}

// 分页获取笔记顶级评论
export const getNoteTopCommentsPage = (noteId: number, page = 0, size = 20) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/top/page`,
    method: 'get',
    params: { page, size }
  })
}

// 获取子评论
export const getChildComments = (parentId: number) => {
  return request({
    url: `/api/v1/note-comments/parent/${parentId}`,
    method: 'get'
  })
}

// 获取我的评论
export const getMyComments = (page = 0, size = 20) => {
  return request({
    url: '/api/v1/note-comments/my',
    method: 'get',
    params: { page, size }
  })
}

// 获取笔记活跃评论
export const getActiveNoteComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/active`,
    method: 'get'
  })
}

// 获取笔记活跃顶级评论
export const getActiveNoteTopComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/active/top`,
    method: 'get'
  })
}

// 获取笔记评论数
export const countNoteComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/count`,
    method: 'get'
  })
}

// 获取笔记活跃评论数
export const countActiveNoteComments = (noteId: number) => {
  return request({
    url: `/api/v1/note-comments/note/${noteId}/active/count`,
    method: 'get'
  })
}

// 获取我的评论数
export const countMyComments = () => {
  return request({
    url: '/api/v1/note-comments/my/count',
    method: 'get'
  })
}

// 获取我的活跃评论数
export const countMyActiveComments = () => {
  return request({
    url: '/api/v1/note-comments/my/active/count',
    method: 'get'
  })
}

// 获取最近评论
export const getRecentComments = (page = 0, size = 10) => {
  return request({
    url: '/api/v1/note-comments/recent',
    method: 'get',
    params: { page, size }
  })
}

// 获取我的最近评论
export const getMyRecentComments = (page = 0, size = 10) => {
  return request({
    url: '/api/v1/note-comments/my/recent',
    method: 'get',
    params: { page, size }
  })
}

// 点赞评论
export const likeComment = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}/like`,
    method: 'post'
  })
}

// 取消点赞评论
export const unlikeComment = (commentId: number) => {
  return request({
    url: `/api/v1/note-comments/${commentId}/unlike`,
    method: 'post'
  })
}
