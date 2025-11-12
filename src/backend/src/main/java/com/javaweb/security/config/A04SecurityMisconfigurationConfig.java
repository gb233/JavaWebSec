package com.javaweb.security.config;

import com.javaweb.security.model.VulnerabilityConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A04 安全配置错误漏洞配置类
 *
 * <p>定义A04安全配置错误漏洞的详细信息和流程图配置
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-29
 */
@Configuration
public class A04SecurityMisconfigurationConfig {

  @Bean
  public VulnerabilityConfig a04SecurityMisconfigurationVulnerabilityConfig() {
    VulnerabilityConfig config = new VulnerabilityConfig();
    config.setCategory("A04");
    config.setName("安全配置错误 (Security Misconfiguration)");
    config.setDescription(
        "安全配置错误是指应用程序在配置过程中存在安全缺陷，包括使用不安全的默认配置、敏感信息泄露、错误配置等问题。这些缺陷可能导致系统暴露敏感信息或存在安全风险。");
    config.setRiskLevel("中危");
    config.setAttackVector("默认配置漏洞、敏感信息泄露、错误配置、配置管理缺陷");
    config.setHarmScenarios(
        Arrays.asList(
            "系统完全暴露：攻击者利用默认配置获得系统完全控制权，包括管理员权限。",
            "敏感信息泄露：配置文件中包含的敏感信息被非法获取，包括数据库密码、API密钥等。",
            "安全防护失效：错误配置导致安全机制失效，系统暴露在多种攻击风险下。",
            "配置管理混乱：缺少配置管理导致安全风险难以控制，系统安全性无法保障。"));

    // 配置子类型
    Map<String, VulnerabilityConfig.SubTypeConfig> subTypes = new HashMap<>();

    // 默认配置漏洞
    VulnerabilityConfig.SubTypeConfig defaultConfigSubType =
        new VulnerabilityConfig.SubTypeConfig();
    defaultConfigSubType.setSubType("default-config");
    defaultConfigSubType.setName("默认配置漏洞");
    defaultConfigSubType.setDescription("使用不安全的默认配置，如默认密码、调试模式等");
    defaultConfigSubType.setVulnerableCodeExample(getDefaultConfigVulnerableCode());
    defaultConfigSubType.setSecureCodeExample(getDefaultConfigSecureCode());
    defaultConfigSubType.setAttackPayloads(Arrays.asList("admin", "password", "debug"));
    defaultConfigSubType.setDefensePayloads(
        Arrays.asList("secure-config", "audit-enabled", "defaults-disabled"));
    defaultConfigSubType.setSupportedParameters(
        Arrays.asList("configData", "debugMode", "defaultPassword"));
    subTypes.put("default-config", defaultConfigSubType);

    // 敏感信息泄露
    VulnerabilityConfig.SubTypeConfig sensitiveInfoSubType =
        new VulnerabilityConfig.SubTypeConfig();
    sensitiveInfoSubType.setSubType("sensitive-info");
    sensitiveInfoSubType.setName("敏感信息泄露");
    sensitiveInfoSubType.setDescription("配置文件中包含敏感信息，如密码、密钥等");
    sensitiveInfoSubType.setVulnerableCodeExample(getSensitiveInfoVulnerableCode());
    sensitiveInfoSubType.setSecureCodeExample(getSensitiveInfoSecureCode());
    sensitiveInfoSubType.setAttackPayloads(Arrays.asList("config", "env", "secrets"));
    sensitiveInfoSubType.setDefensePayloads(
        Arrays.asList("encrypted-config", "env-vars", "secrets-management"));
    sensitiveInfoSubType.setSupportedParameters(
        Arrays.asList("configData", "sensitiveData", "exposureLevel"));
    subTypes.put("sensitive-info", sensitiveInfoSubType);

    // 错误配置
    VulnerabilityConfig.SubTypeConfig wrongConfigSubType = new VulnerabilityConfig.SubTypeConfig();
    wrongConfigSubType.setSubType("wrong-config");
    wrongConfigSubType.setName("错误配置");
    wrongConfigSubType.setDescription("配置项设置不当，如权限配置、安全策略等");
    wrongConfigSubType.setVulnerableCodeExample(getWrongConfigVulnerableCode());
    wrongConfigSubType.setSecureCodeExample(getWrongConfigSecureCode());
    wrongConfigSubType.setAttackPayloads(Arrays.asList("permissions", "cors", "headers"));
    wrongConfigSubType.setDefensePayloads(
        Arrays.asList("secure-permissions", "strict-cors", "security-headers"));
    wrongConfigSubType.setSupportedParameters(
        Arrays.asList("configData", "permissionLevel", "securityPolicy"));
    subTypes.put("wrong-config", wrongConfigSubType);

    // 配置管理
    VulnerabilityConfig.SubTypeConfig configManagementSubType =
        new VulnerabilityConfig.SubTypeConfig();
    configManagementSubType.setSubType("config-management");
    configManagementSubType.setName("配置管理");
    configManagementSubType.setDescription("缺少配置管理流程，如配置审计、版本控制等");
    configManagementSubType.setVulnerableCodeExample(getConfigManagementVulnerableCode());
    configManagementSubType.setSecureCodeExample(getConfigManagementSecureCode());
    configManagementSubType.setAttackPayloads(Arrays.asList("version-control", "audit", "backup"));
    configManagementSubType.setDefensePayloads(
        Arrays.asList("git-config", "audit-log", "backup-strategy"));
    configManagementSubType.setSupportedParameters(
        Arrays.asList("configData", "versionControl", "auditEnabled"));
    subTypes.put("config-management", configManagementSubType);

    config.setSubTypeConfigs(subTypes);

    // 配置流程图
    VulnerabilityConfig.FlowChartConfig flowChart = new VulnerabilityConfig.FlowChartConfig();

    // 时序流程图
    VulnerabilityConfig.SequenceFlowChart sequenceFlow =
        new VulnerabilityConfig.SequenceFlowChart();
    sequenceFlow.setTitle("A04安全配置错误攻击时序流程");
    sequenceFlow.setDescription("展示攻击者通过安全配置错误获取系统信息的完整时序流程");
    sequenceFlow.setSteps(
        Arrays.asList(
            new VulnerabilityConfig.FlowStep("1", "攻击者扫描系统", "发现系统存在安全配置错误", "attack"),
            new VulnerabilityConfig.FlowStep("2", "分析配置信息", "识别具体的配置缺陷类型", "attack"),
            new VulnerabilityConfig.FlowStep("3", "利用配置漏洞", "根据配置错误类型进行针对性攻击", "attack"),
            new VulnerabilityConfig.FlowStep("4", "获取敏感信息", "成功获取系统敏感配置信息", "attack"),
            new VulnerabilityConfig.FlowStep("5", "扩大攻击范围", "利用获取的信息进行进一步攻击", "attack")));
    flowChart.setSequenceFlow(sequenceFlow);

    // 攻击示例图
    VulnerabilityConfig.AttackExampleChart attackExample =
        new VulnerabilityConfig.AttackExampleChart();
    attackExample.setTitle("A04安全配置错误攻击示例");
    attackExample.setDescription("展示具体的安全配置错误攻击场景和利用方法");
    attackExample.setAttackSteps(
        Arrays.asList(
            new VulnerabilityConfig.AttackStep("默认配置扫描", "扫描系统默认配置，发现调试模式启用", "debug=true", "系统暴露"),
            new VulnerabilityConfig.AttackStep(
                "敏感信息获取", "通过配置接口获取数据库连接字符串", "config=full", "敏感信息泄露"),
            new VulnerabilityConfig.AttackStep(
                "权限绕过", "利用错误的权限配置访问管理员功能", "permissions=all", "权限提升"),
            new VulnerabilityConfig.AttackStep("系统控制", "获得系统完全控制权", "admin=access", "系统控制")));
    flowChart.setAttackExample(attackExample);

    // 代码流程图
    VulnerabilityConfig.CodeFlowChart codeFlow = new VulnerabilityConfig.CodeFlowChart();
    codeFlow.setTitle("A04安全配置错误代码流程");
    codeFlow.setDescription("展示安全配置错误在代码层面的实现流程");
    codeFlow.setCodeSteps(
        Arrays.asList(
            new VulnerabilityConfig.CodeStep(
                "配置加载",
                "系统启动时加载配置文件",
                "@Value(\"${app.debug:true}\")",
                "@Value(\"${app.debug:false}\")"),
            new VulnerabilityConfig.CodeStep("默认值设置", "使用不安全的默认配置值", "debug=true", "debug=false"),
            new VulnerabilityConfig.CodeStep(
                "敏感信息暴露", "在接口中直接返回敏感配置信息", "return password", "return \"***\""),
            new VulnerabilityConfig.CodeStep(
                "权限验证",
                "错误的权限配置导致安全机制失效",
                ".anyRequest().permitAll()",
                ".anyRequest().authenticated()")));
    flowChart.setCodeFlow(codeFlow);

    // 防护流程图
    VulnerabilityConfig.ProtectionFlowChart protectionFlow =
        new VulnerabilityConfig.ProtectionFlowChart();
    protectionFlow.setTitle("A04安全配置错误防护流程");
    protectionFlow.setDescription("展示如何通过安全配置防护安全配置错误漏洞");
    protectionFlow.setProtectionSteps(
        Arrays.asList(
            new VulnerabilityConfig.ProtectionStep(
                "配置审计", "定期审计系统配置，发现安全缺陷", "自动化扫描", "定期执行安全配置检查"),
            new VulnerabilityConfig.ProtectionStep(
                "默认配置禁用", "禁用所有不安全的默认配置", "强制修改", "禁用所有默认配置和调试模式"),
            new VulnerabilityConfig.ProtectionStep(
                "敏感信息保护", "使用环境变量和加密存储敏感信息", "环境变量", "使用环境变量和加密存储"),
            new VulnerabilityConfig.ProtectionStep(
                "权限配置优化", "实施最小权限原则和严格的访问控制", "最小权限", "实施严格的访问控制策略"),
            new VulnerabilityConfig.ProtectionStep(
                "配置管理", "建立配置版本控制和变更管理流程", "版本控制", "建立配置管理和审计机制")));
    flowChart.setProtectionFlow(protectionFlow);

    config.setFlowChart(flowChart);

    return config;
  }

