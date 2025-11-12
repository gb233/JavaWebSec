package com.javaweb.security.config;

import com.javaweb.security.model.VulnerabilityConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A01 越权访问漏洞配置类
 *
 * <p>定义A01越权访问漏洞的详细信息和流程图配置
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-29
 */
@Configuration
public class A01AccessControlConfig {

  @Bean
  public VulnerabilityConfig a01AccessControlVulnerabilityConfig() {
    VulnerabilityConfig config = new VulnerabilityConfig();
    config.setCategory("A01");
    config.setName("失效的访问控制 (Broken Access Control)");
    config.setDescription("失效的访问控制是指应用程序在验证用户权限时存在缺陷，导致攻击者可以访问本应受限的API资源或功能。");
    config.setRiskLevel("高危");
    config.setAttackVector("水平越权、垂直越权、直接对象引用（IDOR）、上下文越权、功能级访问控制失效、路径遍历");
    config.setHarmScenarios(
        Arrays.asList(
            "数据泄露：攻击者获取其他用户的敏感数据，如个人信息、订单信息等。",
            "权限提升：普通用户获得管理员权限，可以执行危险操作。",
            "系统控制：攻击者通过权限绕过获得系统控制权。",
            "业务损失：敏感数据泄露导致业务损失和法律风险。"));

    // 配置流程图
    VulnerabilityConfig.FlowChartConfig flowChart = new VulnerabilityConfig.FlowChartConfig();

    // 时序流程图
    VulnerabilityConfig.SequenceFlowChart sequenceFlow =
        new VulnerabilityConfig.SequenceFlowChart();
    sequenceFlow.setTitle("A01越权访问攻击时序流程");
    sequenceFlow.setDescription("展示攻击者通过API越权访问获取敏感数据的完整时序流程");
    sequenceFlow.setSteps(
        Arrays.asList(
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "1. 发送正常请求获取Token", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "2. 返回用户Token", "response"),
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "3. 修改请求参数(如订单ID)", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "4. 返回其他用户数据(越权成功)", "response"),
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "5. 继续尝试其他资源ID", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "6. 返回更多敏感数据", "response")));
    flowChart.setSequenceFlow(sequenceFlow);

    // 攻击示例图
    VulnerabilityConfig.AttackExampleChart attackExample =
        new VulnerabilityConfig.AttackExampleChart();
    attackExample.setTitle("A01越权访问攻击示例");
    attackExample.setDescription("展示具体的越权访问攻击步骤和载荷");
    attackExample.setAttackSteps(
        Arrays.asList(
            new VulnerabilityConfig.AttackStep(
                "1", "发现API接口", "GET /api/orders/{orderId}", "返回订单信息"),
            new VulnerabilityConfig.AttackStep("2", "测试水平越权", "修改orderId为其他用户订单", "获取他人订单数据"),
            new VulnerabilityConfig.AttackStep("3", "测试垂直越权", "访问管理员接口", "获取管理员权限"),
            new VulnerabilityConfig.AttackStep("4", "批量扫描", "遍历所有可能的ID", "获取大量敏感数据")));
    flowChart.setAttackExample(attackExample);

    // 代码流程图
    VulnerabilityConfig.CodeFlowChart codeFlow = new VulnerabilityConfig.CodeFlowChart();
    codeFlow.setTitle("A01越权访问代码流程");
    codeFlow.setDescription("展示易受攻击的代码实现和安全代码实现");
    codeFlow.setCodeSteps(
        Arrays.asList(
            new VulnerabilityConfig.CodeStep(
                "1",
                "易受攻击的代码",
                "@GetMapping(\"/orders/{id}\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.findById(id);\n}",
                "缺少权限验证，直接返回数据"),
            new VulnerabilityConfig.CodeStep(
                "2",
                "安全代码实现",
                "@GetMapping(\"/orders/{id}\")\n@PreAuthorize(\"@orderService.canAccess(#id, authentication.name)\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.findById(id);\n}",
                "添加权限验证注解"),
            new VulnerabilityConfig.CodeStep(
                "3",
                "权限验证方法",
                "public boolean canAccess(Long orderId, String username) {\n    Order order = findById(orderId);\n    return order.getUserId().equals(getCurrentUserId(username));\n}",
                "验证资源所有权")));
    flowChart.setCodeFlow(codeFlow);

    // 防护流程图
    VulnerabilityConfig.ProtectionFlowChart protectionFlow =
        new VulnerabilityConfig.ProtectionFlowChart();
    protectionFlow.setTitle("A01越权访问防护流程");
    protectionFlow.setDescription("展示如何防护越权访问攻击");
    protectionFlow.setProtectionSteps(
        Arrays.asList(
            new VulnerabilityConfig.ProtectionStep("1", "实施RBAC", "基于角色的访问控制", "为每个API接口配置适当的角色权限"),
            new VulnerabilityConfig.ProtectionStep("2", "资源所有权验证", "验证用户对资源的访问权限", "在业务逻辑中验证资源所有权"),
            new VulnerabilityConfig.ProtectionStep(
                "3", "API权限验证", "统一的权限验证中间件", "使用Spring Security的@PreAuthorize注解"),
            new VulnerabilityConfig.ProtectionStep("4", "访问日志记录", "记录所有API访问", "监控异常访问模式"),
            new VulnerabilityConfig.ProtectionStep("5", "输入验证", "验证请求参数", "防止参数篡改攻击")));
    flowChart.setProtectionFlow(protectionFlow);

    config.setFlowChart(flowChart);

    // 定义子类型配置
    Map<String, VulnerabilityConfig.SubTypeConfig> subTypeConfigs = new HashMap<>();

    // 水平越权配置
    VulnerabilityConfig.SubTypeConfig horizontalOrderConfig =
        new VulnerabilityConfig.SubTypeConfig();
    horizontalOrderConfig.setSubType("horizontal-order");
    horizontalOrderConfig.setName("水平越权-订单访问");
    horizontalOrderConfig.setDescription("攻击者可以访问其他用户的订单信息");
    horizontalOrderConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码\n@GetMapping(\"/orders/{id}\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.findById(id);\n}\n```");
    horizontalOrderConfig.setSecureCodeExample(
        "```java\n// 安全的代码\n@GetMapping(\"/orders/{id}\")\n@PreAuthorize(\"@orderService.canAccess(#id, authentication.name)\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.findById(id);\n}\n```");
    horizontalOrderConfig.setAttackPayloads(
        Arrays.asList("修改订单ID: /api/orders/123", "批量扫描: /api/orders/1-1000"));
    horizontalOrderConfig.setDefensePayloads(
        Arrays.asList("权限验证: @PreAuthorize", "资源所有权检查: canAccess()"));
    horizontalOrderConfig.setSupportedParameters(Arrays.asList("orderId", "userId"));
    subTypeConfigs.put("horizontal-order", horizontalOrderConfig);

    // 垂直越权配置
    VulnerabilityConfig.SubTypeConfig verticalOrderConfig = new VulnerabilityConfig.SubTypeConfig();
    verticalOrderConfig.setSubType("vertical-order");
    verticalOrderConfig.setName("垂直越权-管理员功能");
    verticalOrderConfig.setDescription("普通用户获得管理员权限");
    verticalOrderConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码\n@GetMapping(\"/admin/users\")\npublic List<User> getAllUsers() {\n    return userService.findAll();\n}\n```");
    verticalOrderConfig.setSecureCodeExample(
        "```java\n// 安全的代码\n@GetMapping(\"/admin/users\")\n@PreAuthorize(\"hasRole('ADMIN')\")\npublic List<User> getAllUsers() {\n    return userService.findAll();\n}\n```");
    verticalOrderConfig.setAttackPayloads(
        Arrays.asList("访问管理员接口: /api/admin/users", "修改用户角色: /api/admin/users/role"));
    verticalOrderConfig.setDefensePayloads(
        Arrays.asList("角色验证: hasRole('ADMIN')", "权限检查: hasAuthority()"));
    verticalOrderConfig.setSupportedParameters(Arrays.asList("userId", "role"));
    subTypeConfigs.put("vertical-order", verticalOrderConfig);

    config.setSubTypeConfigs(subTypeConfigs);

    return config;
  }
}
