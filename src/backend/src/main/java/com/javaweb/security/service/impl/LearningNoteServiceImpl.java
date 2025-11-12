package com.javaweb.security.service.impl;

import com.javaweb.security.entity.Collection;
import com.javaweb.security.entity.LearningNote;
import com.javaweb.security.repository.LearningNoteRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.CollectionItemService;
import com.javaweb.security.service.CollectionService;
import com.javaweb.security.service.LearningNoteService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 学习笔记服务实现类 */
@Slf4j
@Service
@Transactional
public class LearningNoteServiceImpl implements LearningNoteService {

  @Autowired private LearningNoteRepository noteRepository;
  @Autowired private BadgeDetectionService badgeDetectionService;
  @Autowired private CollectionService collectionService;
  @Autowired private CollectionItemService collectionItemService;

  @Override
  public LearningNote createNote(LearningNote note) {
    // 计算字数
    note.setWordCount(calculateWordCount(note.getContent()));
    // 计算阅读时间（按每分钟200字计算）
    note.setReadingTime((int) Math.ceil(note.getWordCount() / 200.0));
    LearningNote savedNote = noteRepository.save(note);

    // 检测笔记达人徽章
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("noteId", savedNote.getId());
      badgeDetectionService.checkSpecialBadges(note.getUserId(), "NOTE_CREATED", data);
      log.debug("检测笔记达人徽章: userId={}, noteId={}", note.getUserId(), savedNote.getId());
    } catch (Exception e) {
      log.error("检测笔记达人徽章失败: userId={}, error={}", note.getUserId(), e.getMessage(), e);
      // 不抛出异常，避免影响笔记创建流程
    }

    // 如果笔记关联了漏洞，自动添加到默认收藏夹
    if (savedNote.getVulnerabilityCode() != null && !savedNote.getVulnerabilityCode().isEmpty()) {
      try {
        // 获取或创建默认收藏夹
        Collection defaultCollection =
            collectionService.getOrCreateDefaultCollection(note.getUserId());

        // 构建笔记URL
        String noteUrl = "/notes/" + savedNote.getId();

        // 添加到收藏夹
        String description =
            savedNote.getSummary() != null
                ? savedNote.getSummary()
                : (savedNote.getContent() != null && !savedNote.getContent().isEmpty()
                    ? savedNote
                        .getContent()
                        .substring(0, Math.min(200, savedNote.getContent().length()))
                    : savedNote.getTitle());
        collectionItemService.quickAddItem(
            note.getUserId(),
            "note",
            savedNote.getId(),
            savedNote.getTitle(),
            description,
            noteUrl,
            defaultCollection.getId());
        log.debug(
            "笔记自动添加到收藏夹: userId={}, noteId={}, collectionId={}",
            note.getUserId(),
            savedNote.getId(),
            defaultCollection.getId());
      } catch (Exception e) {
        log.error(
            "笔记自动添加到收藏夹失败: userId={}, noteId={}, error={}",
            note.getUserId(),
            savedNote.getId(),
            e.getMessage(),
            e);
        // 不抛出异常，避免影响笔记创建流程
      }
    }

