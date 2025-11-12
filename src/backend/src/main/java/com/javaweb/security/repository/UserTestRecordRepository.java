package com.javaweb.security.repository;

import com.javaweb.security.entity.UserTestRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户测试记录数据访问层
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Repository
public interface UserTestRecordRepository extends JpaRepository<UserTestRecord, Long> {

  /**
   * 根据用户ID查找测试记录列表
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 测试记录列表
   */
  Page<UserTestRecord> findByUserIdOrderByCompletedAtDesc(Long userId, Pageable pageable);

  /**
   * 根据用户ID和模式代码查找测试记录列表
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @param pageable 分页参数
   * @return 测试记录列表
   */
  Page<UserTestRecord> findByUserIdAndModeCodeOrderByCompletedAtDesc(
      Long userId, String modeCode, Pageable pageable);

  /**
   * 根据用户ID和分类代码查找测试记录列表
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 测试记录列表
   */
  Page<UserTestRecord> findByUserIdAndCategoryCodeOrderByCompletedAtDesc(
      Long userId, String categoryCode, Pageable pageable);

  /**
   * 根据用户ID和模式代码和分类代码查找测试记录列表
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 测试记录列表
   */
  Page<UserTestRecord> findByUserIdAndModeCodeAndCategoryCodeOrderByCompletedAtDesc(
      Long userId, String modeCode, String categoryCode, Pageable pageable);

