package com.javaweb.security.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web MVC配置类
 *
 * <p>配置静态资源处理和SPA（单页应用）fallback支持
 *
 * <p>功能：
 *
 * <ul>
 *   <li>静态资源服务：serve前端构建后的静态文件（HTML、JS、CSS等）
 *   <li>SPA Fallback：Vue Router history模式支持，所有未匹配的路由都返回index.html
 * </ul>
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * 配置静态资源处理器
   *
   * <p>Spring Boot默认会serve以下目录的静态资源：
   *
   * <ul>
   *   <li>classpath:/static/
   *   <li>classpath:/public/
   *   <li>classpath:/resources/
   *   <li>classpath:/META-INF/resources/
   * </ul>
   *
   * <p>这里显式配置以确保静态资源正确serve
   *
   * @param registry 资源处理器注册表
   */
  @Override
  public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    // 配置静态资源处理
    registry
        .addResourceHandler("/**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(true)
        // SPA fallback: 如果资源不存在且不是API路径，返回index.html
        .addResolver(
            new PathResourceResolver() {
              @Override
              protected Resource getResource(
                  @NonNull String resourcePath, @NonNull Resource location) throws IOException {
                Resource resource = location.createRelative(resourcePath);

                // 如果资源存在，直接返回
                if (resource.exists() && resource.isReadable()) {
                  return resource;
                }

                // 如果是API路径，返回null（让Spring MVC处理）
                if (resourcePath.startsWith("api/")) {
                  return null;
                }

                // 其他路径（Vue Router路由），返回index.html
                Resource indexHtml = location.createRelative("index.html");
                if (indexHtml.exists() && indexHtml.isReadable()) {
                  return indexHtml;
                }

                return null;
              }
            });
  }

  /**
   * 配置视图控制器
   *
   * <p>为根路径配置fallback到index.html，支持Vue Router history模式
   *
   * @param registry 视图控制器注册表
   */
  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {
    // 根路径fallback到index.html
    registry.addViewController("/").setViewName("forward:/index.html");
  }
}
