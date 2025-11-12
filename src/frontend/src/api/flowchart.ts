import request from '@/utils/request'

/**
 * 流程图API接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
export const flowchartApi = {
  /**
   * 获取流程图数据
   * @param category 漏洞分类
   * @param type 流程图类型
   */
  getFlowchart: (category: string, type: string) => {
    return request({
      url: `/api/v1/demo/${category}/flowchart`,
      method: 'get',
      params: { type }
    })
  }
}
