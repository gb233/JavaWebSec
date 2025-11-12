import { request } from '@/utils/request'
import type { ApiResult, DashboardOverview } from '@/types/api'

export const fetchDashboardOverview = async (): Promise<ApiResult<DashboardOverview>> => {
  return request.get('/api/v1/dashboard/overview')
}

export default {
  fetchDashboardOverview
}
