package com.javaweb.security.repository;

import com.javaweb.security.entity.BadgeRequirement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 徽章要求Repository接口 */
@Repository
public interface BadgeRequirementRepository extends JpaRepository<BadgeRequirement, Long> {

  /** 根据徽章ID查找要求 */
  List<BadgeRequirement> findByBadgeId(Long badgeId);

  /** 根据徽章ID和要求类型查找要求 */
  List<BadgeRequirement> findByBadgeIdAndRequirementType(Long badgeId, String requirementType);

  /** 根据徽章ID查找要求（按排序顺序） */
  List<BadgeRequirement> findByBadgeIdOrderBySortOrderAsc(Long badgeId);

  /** 根据要求类型查找要求 */
  List<BadgeRequirement> findByRequirementType(String requirementType);

  /** 统计徽章要求数量 */
  long countByBadgeId(Long badgeId);

  /** 根据徽章ID和要求类型统计数量 */
  long countByBadgeIdAndRequirementType(Long badgeId, String requirementType);

  /** 删除徽章的所有要求 */
  void deleteByBadgeId(Long badgeId);

  /** 根据徽章ID和要求类型删除要求 */
  void deleteByBadgeIdAndRequirementType(Long badgeId, String requirementType);
}