    return savedNote;
  }

  @Override
  public LearningNote updateNote(Long noteId, LearningNote note) {
    LearningNote existingNote =
        noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("笔记不存在"));

    existingNote.setTitle(note.getTitle());
    existingNote.setContent(note.getContent());
    existingNote.setSummary(note.getSummary());
    existingNote.setNoteType(note.getNoteType());
    existingNote.setVulnerabilityCode(note.getVulnerabilityCode());
    existingNote.setTags(note.getTags());
    existingNote.setIsPublic(note.getIsPublic());
    existingNote.setIsPinned(note.getIsPinned());

    // 重新计算字数和阅读时间
    existingNote.setWordCount(calculateWordCount(note.getContent()));
    existingNote.setReadingTime((int) Math.ceil(existingNote.getWordCount() / 200.0));

    return noteRepository.save(existingNote);
  }

  @Override
  public void deleteNote(Long noteId) {
    noteRepository.deleteById(noteId);
  }

  @Override
  public LearningNote getNoteById(Long noteId) {
    return noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("笔记不存在"));
  }

  @Override
  public List<LearningNote> getUserNotes(Long userId) {
    return noteRepository.findByUserId(userId);
  }

  @Override
  public Page<LearningNote> getUserNotes(Long userId, Pageable pageable) {
    return noteRepository.findByUserId(userId, pageable);
  }

  @Override
  public List<LearningNote> getUserNotesByType(Long userId, String noteType) {
    return noteRepository.findByUserIdAndNoteType(userId, noteType);
  }

  @Override
  public List<LearningNote> getUserNotesByVulnerability(Long userId, String vulnerabilityCode) {
    return noteRepository.findByUserIdAndVulnerabilityCode(userId, vulnerabilityCode);
  }

  @Override
  public List<LearningNote> getPublicNotes() {
    return noteRepository.findByIsPublicTrue();
  }

  @Override
  public Page<LearningNote> getPublicNotes(Pageable pageable) {
    return noteRepository.findByIsPublicTrue(pageable);
  }

  @Override
  public List<LearningNote> getPublicNotesByVulnerability(String vulnerabilityCode) {
    return noteRepository.findByIsPublicTrueAndVulnerabilityCode(vulnerabilityCode);
  }

  @Override
  public List<LearningNote> searchNotes(Long userId, String keyword) {
    return noteRepository.findByUserIdAndTitleContaining(userId, keyword);
  }

  @Override
  public List<LearningNote> fullTextSearchNotes(Long userId, String keyword) {
    return noteRepository.findByUserIdAndContentSearch(userId, keyword);
  }

  @Override
  public List<LearningNote> searchNotesByTag(Long userId, String tag) {
    return noteRepository.findByUserIdAndTag(userId, tag);
  }

  @Override
  public List<LearningNote> getPinnedNotes(Long userId) {
    return noteRepository.findByUserIdAndIsPinnedTrue(userId);
  }

  @Override
  public List<LearningNote> getRecentModifiedNotes(Long userId) {
    return noteRepository.findByUserIdOrderByLastModifiedAtDesc(userId);
  }

  @Override
  public List<LearningNote> getRecentCreatedNotes(Long userId) {
    return noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }

  @Override
  public List<LearningNote> getPopularNotes(Pageable pageable) {
    return noteRepository.findPopularNotes(pageable);
  }

  @Override
  public List<LearningNote> getLatestNotes(Pageable pageable) {
    return noteRepository.findLatestNotes(pageable);
  }

  @Override
  public void incrementViewCount(Long noteId) {
    LearningNote note =
        noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("笔记不存在"));
    note.setViewCount(note.getViewCount() + 1);
    noteRepository.save(note);
  }

  @Override
  public void togglePin(Long noteId, Boolean isPinned) {
    LearningNote note =
        noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("笔记不存在"));
    note.setIsPinned(isPinned);
    noteRepository.save(note);
  }

  @Override
  public void togglePublic(Long noteId, Boolean isPublic) {
    LearningNote note =
        noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("笔记不存在"));
    note.setIsPublic(isPublic);
    noteRepository.save(note);
  }

  @Override
  public long countUserNotes(Long userId) {
    return noteRepository.countByUserId(userId);
  }

  @Override
  public long countUserPublicNotes(Long userId) {
    return noteRepository.countByUserIdAndIsPublicTrue(userId);
  }

  @Override
  public Map<String, Object> getUserNoteStats(Long userId) {
    Map<String, Object> stats = new HashMap<>();

    // 总笔记数
    stats.put("totalNotes", noteRepository.countByUserId(userId));

    // 公开笔记数
    stats.put("publicNotes", noteRepository.countByUserIdAndIsPublicTrue(userId));

    // 按类型统计
    List<Object[]> typeStats = noteRepository.countByUserIdGroupByNoteType(userId);
    Map<String, Long> typeCounts = new HashMap<>();
    for (Object[] stat : typeStats) {
      typeCounts.put((String) stat[0], (Long) stat[1]);
    }
    stats.put("typeStats", typeCounts);

    // 按漏洞代码统计
    List<Object[]> vulnStats = noteRepository.countByUserIdGroupByVulnerabilityCode(userId);
    Map<String, Long> vulnCounts = new HashMap<>();
    for (Object[] stat : vulnStats) {
      vulnCounts.put((String) stat[0], (Long) stat[1]);
    }
    stats.put("vulnerabilityStats", vulnCounts);

    return stats;
  }

  @Override
  public Map<String, Object> getNoteStats() {
    Map<String, Object> stats = new HashMap<>();

    // 总笔记数
    stats.put("totalNotes", noteRepository.count());

    // 公开笔记数
    stats.put("publicNotes", noteRepository.countByIsPublicTrue());

    return stats;
  }

  @Override
  public boolean hasNotePermission(Long userId, Long noteId) {
    return noteRepository.existsByUserIdAndId(userId, noteId);
  }

  @Override
  public void deleteUserNotes(Long userId) {
    noteRepository.deleteByUserId(userId);
  }

  @Override
  public void deleteUserNotesByType(Long userId, String noteType) {
    noteRepository.deleteByUserIdAndNoteType(userId, noteType);
  }

  /** 计算字数 */
  private int calculateWordCount(String content) {
    if (content == null || content.isEmpty()) {
      return 0;
    }
    // 简单的中文字数计算，实际项目中可能需要更复杂的逻辑
    return content.length();
  }
}
