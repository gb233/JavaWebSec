package com.javaweb.security.service.impl;

import com.javaweb.security.dto.LanguagePreferenceDto;
import com.javaweb.security.entity.LanguagePreference;
import com.javaweb.security.repository.LanguagePreferenceRepository;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.LanguageService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 语言服务实现
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageServiceImpl implements LanguageService {

  private final LanguagePreferenceRepository languagePreferenceRepository;
  private final AuthenticationService authenticationService;

  // 支持的语言列表
  private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("zh-CN", "en-US");
  private static final String DEFAULT_LANGUAGE = "zh-CN";

  @Override
  public LanguagePreferenceDto getCurrentUserLanguage() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      log.warn("无法获取当前用户ID，返回默认语言");
      return getDefaultLanguagePreference();
    }

    try {
      Optional<LanguagePreference> preference =
          languagePreferenceRepository.findByUserIdAndActive(userId);
      if (preference.isPresent()) {
        return LanguagePreferenceDto.fromEntity(preference.get());
      }
    } catch (Exception e) {
      log.warn("获取用户语言偏好失败，可能存在重复记录: {}", e.getMessage());
      // 如果查询失败，尝试清理重复记录
      cleanupDuplicatePreferences(userId);
    }

    // 如果用户没有设置语言偏好，返回默认语言
    return getDefaultLanguagePreference();
  }

  @Override
  @Transactional
  public LanguagePreferenceDto setUserLanguage(String languageCode) {
    if (!isLanguageSupported(languageCode)) {
      throw new IllegalArgumentException("不支持的语言代码: " + languageCode);
    }

    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      throw new IllegalStateException("用户未登录");
    }

    // 查找现有偏好
    Optional<LanguagePreference> existingPreference =
        languagePreferenceRepository.findByUserIdAndLanguageCode(userId, languageCode);

    if (existingPreference.isPresent()) {
      // 如果偏好已存在，激活它
      LanguagePreference preference = existingPreference.get();
      preference.setIsActive(true);
      languagePreferenceRepository.save(preference);
      return LanguagePreferenceDto.fromEntity(preference);
    } else {
      // 创建新的语言偏好
      // 先停用其他活跃偏好
      languagePreferenceRepository
          .findByUserIdAndActive(userId)
          .ifPresent(
              pref -> {
                pref.setIsActive(false);
                languagePreferenceRepository.save(pref);
              });

      // 创建新偏好
      LanguagePreference newPreference =
          LanguagePreference.builder()
              .userId(userId)
              .languageCode(languageCode)
              .isActive(true)
              .build();

      LanguagePreference saved = languagePreferenceRepository.save(newPreference);
      log.info("用户 {} 设置语言为 {}", userId, languageCode);
      return LanguagePreferenceDto.fromEntity(saved);
    }
  }

  @Override
  public List<LanguagePreferenceDto> getSupportedLanguages() {
    return SUPPORTED_LANGUAGES.stream().map(this::createLanguagePreferenceDto).toList();
  }

  @Override
  public boolean isLanguageSupported(String languageCode) {
    return SUPPORTED_LANGUAGES.contains(languageCode);
  }

  @Override
  public String getDefaultLanguage() {
    return DEFAULT_LANGUAGE;
  }

  /** 获取默认语言偏好DTO */
  private LanguagePreferenceDto getDefaultLanguagePreference() {
    return createLanguagePreferenceDto(DEFAULT_LANGUAGE);
  }

  /** 创建语言偏好DTO */
  private LanguagePreferenceDto createLanguagePreferenceDto(String languageCode) {
    LanguagePreference entity =
        LanguagePreference.builder().languageCode(languageCode).isActive(true).build();

    return LanguagePreferenceDto.fromEntity(entity);
  }

  /** 清理重复的语言偏好记录 */
  @Transactional
  private void cleanupDuplicatePreferences(Long userId) {
    try {
      log.info("开始清理用户 {} 的重复语言偏好记录", userId);

      // 获取用户的所有语言偏好记录
      List<LanguagePreference> allPreferences = languagePreferenceRepository.findByUserId(userId);

      if (allPreferences.size() > 1) {
        // 保留最新的记录，删除其他记录
        LanguagePreference latestPreference = allPreferences.get(0);
        for (int i = 1; i < allPreferences.size(); i++) {
          LanguagePreference preference = allPreferences.get(i);
          if (preference.getCreatedAt() != null
              && latestPreference.getCreatedAt() != null
              && preference.getCreatedAt().isAfter(latestPreference.getCreatedAt())) {
            latestPreference = preference;
          }
        }

        // 删除除最新记录外的所有记录
        for (LanguagePreference preference : allPreferences) {
          if (!preference.getId().equals(latestPreference.getId())) {
            languagePreferenceRepository.delete(preference);
            log.info("删除重复的语言偏好记录: {}", preference.getId());
          }
        }

        // 确保最新记录是活跃的
        latestPreference.setIsActive(true);
        languagePreferenceRepository.save(latestPreference);

        log.info("清理完成，保留记录: {}", latestPreference.getId());
      }
    } catch (Exception e) {
      log.error("清理重复语言偏好记录失败: {}", e.getMessage(), e);
    }
  }
}
