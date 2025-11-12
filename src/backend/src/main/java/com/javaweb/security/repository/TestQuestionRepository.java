package com.javaweb.security.repository;

import com.javaweb.security.entity.TestQuestion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 测试题目Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

  /** 根据分类查找题目 */
  Page<TestQuestion> findByCategoryCodeAndIsActiveTrue(String categoryCode, Pageable pageable);

  /** 根据难度查找题目 */
  Page<TestQuestion> findByDifficultyLevelAndIsActiveTrue(
      String difficultyLevel, Pageable pageable);

  /** 根据分类和难度查找题目 */
  Page<TestQuestion> findByCategoryCodeAndDifficultyLevelAndIsActiveTrue(
      String categoryCode, String difficultyLevel, Pageable pageable);

  /** 随机获取指定数量的题目 */
  @Query("SELECT t FROM TestQuestion t WHERE t.isActive = true ORDER BY RAND()")
  List<TestQuestion> findRandomQuestions(Pageable pageable);

  /** 根据分类随机获取指定数量的题目 */
  @Query(
      "SELECT t FROM TestQuestion t WHERE t.categoryCode = :categoryCode AND t.isActive = true ORDER BY RAND()")
  List<TestQuestion> findRandomQuestionsByCategory(
      @Param("categoryCode") String categoryCode, Pageable pageable);

  /** 统计分类下的题目数量 */
  @Query(
      "SELECT COUNT(t) FROM TestQuestion t WHERE t.categoryCode = :categoryCode AND t.isActive = true")
  Long countByCategoryCode(@Param("categoryCode") String categoryCode);

  /** 统计难度下的题目数量 */
  @Query(
      "SELECT COUNT(t) FROM TestQuestion t WHERE t.difficultyLevel = :difficultyLevel AND t.isActive = true")
  Long countByDifficultyLevel(@Param("difficultyLevel") String difficultyLevel);
}
