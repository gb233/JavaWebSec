package com.javaweb.security.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 测试模式配置
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "test.mode")
public class TestModeConfig {

  /** 实时反馈模式配置 */
  private ModeConfig realtimeFeedback;

  /** 考试模式配置 */
  private ModeConfig examMode;

  /** 随机综合模式配置 */
  private ModeConfig randomComprehensive;

  @Data
  public static class ModeConfig {
    private String code;
    private String name;
    private String description;
    private boolean feedback;
    private boolean navigation;
    private boolean randomQuestions;
    private boolean singleCategory;
    private int questionCount;
    private int timeLimit;
    private Map<String, Object> features;
  }

  /** 获取模式配置 */
  public ModeConfig getModeConfig(String modeCode) {
    switch (modeCode) {
      case "REALTIME_FEEDBACK":
        return realtimeFeedback;
      case "EXAM_MODE":
        return examMode;
      case "RANDOM_COMPREHENSIVE":
        return randomComprehensive;
      default:
        throw new IllegalArgumentException("不支持的测试模式: " + modeCode);
    }
  }
}
