export interface KnowledgeCategory {
  id: number
  code: string
  name: string
  description?: string
  severity?: string
  iconUrl?: string
  colorTheme?: string
  owaspYear?: number
  orderNum?: number
}

export interface KnowledgeVulnerabilitySummary {
  id: number
  categoryCode?: string
  categoryName?: string
  title: string
  subtitle?: string
  description?: string
  difficultyLevel?: string
  severityLevel?: string
  estimatedTime?: number
  viewCount?: number
  slug?: string
}

export interface KnowledgeReferenceItem {
  title?: string
  url?: string
  description?: string
}

export interface KnowledgeVulnerabilityDetail extends KnowledgeVulnerabilitySummary {
  knowledgeContent?: string
  demoDescription?: string
  vulnerableCode?: string
  secureCode?: string
  repairSuggestions?: string
  realWorldExamples?: KnowledgeReferenceItem[]
  references?: KnowledgeReferenceItem[]
  viewCount?: number
  likeCount?: number
}