  /**
   * 根据用户ID查找最佳成绩记录
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 最佳成绩记录
   */
  @Query(
      "SELECT utr FROM UserTestRecord utr WHERE utr.userId = :userId AND utr.categoryCode = :categoryCode ORDER BY utr.totalScore DESC, utr.correctCount DESC")
  List<UserTestRecord> findBestScoreByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode);

  /**
   * 根据用户ID查找最新测试记录
   *
   * @param userId 用户ID
   * @param limit 记录数量
   * @return 最新测试记录列表
   */
  @Query(
      "SELECT utr FROM UserTestRecord utr WHERE utr.userId = :userId ORDER BY utr.completedAt DESC")
  List<UserTestRecord> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);

  /**
   * 根据用户ID统计测试记录数量
   *
   * @param userId 用户ID
   * @return 测试记录数量
   */
  long countByUserId(Long userId);

  /**
   * 根据用户ID和模式代码统计测试记录数量
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @return 测试记录数量
   */
  long countByUserIdAndModeCode(Long userId, String modeCode);

  /**
   * 根据用户ID和分类代码统计测试记录数量
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 测试记录数量
   */
  long countByUserIdAndCategoryCode(Long userId, String categoryCode);

  /**
   * 根据用户ID统计总得分
   *
   * @param userId 用户ID
   * @return 总得分
   */
  @Query(
      "SELECT COALESCE(SUM(utr.totalScore), 0) FROM UserTestRecord utr WHERE utr.userId = :userId")
  Integer getTotalScoreByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计总正确题数
   *
   * @param userId 用户ID
   * @return 总正确题数
   */
  @Query(
      "SELECT COALESCE(SUM(utr.correctCount), 0) FROM UserTestRecord utr WHERE utr.userId = :userId")
  Integer getTotalCorrectCountByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计总答题数
   *
   * @param userId 用户ID
   * @return 总答题数
   */
  @Query(
      "SELECT COALESCE(SUM(utr.totalQuestions), 0) FROM UserTestRecord utr WHERE utr.userId = :userId")
  Integer getTotalQuestionsByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计平均得分
   *
   * @param userId 用户ID
   * @return 平均得分
   */
  @Query("SELECT AVG(utr.totalScore) FROM UserTestRecord utr WHERE utr.userId = :userId")
  BigDecimal getAverageScoreByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计平均正确率
   *
   * @param userId 用户ID
   * @return 平均正确率
   */
  @Query(
      "SELECT AVG(utr.correctCount * 100.0 / utr.totalQuestions) FROM UserTestRecord utr WHERE utr.userId = :userId")
  BigDecimal getAverageAccuracyByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计平均完成率
   *
   * @param userId 用户ID
   * @return 平均完成率
   */
  @Query("SELECT AVG(utr.completionRate) FROM UserTestRecord utr WHERE utr.userId = :userId")
  BigDecimal getAverageCompletionRateByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计平均用时
   *
   * @param userId 用户ID
   * @return 平均用时
   */
  @Query("SELECT AVG(utr.timeSpent) FROM UserTestRecord utr WHERE utr.userId = :userId")
  BigDecimal getAverageTimeSpentByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID和分类代码统计测试情况
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 测试统计
   */
  @Query(
      "SELECT COUNT(utr), AVG(utr.totalScore), AVG(utr.correctCount * 100.0 / utr.totalQuestions), AVG(utr.completionRate), AVG(utr.timeSpent) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId AND utr.categoryCode = :categoryCode")
  Object[] getStatisticsByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode);

  /**
   * 根据用户ID和模式代码统计测试情况
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @return 测试统计
   */
  @Query(
      "SELECT COUNT(utr), AVG(utr.totalScore), AVG(utr.correctCount * 100.0 / utr.totalQuestions), AVG(utr.completionRate), AVG(utr.timeSpent) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId AND utr.modeCode = :modeCode")
  Object[] getStatisticsByUserIdAndMode(
      @Param("userId") Long userId, @Param("modeCode") String modeCode);

  /**
   * 根据用户ID查找各分类的测试统计
   *
   * @param userId 用户ID
   * @return 各分类测试统计
   */
  @Query(
      "SELECT utr.categoryCode, COUNT(utr), AVG(utr.totalScore), AVG(utr.correctCount * 100.0 / utr.totalQuestions), AVG(utr.completionRate) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId GROUP BY utr.categoryCode")
  List<Object[]> getCategoryStatisticsByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查找各模式的测试统计
   *
   * @param userId 用户ID
   * @return 各模式测试统计
   */
  @Query(
      "SELECT utr.modeCode, COUNT(utr), AVG(utr.totalScore), AVG(utr.correctCount * 100.0 / utr.totalQuestions), AVG(utr.completionRate) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId GROUP BY utr.modeCode")
  List<Object[]> getModeStatisticsByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查找测试等级分布
   *
   * @param userId 用户ID
   * @return 测试等级分布
   */
  @Query(
      "SELECT CASE "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 90 THEN '优秀' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 80 THEN '良好' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 70 THEN '中等' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 60 THEN '及格' "
          + "ELSE '不及格' END, COUNT(utr) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId GROUP BY "
          + "CASE WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 90 THEN '优秀' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 80 THEN '良好' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 70 THEN '中等' "
          + "WHEN utr.correctCount * 100.0 / utr.totalQuestions >= 60 THEN '及格' "
          + "ELSE '不及格' END")
  List<Object[]> getTestLevelDistributionByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查找学习进度趋势
   *
   * @param userId 用户ID
   * @param limit 记录数量
   * @return 学习进度趋势
   */
  @Query(
      "SELECT utr.completedAt, utr.totalScore, utr.correctCount * 100.0 / utr.totalQuestions, utr.completionRate "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId ORDER BY utr.completedAt DESC")
  List<Object[]> getLearningProgressByUserId(@Param("userId") Long userId, Pageable pageable);

  /**
   * 根据用户ID查找薄弱环节
   *
   * @param userId 用户ID
   * @return 薄弱环节统计
   */
  @Query(
      "SELECT utr.categoryCode, AVG(utr.correctCount * 100.0 / utr.totalQuestions) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId GROUP BY utr.categoryCode "
          + "HAVING AVG(utr.correctCount * 100.0 / utr.totalQuestions) < 70 ORDER BY AVG(utr.correctCount * 100.0 / utr.totalQuestions)")
  List<Object[]> getWeakAreasByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查找学习建议
   *
   * @param userId 用户ID
   * @return 学习建议
   */
  @Query(
      "SELECT utr.categoryCode, COUNT(utr), AVG(utr.correctCount * 100.0 / utr.totalQuestions), AVG(utr.completionRate) "
          + "FROM UserTestRecord utr WHERE utr.userId = :userId GROUP BY utr.categoryCode "
          + "ORDER BY AVG(utr.correctCount * 100.0 / utr.totalQuestions)")
  List<Object[]> getLearningSuggestionsByUserId(@Param("userId") Long userId);

  /**
   * 根据会话ID查找用户测试记录
   *
   * @param sessionId 会话ID
   * @return 用户测试记录
   */
  Optional<UserTestRecord> findBySessionId(Long sessionId);

  /**
   * 根据用户ID统计通过次数（完成率>=60%）
   *
   * @param userId 用户ID
   * @return 通过次数
   */
  @Query(
      "SELECT COUNT(utr) FROM UserTestRecord utr WHERE utr.userId = :userId AND utr.completionRate >= 60")
  long countPassedTestsByUserId(@Param("userId") Long userId);
}
