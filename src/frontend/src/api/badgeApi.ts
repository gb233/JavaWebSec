import request from '@/utils/request'

// 徽章相关API接口
export const badgeApi = {
  // 获取所有徽章
  getAllBadges: () => {
    return request({
      url: '/api/v1/badges',
      method: 'get'
    })
  },

  // 根据分类获取徽章
  getBadgesByCategory: (category: string) => {
    return request({
      url: `/api/v1/badges/category/${category}`,
      method: 'get'
    })
  },

  // 根据代码获取徽章
  getBadgeByCode: (badgeCode: string) => {
    return request({
      url: `/api/v1/badges/code/${badgeCode}`,
      method: 'get'
    })
  },

  // 获取用户徽章
  getUserBadges: () => {
    return request({
      url: '/api/v1/badges/user',
      method: 'get'
    })
  },

  // 根据分类获取用户徽章
  getUserBadgesByCategory: (category: string) => {
    return request({
      url: `/api/v1/badges/user/category/${category}`,
      method: 'get'
    })
  },

  // 获取用户最近徽章
  getUserRecentBadges: (limit: number = 5) => {
    return request({
      url: '/api/v1/badges/user/recent',
      method: 'get',
      params: { limit }
    })
  },

  // 获取用户徽章统计
  getUserBadgeStats: () => {
    return request({
      url: '/api/v1/badges/user/stats',
      method: 'get'
    })
  },

  // 颁发徽章
  awardBadge: (badgeCode: string) => {
    return request({
      url: `/api/v1/badges/award/${badgeCode}`,
      method: 'post'
    })
  },

  // 检查徽章
  checkBadge: (badgeCode: string) => {
    return request({
      url: `/api/v1/badges/check/${badgeCode}`,
      method: 'get'
    })
  },

  // 获取用户徽章积分总和
  getUserBadgePoints: () => {
    return request({
      url: '/api/v1/badges/user/points',
      method: 'get'
    })
  }
}
