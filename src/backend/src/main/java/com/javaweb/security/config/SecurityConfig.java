package com.javaweb.security.config;

import com.javaweb.security.security.JwtAuthenticationEntryPoint;
import com.javaweb.security.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security配置类
 *
 * <p>配置应用程序的安全策略，包括：
 *
 * <ul>
 *   <li>JWT认证配置
 *   <li>URL访问权限控制
 *   <li>CORS跨域支持
 *   <li>密码加密策略
 *   <li>异常处理
 * </ul>
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      CorsConfigurationSource corsConfigurationSource) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  /**
   * 密码编码器
   *
   * <p>使用BCrypt算法进行密码哈希，提供强度为12的加密
   *
   * @return BCrypt密码编码器
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  /**
   * JWT认证过滤器
   *
   * @return JWT认证过滤器实例
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  /**
   * 安全过滤器链配置
   *
   * <p>配置HTTP安全策略，包括URL访问控制、认证方式、异常处理等
   *
   * @param http HttpSecurity配置对象
   * @return 安全过滤器链
   * @throws Exception 配置异常
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 禁用CSRF（使用JWT无需CSRF保护）
        .csrf()
        .disable()

        // 配置CORS
        .cors()
        .configurationSource(corsConfigurationSource)
        .and()

        // 禁用X-Frame-Options（允许iframe嵌入）
        .headers()
        .frameOptions()
        .disable()
        .and()

        // 异常处理
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()

        // 会话管理（无状态）
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()

        // URL访问权限配置
        .authorizeHttpRequests(
            authz ->
                authz
                    // 公开访问的API端点
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**"))
                    .permitAll()

                    // 系统监控端点（公开访问，无敏感信息）
                    // 注意：health和info端点仅返回基本状态信息，无敏感数据，可以公开访问
                    // health返回{"status":"UP"}，info返回{}，不包含敏感信息
                    .requestMatchers(
                        new AntPathRequestMatcher("/actuator/health"),
                        new AntPathRequestMatcher("/actuator/info"))
                    .permitAll()

                    // 用户统计端点（需要认证，防止泄露系统用户数量等敏感信息）
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/stats", "GET"))
                    .authenticated()

                    // 用户排行榜端点（需要认证，防止用户枚举和信息泄露）
                    // 注意：排行榜可能泄露用户信息，可能被用于用户枚举攻击
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/ranking", "GET"))
                    .authenticated()

                    // 用户搜索端点（需要认证，防止用户枚举攻击）
                    // 注意：用户搜索可能被滥用进行用户枚举攻击，泄露用户名等信息
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/search", "GET"))
                    .authenticated()

                    // 用户详情端点（需要认证，防止用户枚举和信息泄露）
                    // 注意：用户详情可能被用于用户枚举攻击，泄露用户信息
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/{userId}", "GET"))
                    .authenticated()

                    // 文件访问端点（需要认证，防止用户枚举和信息泄露）
                    // 注意：头像文件可能被用于用户枚举攻击，泄露用户信息
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/files/**"))
                    .authenticated()

                    // 挑战场景端点需要认证
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/challenge-scenarios/**"))
                    .authenticated()

                    // 需要认证的用户端点（敏感操作）
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/profile", "GET"))
                    .authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/profile", "PUT"))
                    .authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users/password", "PUT"))
                    .authenticated()
                    .requestMatchers(
                        new AntPathRequestMatcher("/api/v1/users/verify-email", "POST"))
                    .authenticated()

                    // 用户管理端点（统一权限模式：所有登录用户都可以访问）
                    // 注意：当前系统采用统一权限模式，所有注册用户拥有相同的权限
                    // 虽然数据库中有ADMIN角色，但业务逻辑上所有用户权限相同
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/users"))
                    .authenticated()

                    // 系统管理端点（统一权限模式：所有登录用户都可以访问）
                    // 注意：当前系统采用统一权限模式，所有注册用户拥有相同的权限
                    // 虽然数据库中有ADMIN角色，但业务逻辑上所有用户权限相同
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/admin/**"))
                    .authenticated()

                    // 所有API端点需要认证（除了上面明确允许的）
                    .requestMatchers(new AntPathRequestMatcher("/api/**"))
                    .authenticated()

                    // 所有非API路径允许访问（前端SPA路由由Vue Router处理）
                    .anyRequest()
                    .permitAll());

    // 添加JWT过滤器
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * 开发环境专用配置
   *
   * <p>在开发环境下可能需要的特殊安全配置
   *
   * @param http HttpSecurity配置对象
   * @return 开发环境安全过滤器链
   * @throws Exception 配置异常
   */
  @Bean("devSecurityFilterChain")
  @org.springframework.context.annotation.Profile("dev")
  public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
    return http
        // 继承主要配置
        .cors()
        .configurationSource(corsConfigurationSource)
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()

        // 开发环境允许更宽松的访问控制
        .authorizeHttpRequests(
            authz ->
                authz
                    // 开发工具端点
                    .requestMatchers(new AntPathRequestMatcher("/actuator/**"))
                    .permitAll()

                    // H2数据库控制台（如果使用）
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                    .permitAll()

                    // 其他配置与生产环境相同
                    .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**"))
                    .permitAll()
                    // 注意：Swagger文档端点已从配置中移除，因为前端未使用
                    .anyRequest()
                    .authenticated())

        // 添加JWT过滤器
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
