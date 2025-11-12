package com.javaweb.security.config;

import com.javaweb.security.model.VulnerabilityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A06 易受攻击和过时的组件配置类
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Configuration
public class A06VulnerableComponentsConfig {

  @Bean
  public VulnerabilityConfig a06VulnerableComponentsVulnerabilityConfig() {
    VulnerabilityConfig config = new VulnerabilityConfig();
    config.setCategory("A06");
    config.setName("易受攻击和过时的组件 (Vulnerable and Outdated Components)");
    config.setDescription("应用程序使用了包含已知漏洞的组件或依赖，包括过时的组件、易受攻击的依赖、未及时更新的组件等问题。");
    config.setRiskLevel("中危");

    return config;
  }
}
