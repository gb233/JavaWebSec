package com.javaweb.security.repository;

import com.javaweb.security.entity.UserBadge;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 用户徽章记录Repository接口 */
@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

  /** 根据用户ID查找用户徽章 */
  List<UserBadge> findByUserId(Long userId);

  /** 根据用户ID和徽章ID查找用户徽章 */
  Optional<UserBadge> findByUserIdAndBadgeId(Long userId, Long badgeId);

  /** 检查用户是否已获得徽章 */
  boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

  /** 根据用户ID查找显示的徽章 */
  List<UserBadge> findByUserIdAndIsDisplayedTrue(Long userId);

  /** 根据用户ID和分类查找徽章 */
  @Query(
      "SELECT ub FROM UserBadge ub JOIN AchievementBadge ab ON ub.badgeId = ab.id "
          + "WHERE ub.userId = :userId AND ab.badgeCategory = :category")
  List<UserBadge> findByUserIdAndCategory(
      @Param("userId") Long userId, @Param("category") String category);

  /** 根据用户ID查找最近获得的徽章 */
  List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);

  /** 根据用户ID查找指定时间后获得的徽章 */
  List<UserBadge> findByUserIdAndEarnedAtAfter(Long userId, LocalDateTime after);

  /** 统计用户徽章数量 */
  long countByUserId(Long userId);

  /** 根据分类统计用户徽章数量 */
  @Query(
      "SELECT COUNT(ub) FROM UserBadge ub JOIN AchievementBadge ab ON ub.badgeId = ab.id "
          + "WHERE ub.userId = :userId AND ab.badgeCategory = :category")
  long countByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

  /** 删除用户徽章 */
  void deleteByUserIdAndBadgeId(Long userId, Long badgeId);
}
