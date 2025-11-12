package com.javaweb.security.service;

import com.javaweb.security.entity.CollectionTag;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 收藏标签服务接口 */
public interface CollectionTagService {

  /** 创建标签 */
  CollectionTag createTag(CollectionTag tag);

  /** 更新标签 */
  CollectionTag updateTag(Long id, CollectionTag tag);

  /** 删除标签 */
  void deleteTag(Long id);

  /** 根据ID获取标签 */
  Optional<CollectionTag> getTagById(Long id);

  /** 根据名称获取标签 */
  Optional<CollectionTag> getTagByName(String name);

  /** 获取所有标签 */
  List<CollectionTag> getAllTags();

  /** 搜索标签 */
  List<CollectionTag> searchTags(String keyword);

  /** 获取热门标签 */
  List<CollectionTag> getPopularTags();

  /** 获取未使用的标签 */
  List<CollectionTag> getUnusedTags();

  /** 增加标签使用次数 */
  void incrementTagUsage(Long tagId);

  /** 减少标签使用次数 */
  void decrementTagUsage(Long tagId);

  /** 获取标签统计信息 */
  Map<String, Long> getTagStats();

  /** 批量创建标签 */
  List<CollectionTag> batchCreateTags(List<CollectionTag> tags);

  /** 清理未使用的标签 */
  void cleanupUnusedTags();
}
