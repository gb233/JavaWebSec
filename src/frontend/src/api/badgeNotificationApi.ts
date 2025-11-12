import request from '@/utils/request'

// 徽章通知相关API接口
export const badgeNotificationApi = {
  // 测试徽章通知
  testBadgeNotification: (badgeId: number) => {
    return request({
      url: `/api/v1/badge-notifications/test/${badgeId}`,
      method: 'post'
    })
  },

  // 发送进度通知
  sendProgressNotification: (badgeId: number, progress: number, target: number) => {
    return request({
      url: `/api/v1/badge-notifications/progress/${badgeId}`,
      method: 'post',
      params: { progress, target }
    })
  },

  // 发送即将完成通知
  sendNearCompletionNotification: (badgeId: number, progress: number, target: number) => {
    return request({
      url: `/api/v1/badge-notifications/near-completion/${badgeId}`,
      method: 'post',
      params: { progress, target }
    })
  },

  // 发送里程碑通知
  sendMilestoneNotification: (milestone: string, value: string) => {
    return request({
      url: '/api/v1/badge-notifications/milestone',
      method: 'post',
      params: { milestone, value }
    })
  },

  // 发送统计更新通知
  sendStatsUpdateNotification: (stats: any) => {
    return request({
      url: '/api/v1/badge-notifications/stats',
      method: 'post',
      data: stats
    })
  }
}
