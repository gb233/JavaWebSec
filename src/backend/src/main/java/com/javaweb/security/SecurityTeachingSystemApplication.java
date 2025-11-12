package com.javaweb.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Java Web安全教学系统启动类
 *
 * <p>基于Spring Boot 2.7.x构建的安全教学系统，集成了：
 *
 * <ul>
 *   <li>Spring Security - 认证和授权
 *   <li>Spring Data JPA - 数据访问层
 *   <li>MyBatis Plus - 增强的MyBatis
 *   <li>Spring Cache - 缓存支持
 *   <li>Spring Actuator - 监控端点
 *   <li>Swagger/OpenAPI - API文档
 * </ul>
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class SecurityTeachingSystemApplication {

  /**
   * 应用程序主入口点
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    // 设置系统属性
    System.setProperty("spring.application.name", "security-teaching-system");
    System.setProperty("file.encoding", "UTF-8");

    // 启动应用
    SpringApplication app = new SpringApplication(SecurityTeachingSystemApplication.class);

    // 设置默认配置
    app.setDefaultProperties(getDefaultProperties());

    // 启动应用
    app.run(args);
  }

  /**
   * 获取默认配置属性
   *
   * @return 默认配置的Properties对象
   */
  private static java.util.Properties getDefaultProperties() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("spring.application.name", "security-teaching-system");
    props.setProperty("server.port", "8080");
    props.setProperty("spring.profiles.default", "dev");
    return props;
  }
}
