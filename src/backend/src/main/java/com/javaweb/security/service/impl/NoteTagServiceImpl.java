package com.javaweb.security.service.impl;

import com.javaweb.security.entity.NoteTag;
import com.javaweb.security.repository.NoteTagRepository;
import com.javaweb.security.service.NoteTagService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 笔记标签服务实现类 */
@Service
@Transactional
public class NoteTagServiceImpl implements NoteTagService {

  @Autowired private NoteTagRepository tagRepository;

  @Override
  public NoteTag createTag(NoteTag tag) {
    // 检查标签是否已存在
    if (tagRepository.existsByTagName(tag.getTagName())) {
      throw new RuntimeException("标签已存在");
    }
    return tagRepository.save(tag);
  }

  @Override
  public NoteTag updateTag(Long tagId, NoteTag tag) {
    NoteTag existingTag =
        tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("标签不存在"));

    existingTag.setTagName(tag.getTagName());
    existingTag.setTagDescription(tag.getTagDescription());
    existingTag.setColor(tag.getColor());
    existingTag.setIsSystem(tag.getIsSystem());

    return tagRepository.save(existingTag);
  }

  @Override
  public void deleteTag(Long tagId) {
    tagRepository.deleteById(tagId);
  }

  @Override
  public NoteTag getTagById(Long tagId) {
    return tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("标签不存在"));
  }

  @Override
  public NoteTag getTagByName(String tagName) {
    return tagRepository.findByTagName(tagName).orElseThrow(() -> new RuntimeException("标签不存在"));
  }

  @Override
  public List<NoteTag> getAllTags() {
    return tagRepository.findAll();
  }

  @Override
  public List<NoteTag> getSystemTags() {
    return tagRepository.findByIsSystemTrue();
  }

  @Override
  public List<NoteTag> getUserTags() {
    return tagRepository.findByIsSystemFalse();
  }

  @Override
  public List<NoteTag> getPopularTags() {
    return tagRepository.findByOrderByUsageCountDesc();
  }

  @Override
  public List<NoteTag> searchTags(String keyword) {
    return tagRepository.findByTagNameContainingIgnoreCase(keyword);
  }

  @Override
  public void incrementTagUsage(String tagName) {
    tagRepository.incrementUsageCount(tagName);
  }

  @Override
  public void decrementTagUsage(String tagName) {
    tagRepository.decrementUsageCount(tagName);
  }

  @Override
  public boolean tagExists(String tagName) {
    return tagRepository.existsByTagName(tagName);
  }

  @Override
  public long getTagCount() {
    return tagRepository.count();
  }

  @Override
  public List<NoteTag> getMostUsedTags() {
    return tagRepository.findMostUsedTags();
  }

  @Override
  public List<NoteTag> getTagsByColor(String color) {
    return tagRepository.findByColor(color);
  }

  @Override
  public void deleteUnusedTags() {
    tagRepository.deleteByUsageCountAndIsSystemFalse(0);
  }

  @Override
  public void deleteTagByName(String tagName) {
    tagRepository.deleteByTagName(tagName);
  }
}
