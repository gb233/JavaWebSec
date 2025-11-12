package com.javaweb.security.repository;

import com.javaweb.security.entity.TestAnswerDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 测试答题详情Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface TestAnswerDetailRepository extends JpaRepository<TestAnswerDetail, Long> {

  /** 根据测试记录ID查找答题详情 */
  List<TestAnswerDetail> findByTestRecordIdOrderById(Long testRecordId);

  /** 根据测试记录ID和题目ID查找答题详情 */
  TestAnswerDetail findByTestRecordIdAndQuestionId(Long testRecordId, Long questionId);

  /** 统计测试记录中的正确答题数 */
  @Query(
      "SELECT COUNT(t) FROM TestAnswerDetail t WHERE t.testRecordId = :testRecordId AND t.isCorrect = true")
  Long countCorrectAnswersByTestRecordId(@Param("testRecordId") Long testRecordId);

  /** 统计测试记录中的错误答题数 */
  @Query(
      "SELECT COUNT(t) FROM TestAnswerDetail t WHERE t.testRecordId = :testRecordId AND t.isCorrect = false")
  Long countWrongAnswersByTestRecordId(@Param("testRecordId") Long testRecordId);

  /** 统计测试记录的总得分 */
  @Query("SELECT SUM(t.pointsEarned) FROM TestAnswerDetail t WHERE t.testRecordId = :testRecordId")
  Integer getTotalPointsByTestRecordId(@Param("testRecordId") Long testRecordId);

  /** 获取用户的错题列表 */
  @Query(
      "SELECT DISTINCT t.questionId FROM TestAnswerDetail t "
          + "WHERE t.testRecordId IN (SELECT tr.id FROM TestRecord tr WHERE tr.userId = :userId) "
          + "AND t.isCorrect = false")
  List<Long> getWrongQuestionIdsByUserId(@Param("userId") Long userId);
}
