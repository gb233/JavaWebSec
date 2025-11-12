package com.javaweb.security.service;

import com.javaweb.security.dto.LanguagePreferenceDto;
import java.util.List;

/**
 * 语言服务接口
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
public interface LanguageService {

  /** 获取用户当前语言偏好 */
  LanguagePreferenceDto getCurrentUserLanguage();

  /** 设置用户语言偏好 */
  LanguagePreferenceDto setUserLanguage(String languageCode);

  /** 获取支持的语言列表 */
  List<LanguagePreferenceDto> getSupportedLanguages();

  /** 检查语言是否支持 */
  boolean isLanguageSupported(String languageCode);

  /** 获取默认语言 */
  String getDefaultLanguage();
}
