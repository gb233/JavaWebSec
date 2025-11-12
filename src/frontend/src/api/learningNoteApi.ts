import request from '@/utils/request'

// 学习笔记相关API接口
export const learningNoteApi = {
  // 创建笔记
  createNote: (note: LearningNote) => {
    return request({
      url: '/api/v1/notes',
      method: 'post',
      data: note
    })
  },

  // 更新笔记
  updateNote: (noteId: number, note: Partial<LearningNote>) => {
    return request({
      url: `/api/v1/notes/${noteId}`,
      method: 'put',
      data: note
    })
  },

  // 删除笔记
  deleteNote: (noteId: number) => {
    return request({
      url: `/api/v1/notes/${noteId}`,
      method: 'delete'
    })
  },

  // 获取笔记详情
  getNoteById: (noteId: number) => {
    return request({
      url: `/api/v1/notes/${noteId}`,
      method: 'get'
    })
  },

  // 获取我的笔记
  getMyNotes: (params?: any) => {
    return request({
      url: '/api/v1/notes/my',
      method: 'get',
      params
    })
  }
}

export interface LearningNote {
  id?: number
  userId?: number // 创建时可选，后端会自动设置
  title: string
  content: string
  summary?: string
  noteType: string
  vulnerabilityCode?: string
  tags?: string | string[] // 可以是字符串（JSON）或数组
  isPublic: boolean
  isPinned: boolean
  wordCount?: number
  readingTime?: number
  viewCount?: number
  likeCount?: number
  commentCount?: number
  createdAt?: string
  updatedAt?: string
  lastModifiedAt?: string
}

export interface NoteStats {
  totalNotes: number
  publicNotes: number
  typeStats: Record<string, number>
  vulnerabilityStats: Record<string, number>
}

// 创建笔记
export const createNote = (note: LearningNote) => {
  return request({
    url: '/api/v1/notes',
    method: 'post',
    data: note
  })
}

// 更新笔记
export const updateNote = (noteId: number, note: LearningNote) => {
  return request({
    url: `/api/v1/notes/${noteId}`,
    method: 'put',
    data: note
  })
}

// 删除笔记
export const deleteNote = (noteId: number) => {
  return request({
    url: `/api/v1/notes/${noteId}`,
    method: 'delete'
  })
}

// 获取笔记详情
export const getNoteById = (noteId: number) => {
  return request({
    url: `/api/v1/notes/${noteId}`,
    method: 'get'
  })
}

// 获取我的笔记
export const getMyNotes = (page = 0, size = 20) => {
  return request({
    url: '/api/v1/notes/my',
    method: 'get',
    params: { page, size }
  })
}

// 获取我的笔记（按类型）
export const getMyNotesByType = (noteType: string) => {
  return request({
    url: `/api/v1/notes/my/type/${noteType}`,
    method: 'get'
  })
}

// 获取我的笔记（按漏洞）
export const getMyNotesByVulnerability = (vulnerabilityCode: string) => {
  return request({
    url: `/api/v1/notes/my/vulnerability/${vulnerabilityCode}`,
    method: 'get'
  })
}

// 获取公开笔记
export const getPublicNotes = (page = 0, size = 20) => {
  return request({
    url: '/api/v1/notes/public',
    method: 'get',
    params: { page, size }
  })
}

// 获取公开笔记（按漏洞）
export const getPublicNotesByVulnerability = (vulnerabilityCode: string) => {
  return request({
    url: `/api/v1/notes/public/vulnerability/${vulnerabilityCode}`,
    method: 'get'
  })
}

// 搜索笔记
export const searchNotes = (keyword: string) => {
  return request({
    url: '/api/v1/notes/search',
    method: 'get',
    params: { keyword }
  })
}

// 全文搜索笔记
export const fullTextSearchNotes = (keyword: string) => {
  return request({
    url: '/api/v1/notes/search/fulltext',
    method: 'get',
    params: { keyword }
  })
}

// 按标签搜索笔记
export const searchNotesByTag = (tag: string) => {
  return request({
    url: '/api/v1/notes/search/tag',
    method: 'get',
    params: { tag }
  })
}

// 获取置顶笔记
export const getPinnedNotes = () => {
  return request({
    url: '/api/v1/notes/my/pinned',
    method: 'get'
  })
}

// 获取最近笔记
export const getRecentNotes = () => {
  return request({
    url: '/api/v1/notes/my/recent',
    method: 'get'
  })
}

// 获取热门笔记
export const getPopularNotes = (page = 0, size = 10) => {
  return request({
    url: '/api/v1/notes/popular',
    method: 'get',
    params: { page, size }
  })
}

// 获取最新笔记
export const getLatestNotes = (page = 0, size = 10) => {
  return request({
    url: '/api/v1/notes/latest',
    method: 'get',
    params: { page, size }
  })
}

// 置顶/取消置顶笔记
export const togglePin = (noteId: number, isPinned: boolean) => {
  return request({
    url: `/api/v1/notes/${noteId}/pin`,
    method: 'post',
    params: { isPinned }
  })
}

// 公开/私有笔记
export const togglePublic = (noteId: number, isPublic: boolean) => {
  return request({
    url: `/api/v1/notes/${noteId}/public`,
    method: 'post',
    params: { isPublic }
  })
}

// 获取我的笔记统计
export const getMyNoteStats = () => {
  return request({
    url: '/api/v1/notes/stats/my',
    method: 'get'
  })
}

// 获取全局笔记统计
export const getGlobalNoteStats = () => {
  return request({
    url: '/api/v1/notes/stats/global',
    method: 'get'
  })
}

// 快速创建笔记
export const quickCreateNote = (note: {
  title: string
  content: string
  summary?: string
  vulnerabilityCode?: string
  noteType?: string
  tags?: string | string[]
  isPublic?: boolean
}) => {
  return request({
    url: '/api/v1/notes',
    method: 'post',
    data: {
      title: note.title,
      content: note.content,
      summary: note.summary,
      vulnerabilityCode: note.vulnerabilityCode,
      noteType: note.noteType || 'PERSONAL',
      tags: note.tags,
      isPublic: note.isPublic || false,
      isPinned: false
    }
  })
}
