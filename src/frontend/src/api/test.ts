import { request } from '@/utils/request'
import type { ApiResult, PageResult } from '@/types/api'

// 测试相关类型定义
export interface TestQuestion {
  id: number
  categoryCode: string
  questionType: 'single' | 'multiple' | 'judge' | 'SINGLE' | 'MULTIPLE' | 'JUDGE' | 'single_choice' | 'multiple_choice' | 'true_false' | 'fill_blank' | 'FILL_BLANK'
  difficulty: 'easy' | 'medium' | 'hard'
  knowledgeSource: 'principle' | 'harm' | 'exploit' | 'vulnerable_code' | 'secure_code' | 'repair' | 'detection'
  questionText: string
  questionImage?: string
  options: string[] | string
  correctAnswer: string
  explanation: string
  score: number
  points?: number
  tags?: string[]
  authorId?: number
  status: 'draft' | 'review' | 'approved'
  createdAt: string
  updatedAt?: string
  // 动态添加的属性
  isAnswered?: boolean
  isCorrect?: boolean
  userAnswer?: string
  correctAnswer?: string
  explanation?: string
}

// 测试会话
export interface TestSession {
  id: number
  userId: number
  modeCode: string
  categoryCode?: string
  sessionCode: string
  status: 'active' | 'completed' | 'abandoned'
  currentQuestionIndex: number
  totalQuestions: number
  answeredQuestions: number
  correctAnswers: number
  totalScore: number
  startTime: string
  endTime?: string
  createdAt: string
}

// 测试答案
export interface TestAnswer {
  id: number
  sessionId: number
  questionId: number
  questionIndex: number
  userAnswer?: string
  isCorrect?: boolean
  score: number
  feedbackShown: boolean
  answeredAt: string
  correctAnswer?: string
  explanation?: string
}

// 用户测试记录
export interface UserTestRecord {
  id: number
  userId: number
  sessionId: number
  modeCode: string
  categoryCode?: string
  totalScore: number
  correctCount: number
  totalQuestions: number
  completionRate: number
  timeSpent: number
  startedAt: string
  completedAt?: string
}

// 测试统计
export interface TestStats {
  totalTests: number
  passedTests: number
  averageScore: number // 平均总得分（单位：分）
  averageAccuracy?: number // 平均正确率（单位：百分比）
  totalTimeSpent: number
  categoryStats: Record<string, any>
  recentTests: UserTestRecord[]
}

// 测试模式
export interface TestMode {
  modeCode: string
  modeName: string
  description: string
  features: Record<string, any>
}

