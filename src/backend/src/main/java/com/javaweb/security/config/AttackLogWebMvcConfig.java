package com.javaweb.security.config;

import com.javaweb.security.logging.AttackLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AttackLogWebMvcConfig implements WebMvcConfigurer {

  private final AttackLogInterceptor attackLogInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(attackLogInterceptor)
        .addPathPatterns("/api/v1/demo/**", "/api/v1/challenges/**");
  }
}
