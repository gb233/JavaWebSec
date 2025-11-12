package com.javaweb.security.service.impl;

import com.javaweb.security.entity.CollectionTag;
import com.javaweb.security.repository.CollectionTagRepository;
import com.javaweb.security.service.CollectionTagService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 收藏标签服务实现类 */
@Service
@Transactional
public class CollectionTagServiceImpl implements CollectionTagService {

  @Autowired private CollectionTagRepository collectionTagRepository;

  @Override
  public CollectionTag createTag(CollectionTag tag) {
    // 检查标签名是否已存在
    Optional<CollectionTag> existingTag = collectionTagRepository.findByName(tag.getName());
    if (existingTag.isPresent()) {
      throw new RuntimeException("标签名已存在");
    }
    return collectionTagRepository.save(tag);
  }

  @Override
  public CollectionTag updateTag(Long id, CollectionTag tag) {
    Optional<CollectionTag> existingTag = collectionTagRepository.findById(id);
    if (existingTag.isPresent()) {
      CollectionTag existing = existingTag.get();
      existing.setName(tag.getName());
      existing.setDescription(tag.getDescription());
      existing.setColor(tag.getColor());
      return collectionTagRepository.save(existing);
    }
    throw new RuntimeException("标签不存在");
  }

  @Override
  public void deleteTag(Long id) {
    collectionTagRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CollectionTag> getTagById(Long id) {
    return collectionTagRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CollectionTag> getTagByName(String name) {
    return collectionTagRepository.findByName(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionTag> getAllTags() {
    return collectionTagRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionTag> searchTags(String keyword) {
    return collectionTagRepository.searchByNameOrDescription(keyword);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionTag> getPopularTags() {
    return collectionTagRepository.findPopularTags();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionTag> getUnusedTags() {
    return collectionTagRepository.findUnusedTags();
  }

  @Override
  public void incrementTagUsage(Long tagId) {
    collectionTagRepository.incrementUsageCount(tagId);
  }

  @Override
  public void decrementTagUsage(Long tagId) {
    collectionTagRepository.decrementUsageCount(tagId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getTagStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalTags", collectionTagRepository.count());
    stats.put("unusedTags", (long) collectionTagRepository.findUnusedTags().size());
    stats.put("popularTags", (long) collectionTagRepository.findMostUsedTags().size());
    return stats;
  }

  @Override
  public List<CollectionTag> batchCreateTags(List<CollectionTag> tags) {
    return collectionTagRepository.saveAll(tags);
  }

  @Override
  public void cleanupUnusedTags() {
    List<CollectionTag> unusedTags = collectionTagRepository.findUnusedTags();
    collectionTagRepository.deleteAll(unusedTags);
  }
}
