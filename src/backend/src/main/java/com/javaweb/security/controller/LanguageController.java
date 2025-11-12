package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.LanguagePreferenceDto;
import com.javaweb.security.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 语言管理控制器
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/language")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "语言管理", description = "多语言切换相关接口")
public class LanguageController {

  private final LanguageService languageService;

  @GetMapping("/current")
  @Operation(summary = "获取当前用户语言偏好", description = "获取当前登录用户的语言设置")
  public ResponseEntity<ApiResult<LanguagePreferenceDto>> getCurrentLanguage() {
    try {
      LanguagePreferenceDto language = languageService.getCurrentUserLanguage();
      return ResponseEntity.ok(ApiResult.success("获取语言偏好成功", language));
    } catch (Exception e) {
      log.error("获取当前用户语言偏好失败", e);
      return ResponseEntity.ok(ApiResult.error("获取语言偏好失败: " + e.getMessage()));
    }
  }

  @PostMapping("/set")
  @Operation(summary = "设置用户语言偏好", description = "设置当前用户的语言偏好")
  public ResponseEntity<ApiResult<LanguagePreferenceDto>> setLanguage(
      @RequestBody Map<String, String> request) {
    String languageCode = request.get("languageCode");
    try {
      LanguagePreferenceDto language = languageService.setUserLanguage(languageCode);
      return ResponseEntity.ok(ApiResult.success("设置语言偏好成功", language));
    } catch (IllegalArgumentException e) {
      log.warn("不支持的语言代码: {}", languageCode);
      return ResponseEntity.ok(ApiResult.error("不支持的语言代码: " + languageCode));
    } catch (Exception e) {
      log.error("设置用户语言偏好失败", e);
      return ResponseEntity.ok(ApiResult.error("设置语言偏好失败: " + e.getMessage()));
    }
  }

  @GetMapping("/supported")
  @Operation(summary = "获取支持的语言列表", description = "获取系统支持的所有语言")
  public ResponseEntity<ApiResult<List<LanguagePreferenceDto>>> getSupportedLanguages() {
    try {
      List<LanguagePreferenceDto> languages = languageService.getSupportedLanguages();
      return ResponseEntity.ok(ApiResult.success("获取支持语言列表成功", languages));
    } catch (Exception e) {
      log.error("获取支持语言列表失败", e);
      return ResponseEntity.ok(ApiResult.error("获取支持语言列表失败: " + e.getMessage()));
    }
  }

  @GetMapping("/default")
  @Operation(summary = "获取默认语言", description = "获取系统默认语言")
  public ResponseEntity<ApiResult<String>> getDefaultLanguage() {
    try {
      String defaultLanguage = languageService.getDefaultLanguage();
      return ResponseEntity.ok(ApiResult.success("获取默认语言成功", defaultLanguage));
    } catch (Exception e) {
      log.error("获取默认语言失败", e);
      return ResponseEntity.ok(ApiResult.error("获取默认语言失败: " + e.getMessage()));
    }
  }
}
