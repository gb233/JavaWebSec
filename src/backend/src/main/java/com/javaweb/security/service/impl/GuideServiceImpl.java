package com.javaweb.security.service.impl;

import com.javaweb.security.dto.GuideStepDto;
import com.javaweb.security.dto.UserGuidePreferenceDto;
import com.javaweb.security.entity.GuideStep;
import com.javaweb.security.entity.UserGuidePreference;
import com.javaweb.security.repository.GuideStepRepository;
import com.javaweb.security.repository.UserGuidePreferenceRepository;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.GuideService;
import com.javaweb.security.service.LanguageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 指引服务实现
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuideServiceImpl implements GuideService {

  private final UserGuidePreferenceRepository userGuidePreferenceRepository;
  private final GuideStepRepository guideStepRepository;
  private final AuthenticationService authenticationService;
  private final LanguageService languageService;

  private static final String CURRENT_GUIDE_VERSION = "1.0.0";

  @Override
  public UserGuidePreferenceDto getUserGuidePreference() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      throw new IllegalStateException("用户未登录");
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isPresent()) {
      return UserGuidePreferenceDto.fromEntity(preference.get());
    }

    // 如果用户没有指引偏好，创建默认偏好
    return createDefaultGuidePreference(userId);
  }

  @Override
  public boolean shouldShowGuide() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      return false;
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isEmpty()) {
      // 新用户，需要显示指引
      return true;
    }

    UserGuidePreference userPreference = preference.get();

    // 检查是否已完成初始指引
    if (userPreference.getHasCompletedInitialGuide()) {
      return false;
    }

    // 检查是否启用自动显示
    return userPreference.getAutoShowGuide();
  }

  @Override
  public List<GuideStepDto> getGuideSteps() {
    String currentLanguage = languageService.getCurrentUserLanguage().getLanguageCode();

    // 获取当前版本的指引步骤
    List<GuideStep> steps =
        guideStepRepository.findByVersionAndActiveOrderByOrderIndex(CURRENT_GUIDE_VERSION);
    if (steps.isEmpty()) {
      // 如果没有版本化步骤，获取所有活跃步骤
      steps = guideStepRepository.findAllActiveOrderByOrderIndex();
    }

    return steps.stream().map(step -> GuideStepDto.fromEntity(step, currentLanguage)).toList();
  }

  @Override
  @Transactional
  public void markInitialGuideCompleted() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      throw new IllegalStateException("用户未登录");
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isPresent()) {
      UserGuidePreference userPreference = preference.get();
      userPreference.setHasCompletedInitialGuide(true);
      userPreference.setLastGuideShownAt(LocalDateTime.now());
      userGuidePreferenceRepository.save(userPreference);
      log.info("用户 {} 完成初始指引", userId);
    } else {
      // 创建新的指引偏好
      UserGuidePreference newPreference =
          UserGuidePreference.builder()
              .userId(userId)
              .hasCompletedInitialGuide(true)
              .guideVersion(CURRENT_GUIDE_VERSION)
              .lastGuideShownAt(LocalDateTime.now())
              .autoShowGuide(false)
              .build();
      userGuidePreferenceRepository.save(newPreference);
      log.info("用户 {} 完成初始指引（新建偏好）", userId);
    }
  }

  @Override
  @Transactional
  public void updateGuideShownTime() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      return;
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isPresent()) {
      UserGuidePreference userPreference = preference.get();
      userPreference.setLastGuideShownAt(LocalDateTime.now());
      userGuidePreferenceRepository.save(userPreference);
    }
  }

  @Override
  @Transactional
  public void setAutoShowGuide(boolean autoShow) {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      throw new IllegalStateException("用户未登录");
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isPresent()) {
      UserGuidePreference userPreference = preference.get();
      userPreference.setAutoShowGuide(autoShow);
      userGuidePreferenceRepository.save(userPreference);
      log.info("用户 {} 设置自动显示指引为 {}", userId, autoShow);
    } else {
      // 如果用户没有指引偏好，创建新的偏好记录
      UserGuidePreference newPreference =
          UserGuidePreference.builder()
              .userId(userId)
              .hasCompletedInitialGuide(false)
              .guideVersion(CURRENT_GUIDE_VERSION)
              .autoShowGuide(autoShow)
              .build();
      userGuidePreferenceRepository.save(newPreference);
      log.info("用户 {} 创建指引偏好，设置自动显示指引为 {}", userId, autoShow);
    }
  }

  @Override
  @Transactional
  public void resetUserGuide() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      throw new IllegalStateException("用户未登录");
    }

    Optional<UserGuidePreference> preference = userGuidePreferenceRepository.findByUserId(userId);
    if (preference.isPresent()) {
      UserGuidePreference userPreference = preference.get();
      userPreference.setHasCompletedInitialGuide(false);
      userPreference.setAutoShowGuide(true);
      userPreference.setGuideVersion(CURRENT_GUIDE_VERSION);
      userGuidePreferenceRepository.save(userPreference);
      log.info("用户 {} 重置指引状态", userId);
    }
  }

  /** 创建默认指引偏好 */
  private UserGuidePreferenceDto createDefaultGuidePreference(Long userId) {
    UserGuidePreference defaultPreference =
        UserGuidePreference.builder()
            .userId(userId)
            .hasCompletedInitialGuide(false)
            .guideVersion(CURRENT_GUIDE_VERSION)
            .autoShowGuide(true)
            .build();

    UserGuidePreference saved = userGuidePreferenceRepository.save(defaultPreference);
    return UserGuidePreferenceDto.fromEntity(saved);
  }
}