  // 代码示例方法
  private String getDefaultConfigVulnerableCode() {
    return """
        // 漏洞代码 - 使用不安全的默认配置
        @Configuration
        public class AppConfig {

            // ⚠️ 危险：使用不安全的默认配置
            @Value("${app.debug:true}")
            private boolean debug;

            @Value("${app.password:admin123}")
            private String password;

            @Value("${app.admin.enabled:true}")
            private boolean adminEnabled;
        }
        """;
  }

  private String getDefaultConfigSecureCode() {
    return """
        // 安全代码 - 安全配置实现
        @Configuration
        public class AppConfig {

            // ✅ 安全：禁用调试模式
            @Value("${app.debug:false}")
            private boolean debug;

            // ✅ 安全：强制修改默认密码
            @Value("${app.password}")
            @NotBlank(message = "密码不能为空")
            private String password;

            // ✅ 安全：默认禁用管理员功能
            @Value("${app.admin.enabled:false}")
            private boolean adminEnabled;
        }
        """;
  }

  private String getSensitiveInfoVulnerableCode() {
    return """
        // 漏洞代码 - 敏感信息泄露
        @GetMapping("/config")
        public String getConfig() {
            // ⚠️ 危险：直接返回敏感配置信息
            return "调试模式: " + debug +
                   ", 密码: " + password +
                   ", 数据库: " + databaseUrl;
        }
        """;
  }

