package com.javaweb.security.repository;

import com.javaweb.security.entity.Question;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 题目数据访问层
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

  /**
   * 根据分类代码查找题目列表
   *
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> findByCategoryCodeOrderByCreatedAtDesc(String categoryCode, Pageable pageable);

  /**
   * 根据分类代码和题目类型查找题目列表
   *
   * @param categoryCode 分类代码
   * @param questionType 题目类型
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> findByCategoryCodeAndQuestionTypeOrderByCreatedAtDesc(
      String categoryCode, Question.QuestionType questionType, Pageable pageable);

  /**
   * 根据分类代码和难度查找题目列表
   *
   * @param categoryCode 分类代码
   * @param difficulty 难度等级
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> findByCategoryCodeAndDifficultyOrderByCreatedAtDesc(
      String categoryCode, Question.Difficulty difficulty, Pageable pageable);

  /**
   * 根据分类代码和知识点来源查找题目列表
   *
   * @param categoryCode 分类代码
   * @param knowledgeSource 知识点来源
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> findByCategoryCodeAndKnowledgeSourceOrderByCreatedAtDesc(
      String categoryCode, Question.KnowledgeSource knowledgeSource, Pageable pageable);

  /**
   * 根据分类代码和状态查找题目列表
   *
   * @param categoryCode 分类代码
   * @param status 题目状态
   * @param pageable 分页参数
   * @return 题目列表
   */
  Page<Question> findByCategoryCodeAndStatusOrderByCreatedAtDesc(
      String categoryCode, Question.QuestionStatus status, Pageable pageable);

  /**
   * 根据分类代码随机查找指定数量的题目
   *
   * @param categoryCode 分类代码
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE category_code = :categoryCode AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByCategory(
      @Param("categoryCode") String categoryCode, @Param("limit") int limit);

  /**
   * 根据分类代码和题目类型随机查找指定数量的题目
   *
   * @param categoryCode 分类代码
   * @param questionType 题目类型
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE category_code = :categoryCode AND question_type = :questionType AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByCategoryAndType(
      @Param("categoryCode") String categoryCode,
      @Param("questionType") String questionType,
      @Param("limit") int limit);

  /**
   * 根据分类代码和难度随机查找指定数量的题目
   *
   * @param categoryCode 分类代码
   * @param difficulty 难度等级
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE category_code = :categoryCode AND difficulty = :difficulty AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByCategoryAndDifficulty(
      @Param("categoryCode") String categoryCode,
      @Param("difficulty") String difficulty,
      @Param("limit") int limit);

  /**
   * 根据分类代码和知识点来源随机查找指定数量的题目
   *
   * @param categoryCode 分类代码
   * @param knowledgeSource 知识点来源
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE category_code = :categoryCode AND knowledge_source = :knowledgeSource AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByCategoryAndKnowledgeSource(
      @Param("categoryCode") String categoryCode,
      @Param("knowledgeSource") String knowledgeSource,
      @Param("limit") int limit);

  /**
   * 跨分类随机查找指定数量的题目
   *
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsAcrossCategories(@Param("limit") int limit);

  /**
   * 根据题目类型跨分类随机查找指定数量的题目
   *
   * @param questionType 题目类型
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE question_type = :questionType AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByTypeAcrossCategories(
      @Param("questionType") String questionType, @Param("limit") int limit);

  /**
   * 根据难度跨分类随机查找指定数量的题目
   *
   * @param difficulty 难度等级
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE difficulty = :difficulty AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByDifficultyAcrossCategories(
      @Param("difficulty") String difficulty, @Param("limit") int limit);

  /**
   * 统计分类的题目数量
   *
   * @param categoryCode 分类代码
   * @return 题目数量
   */
  long countByCategoryCode(String categoryCode);

  /**
   * 统计分类指定类型的题目数量
   *
   * @param categoryCode 分类代码
   * @param questionType 题目类型
   * @return 题目数量
   */
  long countByCategoryCodeAndQuestionType(String categoryCode, Question.QuestionType questionType);

  /**
   * 统计分类指定难度的题目数量
   *
   * @param categoryCode 分类代码
   * @param difficulty 难度等级
   * @return 题目数量
   */
  long countByCategoryCodeAndDifficulty(String categoryCode, Question.Difficulty difficulty);

  /**
   * 统计分类指定知识点来源的题目数量
   *
   * @param categoryCode 分类代码
   * @param knowledgeSource 知识点来源
   * @return 题目数量
   */
  long countByCategoryCodeAndKnowledgeSource(
      String categoryCode, Question.KnowledgeSource knowledgeSource);

  /**
   * 统计分类指定状态的题目数量
   *
   * @param categoryCode 分类代码
   * @param status 题目状态
   * @return 题目数量
   */
  long countByCategoryCodeAndStatus(String categoryCode, Question.QuestionStatus status);

  /**
   * 查找分类的题目类型分布
   *
   * @param categoryCode 分类代码
   * @return 题目类型分布
   */
  @Query(
      "SELECT q.questionType, COUNT(q) FROM Question q WHERE q.categoryCode = :categoryCode AND q.status = 'APPROVED' GROUP BY q.questionType")
  List<Object[]> getQuestionTypeDistributionByCategory(@Param("categoryCode") String categoryCode);

  /**
   * 查找分类的难度分布
   *
   * @param categoryCode 分类代码
   * @return 难度分布
   */
  @Query(
      "SELECT q.difficulty, COUNT(q) FROM Question q WHERE q.categoryCode = :categoryCode AND q.status = 'APPROVED' GROUP BY q.difficulty")
  List<Object[]> getDifficultyDistributionByCategory(@Param("categoryCode") String categoryCode);

  /**
   * 查找分类的知识点来源分布
   *
   * @param categoryCode 分类代码
   * @return 知识点来源分布
   */
  @Query(
      "SELECT q.knowledgeSource, COUNT(q) FROM Question q WHERE q.categoryCode = :categoryCode AND q.status = 'APPROVED' GROUP BY q.knowledgeSource")
  List<Object[]> getKnowledgeSourceDistributionByCategory(
      @Param("categoryCode") String categoryCode);

  /**
   * 根据关键词搜索题目
   *
   * @param keyword 关键词
   * @param pageable 分页参数
   * @return 题目列表
   */
  @Query(
      "SELECT q FROM Question q WHERE (q.questionText LIKE %:keyword% OR q.explanation LIKE %:keyword%) AND q.status = 'APPROVED'")
  Page<Question> searchQuestionsByKeyword(@Param("keyword") String keyword, Pageable pageable);

  /**
   * 根据标签搜索题目
   *
   * @param tag 标签
   * @param pageable 分页参数
   * @return 题目列表
   */
  @Query("SELECT q FROM Question q WHERE q.tags LIKE %:tag% AND q.status = 'APPROVED'")
  Page<Question> searchQuestionsByTag(@Param("tag") String tag, Pageable pageable);

  /**
   * 根据知识点来源跨分类随机查找指定数量的题目
   *
   * @param knowledgeSource 知识点来源
   * @param limit 题目数量
   * @return 题目列表
   */
  @Query(
      value =
          "SELECT * FROM vulnerability_questions WHERE knowledge_source = :knowledgeSource AND status = 'APPROVED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Question> findRandomQuestionsByKnowledgeSourceAcrossCategories(
      @Param("knowledgeSource") String knowledgeSource, @Param("limit") int limit);
}
