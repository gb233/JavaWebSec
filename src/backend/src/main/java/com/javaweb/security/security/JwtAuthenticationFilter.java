package com.javaweb.security.security;

import com.javaweb.security.service.CustomUserDetailsService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT认证过滤器
 *
 * <p>从HTTP请求中提取JWT令牌，验证令牌的有效性，并设置Spring Security上下文
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private CustomUserDetailsService userDetailsService;

  /**
   * 执行JWT认证过滤
   *
   * @param request 请求对象
   * @param response 响应对象
   * @param filterChain 过滤器链
   * @throws ServletException Servlet异常
   * @throws IOException IO异常
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // 从请求中获取JWT令牌
      String jwt = getJwtFromRequest(request);

      // 验证令牌
      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        // 从令牌中获取用户名
        String username = tokenProvider.getUsernameFromToken(jwt);

        // 加载用户详细信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 创建认证对象
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 设置认证详细信息
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 设置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      // 记录错误但不阻止过滤器链继续执行
      logger.error("无法设置用户认证: " + ex.getMessage(), ex);
    }

    // 继续过滤器链
    filterChain.doFilter(request, response);
  }

  /**
   * 从HTTP请求中提取JWT令牌
   *
   * <p>支持从Authorization头部提取Bearer令牌
   *
   * @param request HTTP请求
   * @return JWT令牌字符串，如果不存在则返回null
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }

  /**
   * 判断是否应该跳过此过滤器
   *
   * <p>对于某些特定的URL路径，可以跳过JWT认证
   *
   * @param request HTTP请求
   * @return 是否跳过过滤器
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    // 跳过公开的端点
    return path.startsWith("/api/v1/auth/")
        || path.startsWith("/swagger-ui/")
        || path.startsWith("/v3/api-docs/")
        || path.startsWith("/actuator/health")
        || path.startsWith("/actuator/info")
        || path.equals("/favicon.ico");
  }
}
