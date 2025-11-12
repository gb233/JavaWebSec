package com.javaweb.security.service.impl;

import com.javaweb.security.entity.Collection;
import com.javaweb.security.repository.CollectionRepository;
import com.javaweb.security.service.CollectionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 收藏夹服务实现类 */
@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

  @Autowired private CollectionRepository collectionRepository;

  @Override
  public Collection createCollection(Collection collection) {
    // 如果是第一个收藏夹，设置为默认收藏夹
    List<Collection> existingCollections =
        collectionRepository.findByUserId(collection.getUserId());
    if (existingCollections.isEmpty()) {
      collection.setIsDefault(true);
    }
    return collectionRepository.save(collection);
  }

  @Override
  public Collection updateCollection(Long id, Collection collection) {
    Optional<Collection> existingCollection = collectionRepository.findById(id);
    if (existingCollection.isPresent()) {
      Collection existing = existingCollection.get();
      existing.setName(collection.getName());
      existing.setDescription(collection.getDescription());
      existing.setIsPublic(collection.getIsPublic());
      return collectionRepository.save(existing);
    }
    throw new RuntimeException("收藏夹不存在");
  }

  @Override
  public void deleteCollection(Long id) {
    collectionRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Collection> getCollectionById(Long id) {
    return collectionRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> getCollectionsByUserId(Long userId) {
    return collectionRepository.findByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Collection> getCollectionsByUserId(Long userId, Pageable pageable) {
    return collectionRepository.findByUserId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> getPublicCollectionsByUserId(Long userId) {
    return collectionRepository.findByUserIdAndIsPublic(userId, true);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Collection> getDefaultCollectionByUserId(Long userId) {
    return collectionRepository.findDefaultByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Collection> getPublicCollections(Pageable pageable) {
    return collectionRepository.findByIsPublicTrue(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> searchCollections(Long userId, String keyword) {
    List<Collection> nameResults =
        collectionRepository.findByUserIdAndNameContaining(userId, keyword);
    List<Collection> descriptionResults =
        collectionRepository.findByUserIdAndDescriptionContaining(userId, keyword);

    // 合并结果并去重
    nameResults.addAll(descriptionResults);
    return nameResults.stream().distinct().toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getCollectionStats(Long userId) {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalCollections", collectionRepository.countByUserId(userId));
    stats.put("publicCollections", collectionRepository.countByUserIdAndIsPublicTrue(userId));
    return stats;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getGlobalCollectionStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalPublicCollections", collectionRepository.countByIsPublicTrue());
    return stats;
  }

  @Override
  public Collection setDefaultCollection(Long userId, Long collectionId) {
    // 取消当前默认收藏夹
    Optional<Collection> currentDefault = collectionRepository.findDefaultByUserId(userId);
    if (currentDefault.isPresent()) {
      Collection defaultCollection = currentDefault.get();
      defaultCollection.setIsDefault(false);
      collectionRepository.save(defaultCollection);
    }

    // 设置新的默认收藏夹
    Optional<Collection> newDefault = collectionRepository.findByIdAndUserId(collectionId, userId);
    if (newDefault.isPresent()) {
      Collection collection = newDefault.get();
      collection.setIsDefault(true);
      return collectionRepository.save(collection);
    }
    throw new RuntimeException("收藏夹不存在或无权限");
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByIdAndUserId(Long id, Long userId) {
    return collectionRepository.findByIdAndUserId(id, userId).isPresent();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> getRecentCollectionsByUserId(Long userId, Pageable pageable) {
    return collectionRepository.findRecentByUserId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> getRecentlyUpdatedCollectionsByUserId(Long userId, Pageable pageable) {
    return collectionRepository.findRecentlyUpdatedByUserId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Collection> getPopularCollections(Pageable pageable) {
    return collectionRepository.findPopularCollections(pageable);
  }

  @Override
  public Collection ensureDefaultCollection(Long userId) {
    Optional<Collection> defaultCollection = collectionRepository.findDefaultByUserId(userId);
    if (defaultCollection.isPresent()) {
      return defaultCollection.get();
    }
    // 如果没有默认收藏夹，创建一个
    Collection newDefaultCollection = new Collection();
    newDefaultCollection.setUserId(userId);
    newDefaultCollection.setName("学习笔记");
    newDefaultCollection.setDescription("自动创建的学习笔记收藏夹");
    newDefaultCollection.setIsDefault(true);
    newDefaultCollection.setIsPublic(false);
    return collectionRepository.save(newDefaultCollection);
  }

  @Override
  public Collection getOrCreateDefaultCollection(Long userId) {
    return ensureDefaultCollection(userId);
  }
}
