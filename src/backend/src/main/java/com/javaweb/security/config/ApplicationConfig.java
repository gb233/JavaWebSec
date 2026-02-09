package com.javaweb.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Arrays;
import java.util.concurrent.Executor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 应用程序核心配置类
 *
 * <p>提供应用程序的核心Bean配置，包括：
 *
 * <ul>
 *   <li>JSON序列化配置
 *   <li>缓存管理器配置
 *   <li>线程池配置
 *   <li>CORS跨域配置
 *   <li>HTTP客户端配置
 * </ul>
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Configuration
public class ApplicationConfig {

  /**
   * JSON序列化配置
   *
   * <p>配置Jackson ObjectMapper用于JSON序列化和反序列化
   *
   * @return 配置好的ObjectMapper实例
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // 注册Java 8时间模块
    mapper.registerModule(new JavaTimeModule());

    // 禁用将日期写为时间戳
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 设置属性命名策略为驼峰命名
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

    // 忽略未知属性
    mapper.configure(
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 修复Jackson序列化问题：处理未知枚举值
    mapper.configure(
        com.fasterxml.jackson.databind.DeserializationFeature
            .READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE,
        true);

    // 禁用空Bean序列化失败
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    return mapper;
  }

  /**
   * 缓存管理器配置
   *
   * <p>使用内存缓存管理器，适用于单节点部署场景
   *
   * @return CacheManager实例
   */
  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

    // 预定义缓存区域
    cacheManager.setCacheNames(
        Arrays.asList(
            "users",
            "vulnerabilities",
            "vulnerability_categories",
            "test_questions",
            "challenges",
            "system_config"));

    // 允许运行时创建缓存
    cacheManager.setAllowNullValues(false);

    return cacheManager;
  }

  /**
   * 异步任务执行器配置
   *
   * <p>用于处理异步任务，如邮件发送、日志记录等
   *
   * @return 线程池执行器
   */
  @Bean("taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 核心线程数
    executor.setCorePoolSize(5);

    // 最大线程数
    executor.setMaxPoolSize(20);

    // 队列容量
    executor.setQueueCapacity(200);

    // 线程名前缀
    executor.setThreadNamePrefix("Security-Teaching-Async-");

    // 拒绝策略：调用者运行
    executor.setRejectedExecutionHandler(
        new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

    // 等待任务完成后再关闭
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);

    executor.initialize();
    return executor;
  }

  /**
   * CORS跨域配置
   *
   * <p>配置跨域资源共享，允许前端访问后端API
   *
   * <p>支持通过环境变量配置允许的源，默认允许所有来源（生产环境建议配置具体域名）
   *
   * @return CORS配置源
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 从环境变量读取允许的源
    // 生产环境必须通过环境变量 CORS_ALLOWED_ORIGINS 配置具体域名
    String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
    if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
      // 支持多个源，用逗号分隔
      configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
    } else {
      // 默认仅允许localhost（开发环境安全配置）
      // 生产环境必须通过环境变量配置具体域名，不允许使用通配符
      configuration.setAllowedOriginPatterns(
          Arrays.asList(
              "http://localhost:*",
              "http://127.0.0.1:*",
              "https://localhost:*",
              "https://127.0.0.1:*"));
    }

    // 允许的HTTP方法
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // 允许的请求头
    configuration.setAllowedHeaders(
        Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Cache-Control",
            "X-File-Name"));

    // 允许携带认证信息
    configuration.setAllowCredentials(true);

    // 预检请求缓存时间
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  /**
   * HTTP客户端配置
   *
   * <p>用于调用外部API服务
   *
   * @return RestTemplate实例
   */
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    // 设置连接超时和读取超时
    org.springframework.http.client.SimpleClientHttpRequestFactory factory =
        new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000); // 5秒连接超时
    factory.setReadTimeout(10000); // 10秒读取超时

    restTemplate.setRequestFactory(factory);

    return restTemplate;
  }
}
