package com.javaweb.security.service;

import com.javaweb.security.dto.GuideStepDto;
import com.javaweb.security.dto.UserGuidePreferenceDto;
import java.util.List;

/**
 * 指引服务接口
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
public interface GuideService {

  /** 获取用户指引偏好 */
  UserGuidePreferenceDto getUserGuidePreference();

  /** 检查用户是否需要显示指引 */
  boolean shouldShowGuide();

  /** 获取指引步骤列表 */
  List<GuideStepDto> getGuideSteps();

  /** 标记用户完成初始指引 */
  void markInitialGuideCompleted();

  /** 更新指引显示时间 */
  void updateGuideShownTime();

  /** 设置自动显示指引 */
  void setAutoShowGuide(boolean autoShow);

  /** 重置用户指引状态 */
  void resetUserGuide();
}