  private String getSensitiveInfoSecureCode() {
    return """
        // 安全代码 - 敏感信息保护
        @GetMapping("/config")
        public ResponseEntity<Map<String, Object>> getConfig() {
            // ✅ 安全：只返回非敏感配置信息
            Map<String, Object> config = new HashMap<>();
            config.put("debug", false);
            config.put("version", "1.0.0");
            config.put("environment", "production");
            return ResponseEntity.ok(config);
        }
        """;
  }

  private String getWrongConfigVulnerableCode() {
    return """
        // 漏洞代码 - 错误配置
        @Configuration
        public class SecurityConfig {

            // ⚠️ 危险：错误的权限配置
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                    .anyRequest().permitAll(); // 允许所有请求
            }
        }
        """;
  }

  private String getWrongConfigSecureCode() {
    return """
        // 安全代码 - 正确配置
        @Configuration
        public class SecurityConfig {

            // ✅ 安全：正确的权限配置
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                    .antMatchers("/api/public/**").permitAll()
                    .antMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
            }
        }
        """;
  }

  private String getConfigManagementVulnerableCode() {
    return """
        // 漏洞代码 - 配置管理缺陷
        @Component
        public class ConfigService {

            // ⚠️ 危险：硬编码配置，无版本控制
            private String databaseUrl = "jdbc:mysql://localhost:3306/app";
            private String apiKey = "sk-1234567890abcdef";

            public void updateConfig(String key, String value) {
                // 无审计日志，无审批流程
                System.setProperty(key, value);
            }
        }
        """;
  }

  private String getConfigManagementSecureCode() {
    return """
        // 安全代码 - 配置管理安全实现
        @Component
        public class ConfigService {

            // ✅ 安全：使用环境变量和配置管理
            @Value("${database.url}")
            private String databaseUrl;

            @Value("${api.key}")
            private String apiKey;

            @Autowired
            private ConfigAuditService auditService;

            public void updateConfig(String key, String value, String operator) {
                // 记录审计日志
                auditService.logConfigChange(key, value, operator);
                // 验证配置变更
                validateConfigChange(key, value);
                // 更新配置
                System.setProperty(key, value);
            }
        }
        """;
  }
}
