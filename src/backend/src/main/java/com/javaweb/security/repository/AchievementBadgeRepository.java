package com.javaweb.security.repository;

import com.javaweb.security.entity.AchievementBadge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 成就徽章Repository接口 */
@Repository
public interface AchievementBadgeRepository extends JpaRepository<AchievementBadge, Long> {

  /** 根据徽章代码查找徽章 */
  Optional<AchievementBadge> findByBadgeCode(String badgeCode);

  /** 根据分类查找徽章 */
  List<AchievementBadge> findByBadgeCategory(String badgeCategory);

  /** 查找活跃的徽章 */
  List<AchievementBadge> findByIsActiveTrue();

  /** 根据分类查找活跃的徽章 */
  List<AchievementBadge> findByBadgeCategoryAndIsActiveTrue(String badgeCategory);

  /** 根据稀有度查找徽章 */
  List<AchievementBadge> findByBadgeRarity(String badgeRarity);

  /** 查找用户未获得的徽章 */
  @Query(
      "SELECT b FROM AchievementBadge b WHERE b.isActive = true AND b.id NOT IN "
          + "(SELECT ub.badgeId FROM UserBadge ub WHERE ub.userId = :userId)")
  List<AchievementBadge> findUnearnedBadgesByUserId(@Param("userId") Long userId);

  /** 根据分类查找用户未获得的徽章 */
  @Query(
      "SELECT b FROM AchievementBadge b WHERE b.isActive = true AND b.badgeCategory = :category "
          + "AND b.id NOT IN (SELECT ub.badgeId FROM UserBadge ub WHERE ub.userId = :userId)")
  List<AchievementBadge> findUnearnedBadgesByUserIdAndCategory(
      @Param("userId") Long userId, @Param("category") String category);
}
