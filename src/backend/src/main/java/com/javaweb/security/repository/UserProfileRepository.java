package com.javaweb.security.repository;

import com.javaweb.security.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户配置文件数据访问层
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  /** 根据用户ID查找用户配置文件 */
  Optional<UserProfile> findByUserId(Long userId);

  /** 根据技能水平查找用户配置文件 */
  List<UserProfile> findBySkillLevel(UserProfile.SkillLevel skillLevel);

  /** 根据职业背景查找用户配置文件 */
  List<UserProfile> findByProfessionalBackground(String professionalBackground);

  /** 根据国家查找用户配置文件 */
  List<UserProfile> findByCountry(String country);

  /** 查找学习时间排行榜（前N名） */
  @Query("SELECT up FROM UserProfile up ORDER BY up.totalStudyTime DESC")
  List<UserProfile> findTopByStudyTime(@Param("limit") int limit);

  /** 查找积分排行榜（前N名） */
  @Query("SELECT up FROM UserProfile up ORDER BY up.totalPoints DESC")
  List<UserProfile> findTopByPoints(@Param("limit") int limit);

  /** 查找完成漏洞数量排行榜 */
  @Query("SELECT up FROM UserProfile up ORDER BY up.completedVulnerabilities DESC")
  List<UserProfile> findTopByCompletedVulnerabilities(@Param("limit") int limit);

  /** 查找连续学习天数排行榜 */
  @Query("SELECT up FROM UserProfile up ORDER BY up.currentStreak DESC")
  List<UserProfile> findTopByCurrentStreak(@Param("limit") int limit);

  /** 统计不同技能水平的用户数量 */
  @Query("SELECT up.skillLevel, COUNT(up) FROM UserProfile up GROUP BY up.skillLevel")
  List<Object[]> countUsersBySkillLevel();

  /** 统计不同国家的用户数量 */
  @Query(
      "SELECT up.country, COUNT(up) FROM UserProfile up WHERE up.country IS NOT NULL GROUP BY up.country ORDER BY COUNT(up) DESC")
  List<Object[]> countUsersByCountry();

  /** 计算平均学习时间 */
  @Query("SELECT AVG(up.totalStudyTime) FROM UserProfile up")
  Double getAverageStudyTime();

  /** 计算平均积分 */
  @Query("SELECT AVG(up.totalPoints) FROM UserProfile up")
  Double getAveragePoints();

  /** 更新用户学习统计数据 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET "
          + "up.totalStudyTime = up.totalStudyTime + :additionalTime, "
          + "up.totalPoints = up.totalPoints + :additionalPoints "
          + "WHERE up.userId = :userId")
  void updateStudyStats(
      @Param("userId") Long userId,
      @Param("additionalTime") Long additionalTime,
      @Param("additionalPoints") Integer additionalPoints);

  /** 增加完成的漏洞数量 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET up.completedVulnerabilities = up.completedVulnerabilities + 1 "
          + "WHERE up.userId = :userId")
  void incrementCompletedVulnerabilities(@Param("userId") Long userId);

  /** 增加通过的测试数量 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET up.passedTests = up.passedTests + 1 "
          + "WHERE up.userId = :userId")
  void incrementPassedTests(@Param("userId") Long userId);

  /** 增加完成的挑战数量 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET up.completedChallenges = up.completedChallenges + 1 "
          + "WHERE up.userId = :userId")
  void incrementCompletedChallenges(@Param("userId") Long userId);

  /** 增加获得的徽章数量 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET up.earnedBadges = up.earnedBadges + 1 "
          + "WHERE up.userId = :userId")
  void incrementEarnedBadges(@Param("userId") Long userId);

  /** 更新学习连续天数 */
  @Modifying
  @Query(
      "UPDATE UserProfile up SET "
          + "up.currentStreak = :currentStreak, "
          + "up.longestStreak = CASE WHEN :currentStreak > up.longestStreak THEN :currentStreak ELSE up.longestStreak END "
          + "WHERE up.userId = :userId")
  void updateStreak(@Param("userId") Long userId, @Param("currentStreak") Integer currentStreak);

  /** 查找学习活跃用户（根据学习时间） */
  @Query(
      "SELECT up FROM UserProfile up WHERE up.totalStudyTime > :minStudyTime ORDER BY up.totalStudyTime DESC")
  List<UserProfile> findActiveStudents(@Param("minStudyTime") Long minStudyTime);

  /** 查找优秀学员（综合评分） */
  @Query(
      "SELECT up FROM UserProfile up WHERE "
          + "up.totalPoints >= :minPoints AND "
          + "up.completedVulnerabilities >= :minVulns AND "
          + "up.passedTests >= :minTests "
          + "ORDER BY (up.totalPoints + up.completedVulnerabilities * 10 + up.passedTests * 5) DESC")
  List<UserProfile> findExcellentStudents(
      @Param("minPoints") Integer minPoints,
      @Param("minVulns") Integer minVulns,
      @Param("minTests") Integer minTests);

  /** 检查用户配置文件是否存在 */
  boolean existsByUserId(Long userId);
}
