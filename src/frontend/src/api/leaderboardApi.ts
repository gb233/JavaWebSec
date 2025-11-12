/*
// 排行榜功能暂时注释掉 - 2025-01-15
import request from '@/utils/request'
import type { ApiResult } from '@/api/model/apiResult'

// 排行榜条目接口
export interface LeaderboardEntry {
  userId: number
  username: string
  fullName: string
  avatarUrl?: string
  rank?: number
  totalScore: number
  totalChallenges: number
  successCount: number
  successRate: number
  lastChallengeAt?: string
  categoryCode?: string
  categoryName?: string
  displayName?: string
  successRatePercent?: string
}

// 用户挑战统计接口
export interface UserChallengeStats {
  userId: number
  username?: string
  totalChallenges: number
  successCount: number
  totalScore: number
  successRate: number
  lastChallengeAt?: string
  currentRank?: number
  totalUsers?: number
  successRatePercent?: string
}

// 分页响应接口
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
}

// 排行榜API
export const leaderboardApi = {
  // 获取总体排行榜
  getOverallLeaderboard: (params?: {
    page?: number
    size?: number
    sortBy?: string
    sortDir?: 'asc' | 'desc'
  }) => {
    return request<ApiResult<PageResponse<LeaderboardEntry>>>({
      url: '/api/v1/challenge/leaderboard/overall',
      method: 'get',
      params
    })
  },

  // 获取分类排行榜
  getCategoryLeaderboard: (categoryCode: string, params?: {
    page?: number
    size?: number
    sortBy?: string
    sortDir?: 'asc' | 'desc'
  }) => {
    return request<ApiResult<PageResponse<LeaderboardEntry>>>({
      url: `/api/v1/challenge/leaderboard/category/${categoryCode}`,
      method: 'get',
      params
    })
  },

  // 获取我的统计
  getMyStats: (categoryCode?: string) => {
    return request<ApiResult<UserChallengeStats>>({
      url: '/api/v1/challenge/leaderboard/my-stats',
      method: 'get',
      params: categoryCode ? { categoryCode } : {}
    })
  },

  // 获取排行榜前N名
  getTopLeaderboard: (categoryCode?: string, limit: number = 10) => {
    return request<ApiResult<LeaderboardEntry[]>>({
      url: '/api/v1/challenge/leaderboard/top',
      method: 'get',
      params: {
        categoryCode: categoryCode || 'all',
        limit
      }
    })
  },

  // 获取我的排名
  getMyRank: (categoryCode?: string) => {
    return request<ApiResult<number>>({
      url: '/api/v1/challenge/leaderboard/my-rank',
      method: 'get',
      params: categoryCode ? { categoryCode } : {}
    })
  },

  // 刷新排行榜
  refreshLeaderboard: (categoryCode?: string) => {
    return request<ApiResult<void>>({
      url: '/api/v1/challenge/leaderboard/refresh',
      method: 'post',
      params: categoryCode ? { categoryCode } : {}
    })
  }
}
*/
