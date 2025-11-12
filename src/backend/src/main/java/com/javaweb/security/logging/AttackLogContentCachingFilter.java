package com.javaweb.security.logging;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class AttackLogContentCachingFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri == null
        || !(uri.startsWith("/api/v1/demo/") || uri.startsWith("/api/v1/challenges/"));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ContentCachingRequestWrapper requestWrapper =
        request instanceof ContentCachingRequestWrapper
            ? (ContentCachingRequestWrapper) request
            : new ContentCachingRequestWrapper(request);

    ContentCachingResponseWrapper responseWrapper =
        response instanceof ContentCachingResponseWrapper
            ? (ContentCachingResponseWrapper) response
            : new ContentCachingResponseWrapper(response);

    requestWrapper.setAttribute(AttackLogConstants.ATTR_CACHED_REQUEST, requestWrapper);
    requestWrapper.setAttribute(AttackLogConstants.ATTR_CACHED_RESPONSE, responseWrapper);

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      responseWrapper.copyBodyToResponse();
    }
  }
}
