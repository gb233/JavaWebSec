package com.javaweb.security.config;

import com.javaweb.security.model.VulnerabilityConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A02 加密失败漏洞配置类
 *
 * <p>定义A02加密失败漏洞的配置信息，包括子类型、参数、载荷等
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-29
 */
@Configuration
public class A02CryptographicConfig {

  @Bean
  public VulnerabilityConfig a02CryptographicVulnerabilityConfig() {
    VulnerabilityConfig config = new VulnerabilityConfig();
    config.setCategory("A02");
    config.setName("加密失败 (Cryptographic Failures)");
    config.setDescription(
        "加密失败是指应用程序在加密实现过程中存在缺陷，包括使用弱加密算法、明文存储敏感数据、弱随机数生成、密钥管理不当等问题，导致敏感信息泄露或加密被破解。");
    config.setRiskLevel("高危");
    config.setAttackVector("弱加密算法、明文存储、弱随机数、密钥管理");
    config.setHarmScenarios(
        Arrays.asList(
            "数据泄露：敏感信息被非法获取，包括密码、个人信息、商业机密等。",
            "身份冒充：攻击者利用破解的密码进行身份冒充，获得未授权访问。",
            "数据篡改：利用弱加密进行数据篡改，破坏数据完整性。",
            "系统控制：通过密钥泄露获得系统控制权，造成严重后果。"));
    config.setSecurityRecommendations(
        Arrays.asList(
            "使用强加密算法：采用AES-256、RSA-2048等现代加密标准。",
            "密码安全存储：使用BCrypt、Argon2等专门为密码设计的哈希算法。",
            "敏感数据加密：所有敏感数据必须使用强加密算法加密存储。",
            "安全随机数：使用SecureRandom等密码学安全的随机数生成器。",
            "密钥管理：实施密钥管理系统，定期轮换密钥，安全存储密钥。",
            "传输加密：使用HTTPS等安全传输协议保护数据传输。"));

    // 配置流程图
    VulnerabilityConfig.FlowChartConfig flowChart = new VulnerabilityConfig.FlowChartConfig();

    // 时序流程图
    VulnerabilityConfig.SequenceFlowChart sequenceFlow =
        new VulnerabilityConfig.SequenceFlowChart();
    sequenceFlow.setTitle("A02加密失败攻击时序流程");
    sequenceFlow.setDescription("展示攻击者利用加密缺陷获取敏感数据的完整时序流程");
    sequenceFlow.setSteps(
        Arrays.asList(
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "1. 发现弱加密算法(MD5/SHA-1)", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "2. 返回加密后的数据", "response"),
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "3. 进行碰撞攻击或彩虹表攻击", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "4. 返回明文数据(破解成功)", "response"),
            new VulnerabilityConfig.FlowStep("攻击者", "系统", "5. 利用明文数据进行进一步攻击", "attack"),
            new VulnerabilityConfig.FlowStep("系统", "攻击者", "6. 返回更多敏感信息", "response")));
    flowChart.setSequenceFlow(sequenceFlow);

    // 攻击示例图
    VulnerabilityConfig.AttackExampleChart attackExample =
        new VulnerabilityConfig.AttackExampleChart();
    attackExample.setTitle("A02加密失败攻击示例");
    attackExample.setDescription("展示具体的加密攻击步骤和载荷");
    attackExample.setAttackSteps(
        Arrays.asList(
            new VulnerabilityConfig.AttackStep("1", "发现弱加密", "使用MD5哈希密码", "生成可预测的哈希值"),
            new VulnerabilityConfig.AttackStep("2", "彩虹表攻击", "使用彩虹表破解MD5", "获取明文密码"),
            new VulnerabilityConfig.AttackStep("3", "明文存储攻击", "直接获取明文密码", "无需破解直接获取"),
            new VulnerabilityConfig.AttackStep("4", "弱随机数攻击", "预测加密密钥", "解密敏感数据")));
    flowChart.setAttackExample(attackExample);

    // 代码流程图
    VulnerabilityConfig.CodeFlowChart codeFlow = new VulnerabilityConfig.CodeFlowChart();
    codeFlow.setTitle("A02加密失败代码流程");
    codeFlow.setDescription("展示易受攻击的加密代码实现和安全代码实现");
    codeFlow.setCodeSteps(
        Arrays.asList(
            new VulnerabilityConfig.CodeStep(
                "1",
                "易受攻击的代码",
                "public String hashPassword(String password) {\n    MessageDigest md = MessageDigest.getInstance(\"MD5\");\n    return bytesToHex(md.digest(password.getBytes()));\n}",
                "使用MD5哈希，容易被破解"),
            new VulnerabilityConfig.CodeStep(
                "2",
                "安全代码实现",
                "@Autowired\nprivate BCryptPasswordEncoder passwordEncoder;\n\npublic String hashPassword(String password) {\n    return passwordEncoder.encode(password);\n}",
                "使用BCrypt哈希，安全性高"),
            new VulnerabilityConfig.CodeStep(
                "3",
                "敏感数据加密",
                "public String encryptSensitiveData(String data) {\n    Cipher cipher = Cipher.getInstance(\"AES\");\n    cipher.init(Cipher.ENCRYPT_MODE, secretKey);\n    return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));\n}",
                "使用AES-256加密敏感数据")));
    flowChart.setCodeFlow(codeFlow);

    // 防护流程图
    VulnerabilityConfig.ProtectionFlowChart protectionFlow =
        new VulnerabilityConfig.ProtectionFlowChart();
    protectionFlow.setTitle("A02加密失败防护流程");
    protectionFlow.setDescription("展示如何防护加密失败攻击");
    protectionFlow.setProtectionSteps(
        Arrays.asList(
            new VulnerabilityConfig.ProtectionStep("1", "使用强加密算法", "AES-256、RSA-2048", "采用现代加密标准"),
            new VulnerabilityConfig.ProtectionStep("2", "密码安全存储", "BCrypt、Argon2", "使用专门的密码哈希算法"),
            new VulnerabilityConfig.ProtectionStep("3", "敏感数据加密", "端到端加密", "所有敏感数据必须加密存储"),
            new VulnerabilityConfig.ProtectionStep("4", "安全随机数", "SecureRandom", "使用密码学安全的随机数生成器"),
            new VulnerabilityConfig.ProtectionStep("5", "密钥管理", "密钥轮换、安全存储", "实施完整的密钥管理系统")));
    flowChart.setProtectionFlow(protectionFlow);

    config.setFlowChart(flowChart);

    // 定义子类型配置
    Map<String, VulnerabilityConfig.SubTypeConfig> subTypeConfigs = new HashMap<>();

    // 弱加密算法配置
    VulnerabilityConfig.SubTypeConfig weakEncryptionConfig =
        new VulnerabilityConfig.SubTypeConfig();
    weakEncryptionConfig.setSubType("weak-encryption");
    weakEncryptionConfig.setName("弱加密算法");
    weakEncryptionConfig.setDescription("使用MD5、SHA-1、DES等已被破解或存在安全缺陷的加密算法，容易被攻击者破解。");
    weakEncryptionConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码示例\npublic String hashPassword(String password) {\n    MessageDigest md = MessageDigest.getInstance(\"MD5\");\n    byte[] hash = md.digest(password.getBytes());\n    return bytesToHex(hash);\n}\n```");
    weakEncryptionConfig.setSecureCodeExample(
        "```java\n// 安全的代码示例\n@Autowired\nprivate BCryptPasswordEncoder passwordEncoder;\n\npublic String hashPassword(String password) {\n    return passwordEncoder.encode(password);\n}\n```");
    weakEncryptionConfig.setAttackPayloads(
        Arrays.asList("MD5哈希：使用MD5算法进行密码哈希", "SHA-1哈希：使用SHA-1算法进行数据哈希", "DES加密：使用DES算法进行数据加密"));
    weakEncryptionConfig.setDefensePayloads(
        Arrays.asList(
            "AES-256加密：使用AES-256算法进行数据加密",
            "BCrypt哈希：使用BCrypt算法进行密码哈希",
            "RSA-2048加密：使用RSA-2048算法进行非对称加密"));
    weakEncryptionConfig.setSupportedParameters(Arrays.asList("algorithm", "data"));
    subTypeConfigs.put("weak-encryption", weakEncryptionConfig);

    // 密码明文存储配置
    VulnerabilityConfig.SubTypeConfig passwordStorageConfig =
        new VulnerabilityConfig.SubTypeConfig();
    passwordStorageConfig.setSubType("password-storage");
    passwordStorageConfig.setName("密码明文存储");
    passwordStorageConfig.setDescription("密码以明文形式存储在数据库中，数据库泄露时密码直接暴露。");
    passwordStorageConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码示例\npublic void saveUser(String username, String password) {\n    // 直接存储明文密码\n    user.setPassword(password);\n    userRepository.save(user);\n}\n```");
    passwordStorageConfig.setSecureCodeExample(
        "```java\n// 安全的代码示例\n@Autowired\nprivate BCryptPasswordEncoder passwordEncoder;\n\npublic void saveUser(String username, String password) {\n    String hashedPassword = passwordEncoder.encode(password);\n    user.setPassword(hashedPassword);\n    userRepository.save(user);\n}\n```");
    passwordStorageConfig.setAttackPayloads(
        Arrays.asList("明文密码：password123", "弱密码：123456", "常见密码：admin"));
    passwordStorageConfig.setDefensePayloads(
        Arrays.asList("BCrypt哈希：$2a$12$...", "Argon2哈希：$argon2id$...", "PBKDF2哈希：$pbkdf2$..."));
    passwordStorageConfig.setSupportedParameters(Arrays.asList("password"));
    subTypeConfigs.put("password-storage", passwordStorageConfig);

    // 敏感数据未加密配置
    VulnerabilityConfig.SubTypeConfig sensitiveDataConfig = new VulnerabilityConfig.SubTypeConfig();
    sensitiveDataConfig.setSubType("sensitive-data");
    sensitiveDataConfig.setName("敏感数据未加密");
    sensitiveDataConfig.setDescription("敏感数据（如个人信息、财务数据等）以明文形式存储，存在严重的数据泄露风险。");
    sensitiveDataConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码示例\npublic void saveUserInfo(UserInfo userInfo) {\n    // 直接存储敏感信息\n    userInfo.setIdCard(userInfo.getIdCard());\n    userInfo.setBankAccount(userInfo.getBankAccount());\n    userInfoRepository.save(userInfo);\n}\n```");
    sensitiveDataConfig.setSecureCodeExample(
        "```java\n// 安全的代码示例\n@Autowired\nprivate AESUtil aesUtil;\n\npublic void saveUserInfo(UserInfo userInfo) {\n    // 加密敏感信息\n    String encryptedIdCard = aesUtil.encrypt(userInfo.getIdCard());\n    String encryptedBankAccount = aesUtil.encrypt(userInfo.getBankAccount());\n    userInfo.setIdCard(encryptedIdCard);\n    userInfo.setBankAccount(encryptedBankAccount);\n    userInfoRepository.save(userInfo);\n}\n```");
    sensitiveDataConfig.setAttackPayloads(
        Arrays.asList("身份证号：110101199001011234", "银行卡号：6222021234567890123", "手机号：13800138000"));
    sensitiveDataConfig.setDefensePayloads(
        Arrays.asList(
            "AES加密：U2FsdGVkX1+...",
            "RSA加密：MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
            "ChaCha20加密：加密后的敏感数据"));
    sensitiveDataConfig.setSupportedParameters(Arrays.asList("sensitiveData"));
    subTypeConfigs.put("sensitive-data", sensitiveDataConfig);

    // 弱随机数生成配置
    VulnerabilityConfig.SubTypeConfig weakRandomConfig = new VulnerabilityConfig.SubTypeConfig();
    weakRandomConfig.setSubType("weak-random");
    weakRandomConfig.setName("弱随机数生成");
    weakRandomConfig.setDescription("使用可预测的伪随机数生成器（如Math.random()），导致加密密钥和随机数容易被预测。");
    weakRandomConfig.setVulnerableCodeExample(
        "```java\n// 易受攻击的代码示例\npublic String generateToken() {\n    StringBuilder token = new StringBuilder();\n    for (int i = 0; i < 32; i++) {\n        token.append((int) (Math.random() * 10));\n    }\n    return token.toString();\n}\n```");
    weakRandomConfig.setSecureCodeExample(
        "```java\n// 安全的代码示例\npublic String generateToken() {\n    SecureRandom secureRandom = new SecureRandom();\n    byte[] bytes = new byte[32];\n    secureRandom.nextBytes(bytes);\n    return Base64.getEncoder().encodeToString(bytes);\n}\n```");
    weakRandomConfig.setAttackPayloads(
        Arrays.asList(
            "Math.random()：使用Java的Math.random()生成随机数", "伪随机数：使用可预测的随机数生成器", "弱种子：使用时间戳等可预测的种子"));
    weakRandomConfig.setDefensePayloads(
        Arrays.asList(
            "SecureRandom：使用Java的SecureRandom生成随机数", "硬件随机数：使用硬件随机数生成器", "强种子：使用密码学安全的种子"));
    weakRandomConfig.setSupportedParameters(Arrays.asList("length"));
    subTypeConfigs.put("weak-random", weakRandomConfig);

    config.setSubTypeConfigs(subTypeConfigs);

    return config;
  }
}
