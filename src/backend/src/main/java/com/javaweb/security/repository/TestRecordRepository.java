package com.javaweb.security.repository;

import com.javaweb.security.entity.TestRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 测试记录Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface TestRecordRepository extends JpaRepository<TestRecord, Long> {

  /** 根据用户ID查找测试记录 */
  Page<TestRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  /** 根据用户ID和分类查找测试记录 */
  Page<TestRecord> findByUserIdAndCategoryCodeOrderByCreatedAtDesc(
      Long userId, String categoryCode, Pageable pageable);

  /** 根据用户ID查找通过的测试记录 */
  Page<TestRecord> findByUserIdAndIsPassedTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

  /** 统计用户的测试次数 */
  @Query("SELECT COUNT(t) FROM TestRecord t WHERE t.userId = :userId")
  Long countByUserId(@Param("userId") Long userId);

  /** 统计用户的通过次数 */
  @Query("SELECT COUNT(t) FROM TestRecord t WHERE t.userId = :userId AND t.isPassed = true")
  Long countPassedByUserId(@Param("userId") Long userId);

  /** 统计用户的平均分数 */
  @Query("SELECT AVG(t.percentage) FROM TestRecord t WHERE t.userId = :userId")
  Double getAverageScoreByUserId(@Param("userId") Long userId);

  /** 获取用户的最佳成绩 */
  @Query("SELECT MAX(t.percentage) FROM TestRecord t WHERE t.userId = :userId")
  Double getBestScoreByUserId(@Param("userId") Long userId);

  /** 获取用户最近N天的测试记录 */
  @Query(
      "SELECT t FROM TestRecord t WHERE t.userId = :userId AND t.createdAt >= :startDate ORDER BY t.createdAt DESC")
  List<TestRecord> findRecentRecordsByUserId(
      @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

  /** 获取分类的测试统计 */
  @Query(
      "SELECT t.categoryCode, COUNT(t), AVG(t.percentage), MAX(t.percentage) "
          + "FROM TestRecord t WHERE t.userId = :userId GROUP BY t.categoryCode")
  List<Object[]> getCategoryStatsByUserId(@Param("userId") Long userId);

  /** 获取所有分类的测试统计 */
  @Query(
      "SELECT t.categoryCode, COUNT(t), AVG(t.percentage), MAX(t.percentage), "
          + "SUM(CASE WHEN t.isPassed = true THEN 1 ELSE 0 END) "
          + "FROM TestRecord t GROUP BY t.categoryCode")
  List<Object[]> getOverallCategoryStats();
}
