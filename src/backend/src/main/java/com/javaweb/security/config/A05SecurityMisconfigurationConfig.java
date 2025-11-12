package com.javaweb.security.config;

import com.javaweb.security.model.VulnerabilityConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** A05 安全配置错误配置类 提供A05安全配置错误漏洞的详细配置信息 */
@Configuration
public class A05SecurityMisconfigurationConfig {

  @Bean
  public VulnerabilityConfig a05SecurityMisconfigurationVulnerabilityConfig() {
    VulnerabilityConfig config = new VulnerabilityConfig();
    config.setCategory("A05");
    config.setName("安全配置错误 (Security Misconfiguration)");
    config.setDescription("应用程序、框架、库和其他组件的不安全配置导致的安全漏洞");
    config.setRiskLevel("中危");
    config.setAttackVector("默认配置利用,调试模式攻击,管理员功能滥用,CORS配置错误,安全头缺失");
    config.setHarmScenarios(Arrays.asList("数据泄露", "系统控制", "权限提升", "跨站攻击", "信息收集"));
    config.setSecurityRecommendations(
        Arrays.asList("修改所有默认配置", "禁用调试模式", "正确配置管理员功能", "设置安全的CORS策略", "定期安全配置审计"));

    // 子类型配置
    Map<String, VulnerabilityConfig.SubTypeConfig> subTypes = new HashMap<>();

    // 默认配置漏洞
    VulnerabilityConfig.SubTypeConfig defaultConfig = new VulnerabilityConfig.SubTypeConfig();
    defaultConfig.setName("默认配置漏洞");
    defaultConfig.setDescription("使用默认配置导致的安全风险");
    defaultConfig.setVulnerableCodeExample(
        "// 漏洞代码 - 使用默认配置\n@Value(\"${app.password:admin123}\")\nprivate String password;");
    defaultConfig.setSecureCodeExample(
        "// 安全代码 - 强制配置\n@Value(\"${app.password}\")\n@NotBlank(message = \"密码不能为空\")\nprivate String password;");
    defaultConfig.setAttackPayloads(Arrays.asList("default-config", "admin123", "test"));
    defaultConfig.setDefensePayloads(
        Arrays.asList("secure-config", "strong-password", "validated"));
    defaultConfig.setSupportedParameters(Arrays.asList("attackType", "testData", "configType"));
    subTypes.put("default-config", defaultConfig);

    // 调试模式漏洞
    VulnerabilityConfig.SubTypeConfig debugMode = new VulnerabilityConfig.SubTypeConfig();
    debugMode.setName("调试模式漏洞");
    debugMode.setDescription("生产环境启用调试模式导致的信息泄露");
    debugMode.setVulnerableCodeExample(
        "// 漏洞代码 - 启用调试模式\n@Value(\"${app.debug:true}\")\nprivate boolean debug;");
    debugMode.setSecureCodeExample(
        "// 安全代码 - 禁用调试模式\n@Value(\"${app.debug:false}\")\nprivate boolean debug;");
    debugMode.setAttackPayloads(Arrays.asList("debug-mode", "debug=true", "trace"));
    debugMode.setDefensePayloads(Arrays.asList("debug-disabled", "debug=false", "production"));
    debugMode.setSupportedParameters(Arrays.asList("attackType", "testData", "debugLevel"));
    subTypes.put("debug-mode", debugMode);

    // 管理员功能漏洞
    VulnerabilityConfig.SubTypeConfig adminFeatures = new VulnerabilityConfig.SubTypeConfig();
    adminFeatures.setName("管理员功能漏洞");
    adminFeatures.setDescription("默认启用管理员功能导致的安全风险");
    adminFeatures.setVulnerableCodeExample(
        "// 漏洞代码 - 默认启用管理员功能\n@Value(\"${app.admin.enabled:true}\")\nprivate boolean adminEnabled;");
    adminFeatures.setSecureCodeExample(
        "// 安全代码 - 默认禁用管理员功能\n@Value(\"${app.admin.enabled:false}\")\nprivate boolean adminEnabled;");
    adminFeatures.setAttackPayloads(Arrays.asList("admin-features", "admin=true", "management"));
    adminFeatures.setDefensePayloads(Arrays.asList("admin-protected", "admin=false", "authorized"));
    adminFeatures.setSupportedParameters(Arrays.asList("attackType", "testData", "adminLevel"));
    subTypes.put("admin-features", adminFeatures);

    // CORS配置错误
    VulnerabilityConfig.SubTypeConfig corsMisconfig = new VulnerabilityConfig.SubTypeConfig();
    corsMisconfig.setName("CORS配置错误");
    corsMisconfig.setDescription("CORS配置不当导致的跨域攻击");
    corsMisconfig.setVulnerableCodeExample(
        "// 漏洞代码 - CORS配置错误\nconfig.addAllowedOrigin(\"*\");\nconfig.addAllowedMethod(\"*\");");
    corsMisconfig.setSecureCodeExample(
        "// 安全代码 - 正确配置CORS\nconfig.addAllowedOrigin(\"https://trusted-domain.com\");\nconfig.addAllowedMethod(\"GET\");");
    corsMisconfig.setAttackPayloads(Arrays.asList("cors-misconfig", "origin=*", "wildcard"));
    corsMisconfig.setDefensePayloads(Arrays.asList("cors-secure", "origin=trusted", "restricted"));
    corsMisconfig.setSupportedParameters(Arrays.asList("attackType", "testData", "origin"));
    subTypes.put("cors-misconfig", corsMisconfig);

    config.setSubTypeConfigs(subTypes);

    // 流程图配置
    VulnerabilityConfig.FlowChartConfig flowChart = new VulnerabilityConfig.FlowChartConfig();

    // 时序图
    VulnerabilityConfig.SequenceFlowChart sequenceFlow =
        new VulnerabilityConfig.SequenceFlowChart();
    sequenceFlow.setSteps(
        Arrays.asList(
            new VulnerabilityConfig.FlowStep("1", "攻击者", "扫描系统", "发现安全配置错误"),
            new VulnerabilityConfig.FlowStep("2", "攻击者", "利用默认配置", "获取系统访问权限"),
            new VulnerabilityConfig.FlowStep("3", "攻击者", "利用调试模式", "获取敏感信息"),
            new VulnerabilityConfig.FlowStep("4", "攻击者", "利用管理员功能", "提升权限"),
            new VulnerabilityConfig.FlowStep("5", "系统", "返回敏感数据", "攻击成功")));
    flowChart.setSequenceFlow(sequenceFlow);

    // 攻击示例图
    VulnerabilityConfig.AttackExampleChart attackExample =
        new VulnerabilityConfig.AttackExampleChart();
    attackExample.setAttackSteps(
        Arrays.asList(
            new VulnerabilityConfig.AttackStep("1", "扫描默认配置", "发现未修改的默认密码", "获取系统访问权限"),
            new VulnerabilityConfig.AttackStep("2", "利用调试模式", "获取系统配置信息", "获取敏感信息"),
            new VulnerabilityConfig.AttackStep("3", "访问管理员功能", "获取系统控制权", "权限提升成功"),
            new VulnerabilityConfig.AttackStep("4", "利用CORS错误", "进行跨域攻击", "攻击成功")));
    flowChart.setAttackExample(attackExample);

    // 代码流程图
    VulnerabilityConfig.CodeFlowChart codeFlow = new VulnerabilityConfig.CodeFlowChart();
    codeFlow.setCodeSteps(
        Arrays.asList(
            new VulnerabilityConfig.CodeStep("1", "配置加载", "读取应用配置", "安全配置加载"),
            new VulnerabilityConfig.CodeStep("2", "安全检查", "验证配置安全性", "配置验证通过"),
            new VulnerabilityConfig.CodeStep("3", "权限验证", "检查访问权限", "权限验证成功"),
            new VulnerabilityConfig.CodeStep("4", "安全响应", "返回安全结果", "安全响应返回")));
    flowChart.setCodeFlow(codeFlow);

    // 防护流程图
    VulnerabilityConfig.ProtectionFlowChart protectionFlow =
        new VulnerabilityConfig.ProtectionFlowChart();
    protectionFlow.setProtectionSteps(
        Arrays.asList(
            new VulnerabilityConfig.ProtectionStep("1", "配置审计", "检查所有配置项", "配置审计完成"),
            new VulnerabilityConfig.ProtectionStep("2", "安全加固", "修改不安全配置", "安全加固完成"),
            new VulnerabilityConfig.ProtectionStep("3", "权限控制", "实施访问控制", "权限控制生效"),
            new VulnerabilityConfig.ProtectionStep("4", "监控告警", "持续安全监控", "监控系统运行")));
    flowChart.setProtectionFlow(protectionFlow);

    config.setFlowChart(flowChart);

    return config;
  }
}
