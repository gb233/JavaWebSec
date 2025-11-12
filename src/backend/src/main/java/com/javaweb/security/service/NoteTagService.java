package com.javaweb.security.service;

import com.javaweb.security.entity.NoteTag;
import java.util.List;

/** 笔记标签服务接口 */
public interface NoteTagService {

  /** 创建标签 */
  NoteTag createTag(NoteTag tag);

  /** 更新标签 */
  NoteTag updateTag(Long tagId, NoteTag tag);

  /** 删除标签 */
  void deleteTag(Long tagId);

  /** 根据ID获取标签 */
  NoteTag getTagById(Long tagId);

  /** 根据标签名获取标签 */
  NoteTag getTagByName(String tagName);

  /** 获取所有标签 */
  List<NoteTag> getAllTags();

  /** 获取系统标签 */
  List<NoteTag> getSystemTags();

  /** 获取用户标签 */
  List<NoteTag> getUserTags();

  /** 获取热门标签 */
  List<NoteTag> getPopularTags();

  /** 搜索标签 */
  List<NoteTag> searchTags(String keyword);

  /** 增加标签使用次数 */
  void incrementTagUsage(String tagName);

  /** 减少标签使用次数 */
  void decrementTagUsage(String tagName);

  /** 检查标签是否存在 */
  boolean tagExists(String tagName);

  /** 获取标签统计 */
  long getTagCount();

  /** 获取热门标签统计 */
  List<NoteTag> getMostUsedTags();

  /** 根据颜色获取标签 */
  List<NoteTag> getTagsByColor(String color);

  /** 删除未使用的标签 */
  void deleteUnusedTags();

  /** 根据标签名删除标签 */
  void deleteTagByName(String tagName);
}
