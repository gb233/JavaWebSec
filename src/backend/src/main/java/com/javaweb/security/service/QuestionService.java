package com.javaweb.security.service;

import com.javaweb.security.entity.Question;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 题目服务接口
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
public interface QuestionService {

  /**
   * 获取题目列表
   *
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> getQuestions(String categoryCode, Pageable pageable);

  /**
   * 获取题目详情
   *
   * @param questionId 题目ID
   * @return 题目详情
   */
  Question getQuestion(Long questionId);

  /**
   * 根据分类代码和题目类型获取题目列表
   *
   * @param categoryCode 分类代码
   * @param questionType 题目类型
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> getQuestionsByType(
      String categoryCode, Question.QuestionType questionType, Pageable pageable);

  /**
   * 根据分类代码和难度获取题目列表
   *
   * @param categoryCode 分类代码
   * @param difficulty 难度等级
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> getQuestionsByDifficulty(
      String categoryCode, Question.Difficulty difficulty, Pageable pageable);

  /**
   * 根据分类代码和知识点来源获取题目列表
   *
   * @param categoryCode 分类代码
   * @param knowledgeSource 知识点来源
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> getQuestionsByKnowledgeSource(
      String categoryCode, Question.KnowledgeSource knowledgeSource, Pageable pageable);

  /**
   * 随机获取题目（单分类）
   *
   * @param categoryCode 分类代码
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestions(String categoryCode, int count);

  /**
   * 随机获取题目（按类型）
   *
   * @param categoryCode 分类代码
   * @param questionType 题目类型
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByType(
      String categoryCode, Question.QuestionType questionType, int count);

  /**
   * 随机获取题目（按难度）
   *
   * @param categoryCode 分类代码
   * @param difficulty 难度等级
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByDifficulty(
      String categoryCode, Question.Difficulty difficulty, int count);

  /**
   * 随机获取题目（按知识点来源）
   *
   * @param categoryCode 分类代码
   * @param knowledgeSource 知识点来源
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByKnowledgeSource(
      String categoryCode, Question.KnowledgeSource knowledgeSource, int count);

  /**
   * 跨分类随机获取题目
   *
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsAcrossCategories(int count);

  /**
   * 按类型跨分类随机获取题目
   *
   * @param questionType 题目类型
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByTypeAcrossCategories(
      Question.QuestionType questionType, int count);

  /**
   * 按难度跨分类随机获取题目
   *
   * @param difficulty 难度等级
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByDifficultyAcrossCategories(
      Question.Difficulty difficulty, int count);

  /**
   * 按知识点来源跨分类随机获取题目
   *
   * @param knowledgeSource 知识点来源
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestionsByKnowledgeSourceAcrossCategories(
      Question.KnowledgeSource knowledgeSource, int count);

  /**
   * 搜索题目
   *
   * @param keyword 关键词
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> searchQuestions(String keyword, Pageable pageable);

  /**
   * 根据标签搜索题目
   *
   * @param tag 标签
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> searchQuestionsByTag(String tag, Pageable pageable);

  /**
   * 获取题目统计
   *
   * @param categoryCode 分类代码
   * @return 题目统计
   */
  Map<String, Object> getQuestionStatistics(String categoryCode);

  /**
   * 获取题目类型分布
   *
   * @param categoryCode 分类代码
   * @return 类型分布
   */
  List<Map<String, Object>> getQuestionTypeDistribution(String categoryCode);

  /**
   * 获取难度分布
   *
   * @param categoryCode 分类代码
   * @return 难度分布
   */
  List<Map<String, Object>> getDifficultyDistribution(String categoryCode);

  /**
   * 获取知识点来源分布
   *
   * @param categoryCode 分类代码
   * @return 知识点来源分布
   */
  List<Map<String, Object>> getKnowledgeSourceDistribution(String categoryCode);

  /**
   * 创建题目
   *
   * @param question 题目信息
   * @return 创建的题目
   */
  Question createQuestion(Question question);

  /**
   * 更新题目
   *
   * @param questionId 题目ID
   * @param question 题目信息
   * @return 更新的题目
   */
  Question updateQuestion(Long questionId, Question question);

  /**
   * 删除题目
   *
   * @param questionId 题目ID
   * @return 是否成功
   */
  boolean deleteQuestion(Long questionId);

  /**
   * 审核题目
   *
   * @param questionId 题目ID
   * @param status 审核状态
   * @return 是否成功
   */
  boolean reviewQuestion(Long questionId, Question.QuestionStatus status);

  /**
   * 批量审核题目
   *
   * @param questionIds 题目ID列表
   * @param status 审核状态
   * @return 成功数量
   */
  int batchReviewQuestions(List<Long> questionIds, Question.QuestionStatus status);

  /**
   * 获取题目详情（包含选项解析）
   *
   * @param questionId 题目ID
   * @return 题目详情
   */
  Map<String, Object> getQuestionDetails(Long questionId);

  /**
   * 验证题目答案
   *
   * @param questionId 题目ID
   * @param userAnswer 用户答案
   * @return 验证结果
   */
  Map<String, Object> validateAnswer(Long questionId, String userAnswer);

  /**
   * 获取题目解析
   *
   * @param questionId 题目ID
   * @return 题目解析
   */
  Map<String, Object> getQuestionExplanation(Long questionId);

  /**
   * 获取相关题目
   *
   * @param questionId 题目ID
   * @param count 数量
   * @return 相关题目列表
   */
  List<Question> getRelatedQuestions(Long questionId, int count);

  /**
   * 获取题目标签
   *
   * @param questionId 题目ID
   * @return 标签列表
   */
  List<String> getQuestionTags(Long questionId);

  /**
   * 更新题目标签
   *
   * @param questionId 题目ID
   * @param tags 标签列表
   * @return 是否成功
   */
  boolean updateQuestionTags(Long questionId, List<String> tags);

  /**
   * 获取题目作者
   *
   * @param questionId 题目ID
   * @return 作者信息
   */
  Map<String, Object> getQuestionAuthor(Long questionId);

  /**
   * 获取题目创建时间
   *
   * @param questionId 题目ID
   * @return 创建时间
   */
  String getQuestionCreatedTime(Long questionId);

  /**
   * 获取题目更新时间
   *
   * @param questionId 题目ID
   * @return 更新时间
   */
  String getQuestionUpdatedTime(Long questionId);

  /**
   * 检查题目是否存在
   *
   * @param questionId 题目ID
   * @return 是否存在
   */
  boolean existsQuestion(Long questionId);

  /**
   * 检查题目是否已审核
   *
   * @param questionId 题目ID
   * @return 是否已审核
   */
  boolean isQuestionApproved(Long questionId);

  /**
   * 获取题目状态
   *
   * @param questionId 题目ID
   * @return 题目状态
   */
  Question.QuestionStatus getQuestionStatus(Long questionId);

  /**
   * 获取题目分类
   *
   * @param questionId 题目ID
   * @return 分类代码
   */
  String getQuestionCategory(Long questionId);

  /**
   * 获取题目类型
   *
   * @param questionId 题目ID
   * @return 题目类型
   */
  Question.QuestionType getQuestionType(Long questionId);

  /**
   * 获取题目难度
   *
   * @param questionId 题目ID
   * @return 题目难度
   */
  Question.Difficulty getQuestionDifficulty(Long questionId);

  /**
   * 获取题目知识点来源
   *
   * @param questionId 题目ID
   * @return 知识点来源
   */
  Question.KnowledgeSource getQuestionKnowledgeSource(Long questionId);
}
