package com.javaweb.security.logging;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "attacklog.mapping")
public class ModuleMappingProperties {

  private Map<String, Long> demo = new HashMap<>();
  private Map<String, Long> challenges = new HashMap<>();
}
