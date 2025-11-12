package com.javaweb.security.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.dto.attacklog.AttackLogCreateRequest;
import com.javaweb.security.entity.AttackLog.RiskLevel;
import com.javaweb.security.service.AttackLogService;
// import com.javaweb.security.service.AuthenticationService;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttackLogInterceptor implements HandlerInterceptor {

  private static final int MAX_CONTENT_LENGTH = 4000;

  private final AttackLogService attackLogService;
  // private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;
  private final ModuleIdentifier moduleIdentifier;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (shouldCapture(request)) {
      request.setAttribute(AttackLogConstants.ATTR_START_TIME, System.currentTimeMillis());
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    if (!shouldCapture(request)) {
      return;
    }

    try {
      recordAttackLog(request, response, ex);
    } catch (Exception e) {
      log.warn("记录攻击日志失败: {}", e.getMessage(), e);
    }
  }

  private void recordAttackLog(
      HttpServletRequest request, HttpServletResponse response, Exception handlerException)
      throws JsonProcessingException {

    ContentCachingRequestWrapper requestWrapper =
        request instanceof ContentCachingRequestWrapper
            ? (ContentCachingRequestWrapper) request
            : (ContentCachingRequestWrapper)
                request.getAttribute(AttackLogConstants.ATTR_CACHED_REQUEST);

    ContentCachingResponseWrapper responseWrapper =
        (ContentCachingResponseWrapper)
            request.getAttribute(AttackLogConstants.ATTR_CACHED_RESPONSE);

    if (requestWrapper == null || responseWrapper == null) {
      log.debug("请求/响应包装缺失，跳过攻击日志记录");
      return;
    }

    AttackLogCreateRequest createRequest = new AttackLogCreateRequest();
    createRequest.setUserId(null); // authenticationService.getCurrentUserId());
    String uri = request.getRequestURI();
    createRequest.setVulnerabilityId(moduleIdentifier.resolveVulnerabilityId(uri));
    createRequest.setAttackType(resolveAttackType(uri));
    createRequest.setModule(moduleIdentifier.resolveModule(uri));
    createRequest.setRequestMethod(request.getMethod());
    createRequest.setRequestUrl(resolveRequestUrl(request));

    String requestBody = extractRequestBody(requestWrapper);
    String attackPayload =
        StringUtils.hasText(requestBody)
            ? requestBody
            : Optional.ofNullable(request.getQueryString()).orElse("");
    createRequest.setAttackPayload(truncate(attackPayload));
    createRequest.setRequestHeaders(toJsonString(extractHeaders(requestWrapper)));
    createRequest.setRequestBody(truncate(requestBody));

    int status = responseWrapper.getStatus();
    createRequest.setResponseStatus(status);
    createRequest.setResponseHeaders(toJsonString(extractHeaders(responseWrapper)));
    createRequest.setResponseBody(truncate(extractResponseBody(responseWrapper)));

    boolean successful = status >= 200 && status < 400 && handlerException == null;
    createRequest.setSuccessful(successful);
    createRequest.setRiskLevel(determineRiskLevel(request, successful));
    createRequest.setSourceIp(request.getRemoteAddr());
    createRequest.setUserAgent(request.getHeader("User-Agent"));
    createRequest.setExecutionTime(resolveExecutionTime(request));
    createRequest.setErrorMessage(handlerException != null ? handlerException.getMessage() : null);
    createRequest.setSessionId(
        Optional.ofNullable(request.getSession(false)).map(s -> s.getId()).orElse(null));
    createRequest.setTraceId(request.getHeader("X-Trace-Id"));

    attackLogService.recordAttack(createRequest);
  }

  private boolean shouldCapture(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri == null) {
      return false;
    }
    if (uri.startsWith("/api/v1/attack-logs")) {
      return false;
    }
    return uri.startsWith("/api/v1/demo/") || uri.startsWith("/api/v1/challenges/");
  }

  private String resolveRequestUrl(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder(request.getRequestURL());
    if (StringUtils.hasText(request.getQueryString())) {
      sb.append('?').append(request.getQueryString());
    }
    return sb.toString();
  }

  private String resolveAttackType(String uri) {
    if (!StringUtils.hasText(uri)) {
      return "UNKNOWN";
    }
    if (uri.startsWith("/api/v1/demo/")) {
      String[] segments = uri.substring("/api/v1/demo/".length()).split("/");
      if (segments.length > 1) {
        return segments[1].replaceAll("\\s+", "-").toUpperCase(Locale.ROOT);
      }
      return "DEMO";
    }
    if (uri.startsWith("/api/v1/challenges/")) {
      return "CHALLENGE";
    }
    return "UNKNOWN";
  }

  private RiskLevel determineRiskLevel(HttpServletRequest request, boolean successful) {
    String uri = Optional.ofNullable(request.getRequestURI()).orElse("");
    if (uri.contains("/secure")) {
      return successful ? RiskLevel.LOW : RiskLevel.LOW;
    }
    if (uri.contains("/vulnerable")) {
      return successful ? RiskLevel.HIGH : RiskLevel.MEDIUM;
    }
    return successful ? RiskLevel.MEDIUM : RiskLevel.LOW;
  }

  private Map<String, String> extractHeaders(HttpServletRequest request) {
    List<String> headerNames = Collections.list(request.getHeaderNames());
    Map<String, String> map = new HashMap<>();
    for (String header : headerNames) {
      map.put(header, request.getHeader(header));
    }
    return map;
  }

  private Map<String, String> extractHeaders(HttpServletResponse response) {
    return response.getHeaderNames().stream()
        .collect(Collectors.toMap(h -> h, response::getHeader, (a, b) -> a));
  }

  private String extractRequestBody(ContentCachingRequestWrapper requestWrapper) {
    byte[] content = requestWrapper.getContentAsByteArray();
    if (content.length == 0) {
      return "";
    }
    Charset charset =
        Optional.ofNullable(requestWrapper.getCharacterEncoding())
            .map(Charset::forName)
            .orElse(StandardCharsets.UTF_8);
    return new String(content, charset);
  }

  private String extractResponseBody(ContentCachingResponseWrapper responseWrapper) {
    byte[] content = responseWrapper.getContentAsByteArray();
    if (content.length == 0 || !isTextualContent(responseWrapper.getContentType())) {
      return "";
    }
    Charset charset =
        Optional.ofNullable(responseWrapper.getCharacterEncoding())
            .map(Charset::forName)
            .orElse(StandardCharsets.UTF_8);
    return new String(content, charset);
  }

  private boolean isTextualContent(String contentType) {
    if (!StringUtils.hasText(contentType)) {
      return false;
    }
    MediaType mediaType = MediaType.parseMediaType(contentType);
    return MediaType.APPLICATION_JSON.includes(mediaType)
        || MediaType.APPLICATION_XML.includes(mediaType)
        || MediaType.TEXT_PLAIN.includes(mediaType)
        || MediaType.TEXT_HTML.includes(mediaType)
        || MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
  }

  private String truncate(String content) {
    if (!StringUtils.hasText(content)) {
      return content;
    }
    String normalized = content.trim();
    return normalized.length() <= MAX_CONTENT_LENGTH
        ? normalized
        : normalized.substring(0, MAX_CONTENT_LENGTH);
  }

  private String toJsonString(Map<String, String> map) throws JsonProcessingException {
    if (map == null || map.isEmpty()) {
      return null;
    }
    return objectMapper.writeValueAsString(map);
  }

  private int resolveExecutionTime(HttpServletRequest request) {
    Long start = (Long) request.getAttribute(AttackLogConstants.ATTR_START_TIME);
    if (start == null) {
      return 0;
    }
    long duration = System.currentTimeMillis() - start;
    return duration > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) duration;
  }
}
