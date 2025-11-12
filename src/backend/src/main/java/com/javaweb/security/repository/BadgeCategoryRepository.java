package com.javaweb.security.repository;

import com.javaweb.security.entity.BadgeCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 徽章分类Repository接口 */
@Repository
public interface BadgeCategoryRepository extends JpaRepository<BadgeCategory, Long> {

  /** 根据分类代码查找分类 */
  Optional<BadgeCategory> findByCategoryCode(String categoryCode);

  /** 查找活跃的分类 */
  List<BadgeCategory> findByIsActiveTrue();

  /** 根据排序顺序查找分类 */
  List<BadgeCategory> findByIsActiveTrueOrderBySortOrderAsc();

  /** 根据分类代码查找活跃的分类 */
  Optional<BadgeCategory> findByCategoryCodeAndIsActiveTrue(String categoryCode);

  /** 检查分类是否存在 */
  boolean existsByCategoryCode(String categoryCode);

  /** 检查活跃的分类是否存在 */
  boolean existsByCategoryCodeAndIsActiveTrue(String categoryCode);
}
