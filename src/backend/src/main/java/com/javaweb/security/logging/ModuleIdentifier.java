package com.javaweb.security.logging;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModuleIdentifier {

  private final ModuleMappingProperties mappingProperties;

  public String resolveModule(String uri) {
    if (uri == null) {
      return "demo:unknown";
    }
    if (uri.startsWith("/api/v1/demo/")) {
      String[] segments = uri.substring("/api/v1/demo/".length()).split("/");
      String category = segments.length > 0 ? segments[0] : "unknown";
      return "demo:" + category.toLowerCase(Locale.ROOT);
    }
    if (uri.startsWith("/api/v1/challenges/")) {
      String[] segments = uri.substring("/api/v1/challenges/".length()).split("/");
      String challenge = segments.length > 0 ? segments[0] : "unknown";
      return "challenge:" + challenge.toLowerCase(Locale.ROOT);
    }
    return "demo:unknown";
  }

  public Long resolveVulnerabilityId(String uri) {
    if (uri == null || !uri.startsWith("/api/v1/demo/")) {
      return null;
    }
    String[] segments = uri.substring("/api/v1/demo/".length()).split("/");
    if (segments.length == 0) {
      return null;
    }
    String category = segments[0].toLowerCase(Locale.ROOT);
    return mappingProperties.getDemo().get(category);
  }

  public Long resolveChallengeId(String uri) {
    if (uri == null || !uri.startsWith("/api/v1/challenges/")) {
      return null;
    }
    String[] segments = uri.substring("/api/v1/challenges/".length()).split("/");
    if (segments.length == 0) {
      return null;
    }
    String slug = segments[0].toLowerCase(Locale.ROOT);
    return mappingProperties.getChallenges().get(slug);
  }
}
