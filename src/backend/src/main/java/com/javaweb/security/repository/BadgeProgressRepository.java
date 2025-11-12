package com.javaweb.security.repository;

import com.javaweb.security.entity.BadgeProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 徽章进度Repository接口 */
@Repository
public interface BadgeProgressRepository extends JpaRepository<BadgeProgress, Long> {

  /** 根据用户ID查找徽章进度 */
  List<BadgeProgress> findByUserId(Long userId);

  /** 根据用户ID和徽章ID查找徽章进度 */
  Optional<BadgeProgress> findByUserIdAndBadgeId(Long userId, Long badgeId);

  /** 根据用户ID查找未完成的徽章进度 */
  List<BadgeProgress> findByUserIdAndIsCompletedFalse(Long userId);

  /** 根据用户ID查找已完成的徽章进度 */
  List<BadgeProgress> findByUserIdAndIsCompletedTrue(Long userId);

  /** 根据用户ID和分类查找徽章进度 */
  @Query(
      "SELECT bp FROM BadgeProgress bp JOIN AchievementBadge ab ON bp.badgeId = ab.id "
          + "WHERE bp.userId = :userId AND ab.badgeCategory = :category")
  List<BadgeProgress> findByUserIdAndCategory(
      @Param("userId") Long userId, @Param("category") String category);

  /** 根据用户ID查找未完成的徽章进度（按分类） */
  @Query(
      "SELECT bp FROM BadgeProgress bp JOIN AchievementBadge ab ON bp.badgeId = ab.id "
          + "WHERE bp.userId = :userId AND bp.isCompleted = false AND ab.badgeCategory = :category")
  List<BadgeProgress> findUncompletedByUserIdAndCategory(
      @Param("userId") Long userId, @Param("category") String category);

  /** 检查用户是否有徽章进度 */
  boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

  /** 统计用户未完成的徽章数量 */
  long countByUserIdAndIsCompletedFalse(Long userId);

  /** 统计用户已完成的徽章数量 */
  long countByUserIdAndIsCompletedTrue(Long userId);

  /** 根据分类统计用户未完成的徽章数量 */
  @Query(
      "SELECT COUNT(bp) FROM BadgeProgress bp JOIN AchievementBadge ab ON bp.badgeId = ab.id "
          + "WHERE bp.userId = :userId AND bp.isCompleted = false AND ab.badgeCategory = :category")
  long countUncompletedByUserIdAndCategory(
      @Param("userId") Long userId, @Param("category") String category);

  /** 删除用户徽章进度 */
  void deleteByUserIdAndBadgeId(Long userId, Long badgeId);
}
