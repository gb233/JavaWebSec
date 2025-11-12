package com.javaweb.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.common.result.ApiResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JWT认证入口点
 *
 * <p>当未认证的用户尝试访问受保护的资源时，此类负责处理认证失败的情况
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Autowired private ObjectMapper objectMapper;

  /**
   * 处理认证异常
   *
   * <p>当用户未认证时访问受保护资源，返回401 Unauthorized响应
   *
   * @param request 请求对象
   * @param response 响应对象
   * @param authException 认证异常
   * @throws IOException IO异常
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    // 设置响应状态和内容类型
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    // 构建错误响应
    ApiResult<Object> result = ApiResult.error(HttpStatus.UNAUTHORIZED.value(), "认证失败，请重新登录");

    // 添加更多错误信息用于调试
    result = result.path(request.getRequestURI());

    // 记录认证失败的详细信息
    String errorMessage = authException.getMessage();
    if (errorMessage != null) {
      result =
          ApiResult.error(HttpStatus.UNAUTHORIZED.value(), "认证失败: " + errorMessage)
              .path(request.getRequestURI());
    }

    // 写入响应
    response.getWriter().write(objectMapper.writeValueAsString(result));
    response.getWriter().flush();
  }
}
