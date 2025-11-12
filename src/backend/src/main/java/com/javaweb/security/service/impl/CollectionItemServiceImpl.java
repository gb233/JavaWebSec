package com.javaweb.security.service.impl;

import com.javaweb.security.entity.Collection;
import com.javaweb.security.entity.CollectionItem;
import com.javaweb.security.repository.CollectionItemRepository;
import com.javaweb.security.repository.CollectionRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.CollectionItemService;
import com.javaweb.security.service.CollectionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 收藏项服务实现类 */
@Slf4j
@Service
@Transactional
public class CollectionItemServiceImpl implements CollectionItemService {

  @Autowired private CollectionItemRepository collectionItemRepository;
  @Autowired private CollectionRepository collectionRepository;
  @Autowired private BadgeDetectionService badgeDetectionService;
  @Autowired private CollectionService collectionService;

  @Override
  public CollectionItem addCollectionItem(CollectionItem collectionItem) {
    // 检查是否已存在
    if (collectionItemRepository.existsByCollectionIdAndItemTypeAndItemId(
        collectionItem.getCollectionId(),
        collectionItem.getItemType(),
        collectionItem.getItemId())) {
      throw new RuntimeException("收藏项已存在");
    }
    CollectionItem savedItem = collectionItemRepository.save(collectionItem);

    // 检测收藏家徽章（通过collectionId获取userId）
    try {
      collectionRepository
          .findById(collectionItem.getCollectionId())
          .ifPresent(
              collection -> {
                Long userId = collection.getUserId();
                Map<String, Object> data = new HashMap<>();
                data.put("collectionItemId", savedItem.getId());
                data.put("collectionId", savedItem.getCollectionId());
                badgeDetectionService.checkSpecialBadges(userId, "COLLECTION_ADDED", data);
                log.debug("检测收藏家徽章: userId={}, collectionItemId={}", userId, savedItem.getId());
              });
    } catch (Exception e) {
      log.error("检测收藏家徽章失败: error={}", e.getMessage(), e);
      // 不抛出异常，避免影响收藏流程
    }

    return savedItem;
  }

  @Override
  public CollectionItem updateCollectionItem(Long id, CollectionItem collectionItem) {
    Optional<CollectionItem> existingItem = collectionItemRepository.findById(id);
    if (existingItem.isPresent()) {
      CollectionItem existing = existingItem.get();
      existing.setItemTitle(collectionItem.getItemTitle());
      existing.setItemDescription(collectionItem.getItemDescription());
      existing.setItemUrl(collectionItem.getItemUrl());
      existing.setItemMetadata(collectionItem.getItemMetadata());
      return collectionItemRepository.save(existing);
    }
    throw new RuntimeException("收藏项不存在");
  }