// 测试API接口
const testApi = {
  // 开始测试会话
  startTestSession: (modeCode: string, categoryCode?: string) =>
    request.post('/api/v1/test/start', null, {
      params: { modeCode, categoryCode }
    }) as Promise<ApiResult<TestSession>>,

  // 获取测试会话
  getTestSession: (sessionCode: string) =>
    request.get(`/api/v1/test/session/${sessionCode}`) as Promise<ApiResult<TestSession>>,

  // 获取测试题目
  getTestQuestions: (sessionCode: string) =>
    request.get(`/api/v1/test/session/${sessionCode}/questions`) as Promise<ApiResult<TestQuestion[]>>,

  // 获取当前题目
  getCurrentQuestion: (sessionCode: string, questionIndex: number) =>
    request.get(`/api/v1/test/session/${sessionCode}/question/${questionIndex}`) as Promise<ApiResult<TestQuestion>>,

  // 提交答案（实时反馈模式）
  submitAnswer: (sessionCode: string, questionId: number, userAnswer: string) =>
    request.post(`/api/v1/test/session/${sessionCode}/answer`, null, {
      params: { questionId, userAnswer }
    }) as Promise<ApiResult<Record<string, any>>>,

  // 批量提交答案（考试模式）
  submitAnswers: (sessionCode: string, answers: Record<number, string>) =>
    request.post(`/api/v1/test/session/${sessionCode}/answers`, answers) as Promise<ApiResult<TestAnswer[]>>,

  // 获取答题反馈
  getAnswerFeedback: (sessionCode: string, questionId: number) =>
    request.get(`/api/v1/test/session/${sessionCode}/answer/${questionId}`) as Promise<ApiResult<TestAnswer>>,

  // 结束测试会话
  endTestSession: (sessionCode: string) =>
    request.post(`/api/v1/test/session/${sessionCode}/end`) as Promise<ApiResult<TestSession>>,

  // 获取测试结果
  getTestResult: (sessionCode: string) =>
    request.get(`/api/v1/test/session/${sessionCode}/result`) as Promise<ApiResult<Record<string, any>>>,

  // 获取用户测试记录
  getUserTestRecords: (params: {
    page?: number
    size?: number
  }) =>
    request.get('/api/v1/test/records', { params }) as Promise<ApiResult<PageResult<UserTestRecord>>>,

  // 获取用户测试统计
  getUserTestStatistics: () =>
    request.get('/api/v1/test/statistics') as Promise<ApiResult<TestStats>>,

  // 获取测试会话历史
  getTestSessionHistory: (params: {
    page?: number
    size?: number
  }) =>
    request.get('/api/v1/test/sessions/history', { params }) as Promise<ApiResult<PageResult<TestSession>>>,

  // 获取测试会话详情
  getTestSessionDetails: (sessionCode: string) =>
    request.get(`/api/v1/test/sessions/${sessionCode}/details`) as Promise<ApiResult<Record<string, any>>>,

  // 获取测试记录详情
  getTestRecordDetail: (recordId: number) =>
    request.get(`/api/v1/test/records/${recordId}`) as Promise<ApiResult<Record<string, any>>>,

  // 获取分类统计（添加到testApi中）
  getCategoryStatistics: (categoryCode: string) =>
    request.get('/api/v1/questions/statistics', { params: { categoryCode } }) as Promise<ApiResult<Record<string, any>>>
}

// 题目API接口
export const questionApi = {
  // 获取题目列表
  getQuestions: (params: {
    categoryCode: string
    page?: number
    size?: number
  }) =>
    request.get('/api/v1/questions', { params }) as Promise<ApiResult<PageResult<TestQuestion>>>,

  // 获取题目详情
  getQuestion: (questionId: number) =>
    request.get(`/api/v1/questions/${questionId}`) as Promise<ApiResult<TestQuestion>>,

  // 搜索题目
  searchQuestions: (params: {
    keyword?: string
    categoryCode?: string
    questionType?: string
    difficulty?: string
    knowledgeSource?: string
    page?: number
    size?: number
  }) =>
    request.get('/api/v1/questions/search', { params }) as Promise<ApiResult<PageResult<TestQuestion>>>,

  // 获取随机题目
  getRandomQuestions: (params: {
    categoryCode?: string
    questionType?: string
    difficulty?: string
    knowledgeSource?: string
    count?: number
  }) =>
    request.get('/api/v1/questions/random', { params }) as Promise<ApiResult<TestQuestion[]>>,

  // 获取分类统计
  getCategoryStatistics: (categoryCode: string) =>
    request.get('/api/v1/questions/statistics', { params: { categoryCode } }) as Promise<ApiResult<Record<string, any>>>,

  // 获取类型统计
  getTypeStatistics: () =>
    request.get('/api/v1/questions/statistics/type') as Promise<ApiResult<Record<string, any>>>,

  // 获取难度统计
  getDifficultyStatistics: () =>
    request.get('/api/v1/questions/statistics/difficulty') as Promise<ApiResult<Record<string, any>>>,

  // 获取知识点统计
  getKnowledgeSourceStatistics: () =>
    request.get('/api/v1/questions/statistics/knowledge-source') as Promise<ApiResult<Record<string, any>>>
}

// 默认导出
export default testApi