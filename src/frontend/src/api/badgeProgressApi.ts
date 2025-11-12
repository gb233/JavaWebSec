import request from '@/utils/request'

// 徽章进度相关API接口
export const badgeProgressApi = {
  // 获取用户徽章进度
  getUserBadgeProgress: () => {
    return request({
      url: '/api/v1/badge-progress/user',
      method: 'get'
    })
  },

  // 获取用户徽章进度详情（包含徽章信息）
  getUserBadgeProgressWithDetails: () => {
    return request({
      url: '/api/v1/badge-progress/user/details',
      method: 'get'
    })
  },

  // 根据分类获取用户徽章进度
  getUserBadgeProgressByCategory: (category: string) => {
    return request({
      url: `/api/v1/badge-progress/user/category/${category}`,
      method: 'get'
    })
  },

  // 获取用户未完成徽章进度
  getUserUncompletedBadgeProgress: () => {
    return request({
      url: '/api/v1/badge-progress/user/uncompleted',
      method: 'get'
    })
  },

  // 获取用户已完成徽章进度
  getUserCompletedBadgeProgress: () => {
    return request({
      url: '/api/v1/badge-progress/user/completed',
      method: 'get'
    })
  },

  // 更新徽章进度
  updateBadgeProgress: (badgeId: number, progress: number) => {
    return request({
      url: '/api/v1/badge-progress/update',
      method: 'post',
      params: { badgeId, progress }
    })
  },

  // 增加徽章进度
  incrementBadgeProgress: (badgeId: number, increment: number) => {
    return request({
      url: '/api/v1/badge-progress/increment',
      method: 'post',
      params: { badgeId, increment }
    })
  },

  // 获取徽章进度百分比
  getBadgeProgressPercentage: (badgeId: number) => {
    return request({
      url: `/api/v1/badge-progress/percentage/${badgeId}`,
      method: 'get'
    })
  },

  // 获取用户徽章进度统计
  getUserBadgeProgressStats: () => {
    return request({
      url: '/api/v1/badge-progress/stats',
      method: 'get'
    })
  },

  // 检查徽章是否完成
  isBadgeCompleted: (badgeId: number) => {
    return request({
      url: `/api/v1/badge-progress/check/${badgeId}`,
      method: 'get'
    })
  }
}