  @Override
  public void deleteCollectionItem(Long id) {
    collectionItemRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CollectionItem> getCollectionItemById(Long id) {
    return collectionItemRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> getCollectionItemsByCollectionId(Long collectionId) {
    return collectionItemRepository.findByCollectionId(collectionId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CollectionItem> getCollectionItemsByCollectionId(
      Long collectionId, Pageable pageable) {
    return collectionItemRepository.findByCollectionId(collectionId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> getCollectionItemsByCollectionIdAndType(
      Long collectionId, String itemType) {
    return collectionItemRepository.findByCollectionIdAndItemType(collectionId, itemType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> getCollectionItemsByUserId(Long userId) {
    return collectionItemRepository.findByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> getCollectionItemsByUserIdAndType(Long userId, String itemType) {
    return collectionItemRepository.findByUserIdAndItemType(userId, itemType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> searchCollectionItems(Long collectionId, String keyword) {
    List<CollectionItem> titleResults =
        collectionItemRepository.findByCollectionIdAndTitleContaining(collectionId, keyword);
    List<CollectionItem> descriptionResults =
        collectionItemRepository.findByCollectionIdAndDescriptionContaining(collectionId, keyword);

    // 合并结果并去重
    titleResults.addAll(descriptionResults);
    return titleResults.stream().distinct().toList();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByCollectionIdAndItemTypeAndItemId(
      Long collectionId, String itemType, Long itemId) {
    return collectionItemRepository.existsByCollectionIdAndItemTypeAndItemId(
        collectionId, itemType, itemId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getCollectionItemStats(Long collectionId) {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalItems", collectionItemRepository.countByCollectionId(collectionId));

    // 按类型统计
    stats.put(
        "vulnerabilityItems",
        collectionItemRepository.countByCollectionIdAndItemType(collectionId, "vulnerability"));
    stats.put(
        "noteItems", collectionItemRepository.countByCollectionIdAndItemType(collectionId, "note"));
    stats.put(
        "challengeItems",
        collectionItemRepository.countByCollectionIdAndItemType(collectionId, "challenge"));
    stats.put(
        "testItems", collectionItemRepository.countByCollectionIdAndItemType(collectionId, "test"));

    return stats;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getUserCollectionItemStats(Long userId) {
    Map<String, Long> stats = new HashMap<>();
    List<CollectionItem> userItems = collectionItemRepository.findByUserId(userId);
    stats.put("totalItems", (long) userItems.size());

    // 按类型统计
    stats.put(
        "vulnerabilityItems",
        (long)
            userItems.stream().filter(item -> "vulnerability".equals(item.getItemType())).count());
    stats.put(
        "noteItems",
        (long) userItems.stream().filter(item -> "note".equals(item.getItemType())).count());
    stats.put(
        "challengeItems",
        (long) userItems.stream().filter(item -> "challenge".equals(item.getItemType())).count());
    stats.put(
        "testItems",
        (long) userItems.stream().filter(item -> "test".equals(item.getItemType())).count());

    return stats;
  }

  @Override
  @Transactional(readOnly = true)
  public List<CollectionItem> getRecentCollectionItemsByUserId(Long userId, Pageable pageable) {
    return collectionItemRepository.findRecentByUserId(userId, pageable);
  }

  @Override
  public List<CollectionItem> batchAddCollectionItems(
      Long collectionId, List<CollectionItem> items) {
    for (CollectionItem item : items) {
      item.setCollectionId(collectionId);
    }
    return collectionItemRepository.saveAll(items);
  }

  @Override
  public void batchDeleteCollectionItems(List<Long> itemIds) {
    collectionItemRepository.deleteAllById(itemIds);
  }

  @Override
  public CollectionItem moveCollectionItem(Long itemId, Long targetCollectionId) {
    Optional<CollectionItem> item = collectionItemRepository.findById(itemId);
    if (item.isPresent()) {
      CollectionItem collectionItem = item.get();
      collectionItem.setCollectionId(targetCollectionId);
      return collectionItemRepository.save(collectionItem);
    }
    throw new RuntimeException("收藏项不存在");
  }

  @Override
  public CollectionItem copyCollectionItem(Long itemId, Long targetCollectionId) {
    Optional<CollectionItem> item = collectionItemRepository.findById(itemId);
    if (item.isPresent()) {
      CollectionItem originalItem = item.get();
      CollectionItem newItem = new CollectionItem();
      newItem.setCollectionId(targetCollectionId);
      newItem.setItemType(originalItem.getItemType());
      newItem.setItemId(originalItem.getItemId());
      newItem.setItemTitle(originalItem.getItemTitle());
      newItem.setItemDescription(originalItem.getItemDescription());
      newItem.setItemUrl(originalItem.getItemUrl());
      newItem.setItemMetadata(originalItem.getItemMetadata());
      return collectionItemRepository.save(newItem);
    }
    throw new RuntimeException("收藏项不存在");
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasItemPermission(Long userId, Long itemId) {
    // 通过收藏项ID查找收藏项，然后检查所属收藏夹是否属于该用户
    Optional<CollectionItem> item = collectionItemRepository.findById(itemId);
    if (!item.isPresent()) {
      return false;
    }
    Long collectionId = item.get().getCollectionId();
    return collectionRepository.findByIdAndUserId(collectionId, userId).isPresent();
  }

  @Override
  public CollectionItem quickAddItem(
      Long userId,
      String itemType,
      Long itemId,
      String itemTitle,
      String itemDescription,
      String itemUrl,
      Long collectionId) {
    // 如果没有指定收藏夹，使用默认收藏夹
    Long targetCollectionId = collectionId;
    if (targetCollectionId == null) {
      Collection defaultCollection = collectionService.getOrCreateDefaultCollection(userId);
      targetCollectionId = defaultCollection.getId();
    } else {
      // 验证收藏夹是否属于该用户
      if (!collectionService.existsByIdAndUserId(targetCollectionId, userId)) {
        throw new RuntimeException("无权限操作此收藏夹");
      }
    }

    // 检查是否已存在
    if (collectionItemRepository.existsByCollectionIdAndItemTypeAndItemId(
        targetCollectionId, itemType, itemId)) {
      throw new RuntimeException("收藏项已存在");
    }

    // 创建收藏项
    CollectionItem collectionItem = new CollectionItem();
    collectionItem.setCollectionId(targetCollectionId);
    collectionItem.setItemType(itemType);
    collectionItem.setItemId(itemId);
    collectionItem.setItemTitle(itemTitle != null ? itemTitle : "");
    collectionItem.setItemDescription(itemDescription);
    collectionItem.setItemUrl(itemUrl);

    CollectionItem savedItem = collectionItemRepository.save(collectionItem);

    // 检测收藏家徽章
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("collectionItemId", savedItem.getId());
      data.put("collectionId", savedItem.getCollectionId());
      badgeDetectionService.checkSpecialBadges(userId, "COLLECTION_ADDED", data);
      log.debug("检测收藏家徽章: userId={}, collectionItemId={}", userId, savedItem.getId());
    } catch (Exception e) {
      log.error("检测收藏家徽章失败: error={}", e.getMessage(), e);
      // 不抛出异常，避免影响收藏流程
    }

    return savedItem;
  }
}
