import { request } from '@/utils/request'
import type { ApiResult, PageResult } from '@/types/api'
import type {
  KnowledgeCategory,
  KnowledgeVulnerabilityDetail,
  KnowledgeVulnerabilitySummary
} from '@/types/knowledge'

export interface KnowledgeQuery {
  category?: string
  keyword?: string
  page?: number
  size?: number
  sort?: string
}

export type KnowledgePageResult = PageResult<KnowledgeVulnerabilitySummary> & {
  numberOfElements?: number
  pageable?: unknown
  sort?: unknown
}

export function fetchKnowledgeCategories(): Promise<ApiResult<KnowledgeCategory[]>> {
  return request.get('/api/v1/knowledge/categories') as Promise<ApiResult<KnowledgeCategory[]>>
}

export function fetchVulnerabilities(
  params: KnowledgeQuery
): Promise<ApiResult<KnowledgePageResult>> {
  return request.get('/api/v1/knowledge/vulnerabilities', {
    params
  }) as Promise<ApiResult<KnowledgePageResult>>
}

export function fetchVulnerabilityDetail(
  id: number | string
): Promise<ApiResult<KnowledgeVulnerabilityDetail>> {
  return request.get(`/api/v1/knowledge/vulnerabilities/${id}`) as Promise<
    ApiResult<KnowledgeVulnerabilityDetail>
  >
}
