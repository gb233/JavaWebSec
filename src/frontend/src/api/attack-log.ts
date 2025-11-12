import type { ApiResult, PageResult, AttackLogRecord, AttackLogStats } from '@/types/api'
import { request } from '@/utils/request'

export interface AttackLogQueryParams {
  module?: string
  attackType?: string
  successful?: string | boolean
  riskLevel?: string
  keyword?: string
  startTime?: string
  endTime?: string
  page?: number
  size?: number
  sort?: string
}

export const fetchAttackLogs = async (
  params: AttackLogQueryParams
): Promise<ApiResult<PageResult<AttackLogRecord>>> => {
  return request.get('/api/v1/attack-logs', { params })
}

export const fetchAttackLogDetail = async (
  id: number
): Promise<ApiResult<AttackLogRecord>> => {
  return request.get(`/api/v1/attack-logs/${id}`)
}

export const fetchAttackLogStats = async (): Promise<ApiResult<AttackLogStats>> => {
  return request.get('/api/v1/attack-logs/stats')
}
