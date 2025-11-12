package com.javaweb.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 挑战验证服务 负责验证攻击payload是否符合预期
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeValidationService {

  private final ObjectMapper objectMapper;

  /** 验证攻击payload */
  public ValidationResult validatePayload(
      String vulnerabilityType, String step, Map<String, Object> params) {
    try {
      switch (vulnerabilityType) {
        case "A01-越权访问":
        case "A01-权限提升":
        case "A01-访问控制":
        case "A01-数据泄露":
        case "A01-数据篡改":
        case "A01-信息收集":
          return validatePrivilegeEscalation(step, params);
        case "A01-路径穿越":
          return validatePathTraversal(step, params);
        case "A01-文件上传":
          return validateFileUpload(step, params);
        case "A01-任意读取":
          return validateArbitraryRead(step, params);
        case "A03-SQL注入":
        case "A03-注入漏洞":
        case "A03-LDAP注入":
          return validateSqlInjection(step, params);
        case "A03-XSS":
          return validateXss(step, params);
        case "A03-XXE":
          return validateXxe(step, params);
        case "A03-命令执行":
          return validateCommandExecution(step, params);
        case "A04-逻辑缺陷":
        case "A04-业务逻辑":
        case "A04-逻辑漏洞":
        case "A04-条件竞争":
        case "A04-金额篡改":
          return validateBusinessLogic(step, params);
        case "A05-CSRF":
          return validateCsrf(step, params);
        case "A05-配置错误":
          return validateConfigurationError(step, params);
        case "A07-JWT漏洞":
          return validateJwtVulnerability(step, params);
        case "A08-反序列化":
          return validateDeserialization(step, params);
        case "A10-SSRF":
          return validateSsrf(step, params);
        default:
          return ValidationResult.failure(
              "不支持的漏洞类型: "
                  + vulnerabilityType
                  + "\n\n💡 学习提示：\n"
                  + "1. 请检查漏洞类型是否正确\n"
                  + "2. 确认参数格式是否符合要求\n"
                  + "3. 参考OWASP Top 10了解常见漏洞类型\n"
                  + "4. 查看挑战描述获取更多信息");
      }
    } catch (Exception e) {
      log.error("验证payload失败", e);
      return ValidationResult.failure("验证失败: " + e.getMessage());
    }
  }

  /** 验证越权访问 */
  private ValidationResult validatePrivilegeEscalation(String step, Map<String, Object> params) {
    String resourceId = (String) params.getOrDefault("resourceId", "");
    String action = (String) params.getOrDefault("action", "");

    // 预设的攻击场景
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "1", // 访问资源1
            "step2", "admin", // 访问管理员资源
            "step3", "sensitive" // 访问敏感数据
            );

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查是否使用了正确的payload
    boolean isValid = resourceId.equals(expectedPayload) || action.equals(expectedPayload);

    if (isValid) {
      return ValidationResult.success(
          "越权访问成功！获取到敏感数据",
          Map.of(
              "result",
              "成功访问资源" + resourceId + "，执行操作 " + action + "（不安全实现：缺少权限验证）",
              "vulnerabilityType",
              "A01-权限提升"));
    } else {
      return ValidationResult.failure("攻击失败，请尝试其他payload。提示：尝试访问资源ID " + expectedPayload);
    }
  }

  /** 验证SQL注入 */
  private ValidationResult validateSqlInjection(String step, Map<String, Object> params) {
    String username = (String) params.getOrDefault("username", "");
    String password = (String) params.getOrDefault("password", "");

    // 预设的SQL注入payload和对应的SQL模板
    Map<String, SqlInjectionTemplate> sqlTemplates =
        Map.of(
            "step1",
                new SqlInjectionTemplate(
                    "' OR 1=1 --",
                    "SELECT * FROM users WHERE username = ? AND password = ?",
                    "SELECT * FROM users WHERE username = '"
                        + username
                        + "' AND password = '"
                        + password
                        + "'",
                    "SELECT * FROM users WHERE username = '"
                        + username
                        + "' OR 1=1 --' AND password = '"
                        + password
                        + "'"),
            "step2",
                new SqlInjectionTemplate(
                    "admin' --",
                    "SELECT * FROM users WHERE username = ? AND password = ?",
                    "SELECT * FROM users WHERE username = '"
                        + username
                        + "' AND password = '"
                        + password
                        + "'",
                    "SELECT * FROM users WHERE username = 'admin' --' AND password = '"
                        + password
                        + "'"),
            "step3",
                new SqlInjectionTemplate(
                    "' UNION SELECT * FROM users --",
                    "SELECT * FROM users WHERE username = ? AND password = ?",
                    "SELECT * FROM users WHERE username = '"
                        + username
                        + "' AND password = '"
                        + password
                        + "'",
                    "SELECT * FROM users WHERE username = '"
                        + username
                        + "' UNION SELECT * FROM users --' AND password = '"
                        + password
                        + "'"));

    SqlInjectionTemplate template = sqlTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查是否使用了正确的SQL注入payload
    boolean isValid =
        password.contains(template.expectedPayload) || username.contains(template.expectedPayload);

    if (isValid) {
      return ValidationResult.success(
          "SQL注入成功！绕过身份验证",
          Map.of(
              "result", "成功绕过登录验证，获取管理员权限",
              "vulnerabilityType", "A03-SQL注入",
              "adminToken", "admin_token_12345",
              "sqlAnalysis",
                  Map.of(
                      "originalQuery", template.originalQuery,
                      "vulnerableQuery", template.vulnerableQuery,
                      "explanation", "通过SQL注入，将原始查询修改为始终返回true的条件，从而绕过身份验证")));
    } else {
      return ValidationResult.failure("SQL注入失败，请尝试其他payload。提示：尝试使用 " + template.expectedPayload);
    }
  }

  /** SQL注入模板类 */
  private static class SqlInjectionTemplate {
    final String expectedPayload;
    final String originalQuery;
    final String currentQuery;
    final String vulnerableQuery;

    SqlInjectionTemplate(
        String expectedPayload, String originalQuery, String currentQuery, String vulnerableQuery) {
      this.expectedPayload = expectedPayload;
      this.originalQuery = originalQuery;
      this.currentQuery = currentQuery;
      this.vulnerableQuery = vulnerableQuery;
    }
  }

  /** 验证XSS攻击 */
  private ValidationResult validateXss(String step, Map<String, Object> params) {
    String comment = (String) params.getOrDefault("comment", "");
    String articleId = (String) params.getOrDefault("articleId", "");

    // 预设的XSS payload和对应的HTML渲染
    Map<String, XssTemplate> xssTemplates =
        Map.of(
            "step1",
                new XssTemplate(
                    "<script>alert('XSS')</script>",
                    "用户评论：" + comment,
                    "用户评论：<script>alert('XSS')</script>",
                    "用户评论：<script>alert('XSS')</script>",
                    "通过<script>标签直接执行JavaScript代码，获取用户Cookie"),
            "step2",
                new XssTemplate(
                    "<img src=x onerror=alert('XSS')>",
                    "用户评论：" + comment,
                    "用户评论：<img src=x onerror=alert('XSS')>",
                    "用户评论：<img src=x onerror=alert('XSS')>",
                    "通过<img>标签的onerror事件执行JavaScript代码，绕过内容过滤"),
            "step3",
                new XssTemplate(
                    "javascript:alert('XSS')",
                    "用户评论：" + comment,
                    "用户评论：<a href=\"javascript:alert('XSS')\">链接</a>",
                    "用户评论：<a href=\"javascript:alert('XSS')\">链接</a>",
                    "通过javascript:协议执行JavaScript代码，利用链接属性进行攻击"));

    XssTemplate template = xssTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查是否包含了XSS payload
    boolean isValid =
        comment.contains("<script>")
            || comment.contains("onerror")
            || comment.contains("javascript:");

    if (isValid) {
      return ValidationResult.success(
          "XSS攻击成功！获取用户Cookie",
          Map.of(
              "result", "成功执行XSS攻击，获取用户Cookie: session_id=abc123",
              "vulnerabilityType", "A03-XSS",
              "cookie", "session_id=abc123; user_id=456",
              "xssAnalysis",
                  Map.of(
                      "originalHtml", template.originalHtml,
                      "vulnerableHtml", template.vulnerableHtml,
                      "renderedHtml", template.renderedHtml,
                      "explanation", template.explanation)));
    } else {
      return ValidationResult.failure("XSS攻击失败，请尝试其他payload。提示：尝试使用 " + template.expectedPayload);
    }
  }

  /** XSS模板类 */
  private static class XssTemplate {
    final String expectedPayload;
    final String originalHtml;
    final String currentHtml;
    final String vulnerableHtml;
    final String renderedHtml;
    final String explanation;

    XssTemplate(
        String expectedPayload,
        String originalHtml,
        String currentHtml,
        String vulnerableHtml,
        String explanation) {
      this.expectedPayload = expectedPayload;
      this.originalHtml = originalHtml;
      this.currentHtml = currentHtml;
      this.vulnerableHtml = vulnerableHtml;
      this.renderedHtml = vulnerableHtml; // 在真实场景中，这里会是服务器渲染后的HTML
      this.explanation = explanation;
    }
  }

  /** 验证CSRF攻击 */
  private ValidationResult validateCsrf(String step, Map<String, Object> params) {
    String targetUrl = (String) params.getOrDefault("targetUrl", "");
    String articleId = (String) params.getOrDefault("articleId", "");

    // 预设的CSRF攻击场景和对应的HTTP请求模拟
    Map<String, CsrfTemplate> csrfTemplates =
        Map.of(
            "step1",
                new CsrfTemplate(
                    "/api/v1/delete-article",
                    "POST /api/v1/delete-article HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\narticleId=1",
                    "POST "
                        + targetUrl
                        + " HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\narticleId="
                        + articleId,
                    "通过构造恶意表单，利用用户已认证的会话执行删除操作"),
            "step2",
                new CsrfTemplate(
                    "/api/v1/transfer",
                    "POST /api/v1/transfer HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\namount=1000&to=attacker",
                    "POST "
                        + targetUrl
                        + " HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\namount=1000&to=attacker",
                    "通过构造恶意转账请求，利用用户已认证的会话执行资金转移"),
            "step3",
                new CsrfTemplate(
                    "/api/v1/change-password",
                    "POST /api/v1/change-password HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\nnewPassword=hacked123",
                    "POST "
                        + targetUrl
                        + " HTTP/1.1\nHost: example.com\nCookie: session_id=abc123\nContent-Type: application/x-www-form-urlencoded\n\nnewPassword=hacked123",
                    "通过构造恶意密码修改请求，利用用户已认证的会话修改密码"));

    CsrfTemplate template = csrfTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查是否使用了正确的CSRF payload
    boolean isValid = targetUrl.contains(template.expectedPayload);

    if (isValid) {
      return ValidationResult.success(
          "CSRF攻击成功！执行恶意操作",
          Map.of(
              "result",
              "成功执行CSRF攻击，执行操作: " + targetUrl,
              "vulnerabilityType",
              "A05-CSRF",
              "action",
              "恶意操作已执行",
              "csrfAnalysis",
              Map.of(
                  "originalRequest", template.originalRequest,
                  "maliciousRequest", template.maliciousRequest,
                  "explanation", template.explanation)));
    } else {
      return ValidationResult.failure("CSRF攻击失败，请尝试其他payload。提示：尝试使用 " + template.expectedPayload);
    }
  }

  /** CSRF模板类 */
  private static class CsrfTemplate {
    final String expectedPayload;
    final String originalRequest;
    final String currentRequest;
    final String maliciousRequest;
    final String explanation;

    CsrfTemplate(
        String expectedPayload, String originalRequest, String currentRequest, String explanation) {
      this.expectedPayload = expectedPayload;
      this.originalRequest = originalRequest;
      this.currentRequest = currentRequest;
      this.maliciousRequest = currentRequest; // 在真实场景中，这里会是构造的恶意请求
      this.explanation = explanation;
    }
  }

  /** 验证反序列化攻击 */
  private ValidationResult validateDeserialization(String step, Map<String, Object> params) {
    String serializedData = (String) params.getOrDefault("serializedData", "");

    // 预设的反序列化payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "O:8:\"stdClass\":1:{s:4:\"test\";s:4:\"evil\";}", // PHP反序列化
            "step2",
                "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAA=", // Java反序列化
            "step3", "{\"__type\":\"System.Windows.Data.ObjectDataProvider\"}" // .NET反序列化
            );

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查是否使用了正确的反序列化payload
    // 对于不同步骤使用不同的验证逻辑
    boolean isValid = false;
    if ("step1".equals(step)) {
      // PHP反序列化：检查是否包含stdClass
      isValid = serializedData.contains("stdClass");
    } else if ("step2".equals(step)) {
      // Java反序列化：检查Base64编码的字符串是否匹配（完全匹配或包含关键部分）
      // Java序列化的Base64编码通常以rO0AB开头
      isValid =
          serializedData.contains(expectedPayload)
              || serializedData.contains("rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcA") // Base64编码的HashMap类名
              || (serializedData.startsWith("rO0AB")
                  && serializedData.length() > 50); // 检查是否是Java序列化的Base64格式
    } else if ("step3".equals(step)) {
      // .NET反序列化：检查是否包含ObjectDataProvider
      isValid = serializedData.contains("ObjectDataProvider");
    }

    if (isValid) {
      return ValidationResult.success(
          "反序列化攻击成功！执行恶意代码",
          Map.of(
              "result", "成功执行反序列化攻击，执行恶意代码",
              "vulnerabilityType", "A08-反序列化",
              "command", "恶意命令已执行"));
    } else {
      return ValidationResult.failure("反序列化攻击失败，请尝试其他payload。提示：尝试使用 " + expectedPayload);
    }
  }

  /** 验证文件上传漏洞 */
  private ValidationResult validateFileUpload(String step, Map<String, Object> params) {
    String filename = (String) params.getOrDefault("filename", "");
    String content = (String) params.getOrDefault("content", "");

    // 预设的文件上传payload
    Map<String, FileUploadTemplate> uploadTemplates =
        Map.of(
            "step1",
                new FileUploadTemplate(
                    "shell.php",
                    "<?php echo 'Hello World'; ?>",
                    "通过上传PHP文件，尝试执行服务器端代码",
                    "检查文件扩展名绕过和内容过滤"),
            "step2",
                new FileUploadTemplate(
                    "webshell.jsp",
                    "<% out.println(\"JSP Shell\"); %>",
                    "通过上传JSP文件，尝试在Java环境中执行代码",
                    "检查MIME类型绕过和文件头检测"),
            "step3",
                new FileUploadTemplate(
                    "backdoor.asp",
                    "<% Response.Write(\"ASP Backdoor\") %>",
                    "通过上传ASP文件，尝试在Windows IIS环境中执行代码",
                    "检查文件大小限制和内容验证"));

    FileUploadTemplate template = uploadTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    // 检查文件名和内容
    boolean isValid =
        filename.contains(template.expectedFilename) && content.contains(template.expectedContent);

    if (isValid) {
      return ValidationResult.success(
          "文件上传成功！恶意文件已上传",
          Map.of(
              "result",
              "成功上传恶意文件: " + filename,
              "vulnerabilityType",
              "A01-文件上传",
              "filename",
              filename,
              "content",
              content,
              "uploadAnalysis",
              Map.of(
                  "originalFilename",
                  filename,
                  "maliciousContent",
                  content,
                  "explanation",
                  template.explanation,
                  "securityTip",
                  template.securityTip)));
    } else {
      return ValidationResult.failure(
          "文件上传失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 文件名: "
              + template.expectedFilename
              + "\n"
              + "2. 文件内容: "
              + template.expectedContent
              + "\n"
              + "3. 攻击原理: "
              + template.explanation
              + "\n"
              + "4. 安全建议: "
              + template.securityTip);
    }
  }

  /** 验证路径穿越漏洞 */
  private ValidationResult validatePathTraversal(String step, Map<String, Object> params) {
    String filepath = (String) params.getOrDefault("filepath", "");
    String action = (String) params.getOrDefault("action", "");

    // 预设的路径穿越payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "../../../etc/passwd",
            "step2", "..\\..\\..\\windows\\system32\\drivers\\etc\\hosts",
            "step3", "....//....//....//etc//passwd");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        filepath.contains("../")
            || filepath.contains("..\\")
            || filepath.contains("....//")
            || filepath.contains("....\\\\");

    if (isValid) {
      return ValidationResult.success(
          "路径穿越成功！访问到敏感文件",
          Map.of(
              "result",
              "成功通过路径穿越访问文件: " + filepath,
              "vulnerabilityType",
              "A01-路径穿越",
              "filepath",
              filepath,
              "pathAnalysis",
              Map.of(
                  "originalPath",
                  filepath,
                  "traversalPayload",
                  expectedPayload,
                  "explanation",
                  "通过../等路径遍历符号绕过目录限制",
                  "securityTip",
                  "使用白名单验证文件路径，禁止使用../等特殊字符")));
    } else {
      return ValidationResult.failure(
          "路径穿越失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试使用: "
              + expectedPayload
              + "\n"
              + "2. 常见payload: ../, ..\\, ....//, ....\\\\\n"
              + "3. 目标文件: /etc/passwd, /windows/system32/drivers/etc/hosts\n"
              + "4. 绕过技巧: 双重编码、大小写变换、特殊字符");
    }
  }

  /** 验证任意文件读取 */
  private ValidationResult validateArbitraryRead(String step, Map<String, Object> params) {
    String filepath = (String) params.getOrDefault("filepath", "");
    String action = (String) params.getOrDefault("action", "");

    // 预设的任意文件读取payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "/etc/passwd",
            "step2", "/proc/version",
            "step3", "/flag.txt");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        filepath.contains(expectedPayload)
            || filepath.contains("flag")
            || filepath.contains("passwd");

    if (isValid) {
      return ValidationResult.success(
          "任意文件读取成功！获取到敏感信息",
          Map.of(
              "result",
              "成功读取文件: " + filepath,
              "vulnerabilityType",
              "A01-任意读取",
              "filepath",
              filepath,
              "readAnalysis",
              Map.of(
                  "targetFile",
                  filepath,
                  "expectedFile",
                  expectedPayload,
                  "explanation",
                  "通过文件读取功能访问系统敏感文件",
                  "securityTip",
                  "限制文件读取权限，使用白名单验证文件路径")));
    } else {
      return ValidationResult.failure(
          "任意文件读取失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试读取: "
              + expectedPayload
              + "\n"
              + "2. 常见目标: /etc/passwd, /proc/version, /flag.txt\n"
              + "3. 攻击原理: 利用文件读取功能访问系统文件\n"
              + "4. 防护建议: 限制文件访问权限，使用白名单验证");
    }
  }

  /** 验证业务逻辑漏洞 */
  private ValidationResult validateBusinessLogic(String step, Map<String, Object> params) {
    String action = (String) params.getOrDefault("action", "");
    String amount = (String) params.getOrDefault("amount", "");
    String target = (String) params.getOrDefault("target", "");

    // 预设的业务逻辑payload
    Map<String, BusinessLogicTemplate> logicTemplates =
        Map.of(
            "step1",
                new BusinessLogicTemplate(
                    "transfer", "1000", "user2", "通过修改转账金额进行业务逻辑攻击", "检查金额验证和权限控制"),
            "step2",
                new BusinessLogicTemplate(
                    "purchase", "-100", "item1", "通过负数金额绕过支付验证", "检查负数金额处理和边界条件"),
            "step3",
                new BusinessLogicTemplate(
                    "refund", "999999", "order1", "通过超大金额进行业务逻辑攻击", "检查金额上限和溢出处理"));

    BusinessLogicTemplate template = logicTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid = false;

    // 灵活的验证逻辑：根据步骤和金额特征验证
    try {
      // 检查action是否匹配
      if (action.equals(template.expectedAction)) {
        // action匹配时，检查amount是否符合要求
        if (amount.equals(template.expectedAmount)) {
          isValid = true;
        } else if (amount.contains("-")) {
          // 负数金额（适用于step2）
          isValid = true;
        } else if (!amount.isEmpty()) {
          int amountValue = Integer.parseInt(amount);
          // 超大金额（适用于step3）或正常金额（适用于step1）
          if (amountValue > 10000 || (amountValue >= 1000 && "step1".equals(step))) {
            isValid = true;
          }
        }
      } else if (!action.isEmpty() && !amount.isEmpty()) {
        // action不匹配时，检查金额特征（允许使用其他action但金额符合要求）
        if (amount.contains("-")) {
          isValid = true; // 负数金额
        } else {
          try {
            int amountValue = Integer.parseInt(amount);
            if (amountValue > 10000) {
              isValid = true; // 超大金额
            } else if (amountValue >= 1000 && "step1".equals(step)) {
              isValid = true; // step1允许正常金额
            }
          } catch (NumberFormatException e) {
            // 忽略格式错误
          }
        }
      }
    } catch (NumberFormatException e) {
      // amount不是数字时的处理
      if (amount.contains("-")) {
        isValid = true;
      }
    }

    if (isValid) {
      return ValidationResult.success(
          "业务逻辑攻击成功！绕过业务验证",
          Map.of(
              "result",
              "成功执行业务逻辑攻击: " + action,
              "vulnerabilityType",
              "A04-业务逻辑",
              "action",
              action,
              "amount",
              amount,
              "logicAnalysis",
              Map.of(
                  "originalAction",
                  action,
                  "maliciousAmount",
                  amount,
                  "explanation",
                  template.explanation,
                  "securityTip",
                  template.securityTip)));
    } else {
      return ValidationResult.failure(
          "业务逻辑攻击失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 操作类型: "
              + template.expectedAction
              + "\n"
              + "2. 金额设置: "
              + template.expectedAmount
              + "\n"
              + "3. 攻击原理: "
              + template.explanation
              + "\n"
              + "4. 防护建议: "
              + template.securityTip);
    }
  }

  /** 验证JWT漏洞 */
  private ValidationResult validateJwtVulnerability(String step, Map<String, Object> params) {
    String token = (String) params.getOrDefault("token", "");
    String algorithm = (String) params.getOrDefault("algorithm", "");

    // 预设的JWT攻击payload
    Map<String, JwtTemplate> jwtTemplates =
        Map.of(
            "step1",
                new JwtTemplate(
                    "none",
                    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJ1c2VybmFtZSI6ImFkbWluIn0.",
                    "通过none算法绕过JWT签名验证",
                    "检查算法验证和签名验证"),
            "step2",
                new JwtTemplate(
                    "HS256",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIn0.",
                    "通过弱密钥进行JWT签名伪造",
                    "检查密钥强度和签名算法"),
            "step3",
                new JwtTemplate(
                    "RS256",
                    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIn0.",
                    "通过公钥进行JWT签名伪造",
                    "检查公钥验证和算法一致性"));

    JwtTemplate template = jwtTemplates.get(step);
    if (template == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        token.contains(template.expectedToken) || algorithm.equals(template.expectedAlgorithm);

    if (isValid) {
      return ValidationResult.success(
          "JWT攻击成功！绕过身份验证",
          Map.of(
              "result",
              "成功绕过JWT验证",
              "vulnerabilityType",
              "A07-JWT漏洞",
              "token",
              token,
              "jwtAnalysis",
              Map.of(
                  "originalToken", token,
                  "maliciousToken", template.expectedToken,
                  "explanation", template.explanation,
                  "securityTip", template.securityTip)));
    } else {
      return ValidationResult.failure(
          "JWT攻击失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 算法类型: "
              + template.expectedAlgorithm
              + "\n"
              + "2. 攻击Token: "
              + template.expectedToken
              + "\n"
              + "3. 攻击原理: "
              + template.explanation
              + "\n"
              + "4. 防护建议: "
              + template.securityTip);
    }
  }

  /** 验证SSRF漏洞 */
  private ValidationResult validateSsrf(String step, Map<String, Object> params) {
    String url = (String) params.getOrDefault("url", "");
    String method = (String) params.getOrDefault("method", "");

    // 预设的SSRF攻击payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "http://127.0.0.1:22",
            "step2", "file:///etc/passwd",
            "step3", "http://169.254.169.254/latest/meta-data/");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        url.contains("127.0.0.1") || url.contains("file://") || url.contains("169.254.169.254");

    if (isValid) {
      return ValidationResult.success(
          "SSRF攻击成功！访问内网资源",
          Map.of(
              "result",
              "成功通过SSRF访问内网资源: " + url,
              "vulnerabilityType",
              "A10-SSRF",
              "url",
              url,
              "ssrfAnalysis",
              Map.of(
                  "originalUrl",
                  url,
                  "maliciousUrl",
                  expectedPayload,
                  "explanation",
                  "通过SSRF攻击访问内网资源",
                  "securityTip",
                  "限制URL访问范围，使用白名单验证")));
    } else {
      return ValidationResult.failure(
          "SSRF攻击失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试URL: "
              + expectedPayload
              + "\n"
              + "2. 常见目标: 127.0.0.1, file://, 169.254.169.254\n"
              + "3. 攻击原理: 利用服务器发起请求访问内网资源\n"
              + "4. 防护建议: 限制URL访问范围，使用白名单验证");
    }
  }

  /** 验证XXE漏洞 */
  private ValidationResult validateXxe(String step, Map<String, Object> params) {
    String xml = (String) params.getOrDefault("xml", "");
    String entity = (String) params.getOrDefault("entity", "");

    // 预设的XXE攻击payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>",
            "step2", "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"http://127.0.0.1:22\">]>",
            "step3", "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///flag.txt\">]>");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        xml.contains("<!DOCTYPE") || xml.contains("<!ENTITY") || xml.contains("SYSTEM");

    if (isValid) {
      return ValidationResult.success(
          "XXE攻击成功！读取敏感文件",
          Map.of(
              "result",
              "成功通过XXE读取文件",
              "vulnerabilityType",
              "A03-XXE",
              "xml",
              xml,
              "xxeAnalysis",
              Map.of(
                  "originalXml",
                  xml,
                  "maliciousXml",
                  expectedPayload,
                  "explanation",
                  "通过XML外部实体注入读取文件",
                  "securityTip",
                  "禁用XML外部实体，使用安全的XML解析器")));
    } else {
      return ValidationResult.failure(
          "XXE攻击失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试XML: "
              + expectedPayload
              + "\n"
              + "2. 常见实体: <!ENTITY xxe SYSTEM \"file:///etc/passwd\">\n"
              + "3. 攻击原理: 通过XML外部实体注入读取文件\n"
              + "4. 防护建议: 禁用XML外部实体，使用安全的XML解析器");
    }
  }

  /** 验证命令执行漏洞 */
  private ValidationResult validateCommandExecution(String step, Map<String, Object> params) {
    String command = (String) params.getOrDefault("command", "");
    String parameter = (String) params.getOrDefault("parameter", "");

    // 预设的命令执行payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "whoami",
            "step2", "id",
            "step3", "cat /flag.txt");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        command.contains(expectedPayload)
            || command.contains("whoami")
            || command.contains("id")
            || command.contains("cat");

    if (isValid) {
      return ValidationResult.success(
          "命令执行成功！执行系统命令",
          Map.of(
              "result",
              "成功执行系统命令: " + command,
              "vulnerabilityType",
              "A03-命令执行",
              "command",
              command,
              "executionAnalysis",
              Map.of(
                  "originalCommand",
                  command,
                  "maliciousCommand",
                  expectedPayload,
                  "explanation",
                  "通过命令注入执行系统命令",
                  "securityTip",
                  "避免直接执行用户输入，使用白名单验证命令")));
    } else {
      return ValidationResult.failure(
          "命令执行失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试命令: "
              + expectedPayload
              + "\n"
              + "2. 常见命令: whoami, id, cat /flag.txt\n"
              + "3. 攻击原理: 通过命令注入执行系统命令\n"
              + "4. 防护建议: 避免直接执行用户输入，使用白名单验证");
    }
  }

  /** 验证配置错误 */
  private ValidationResult validateConfigurationError(String step, Map<String, Object> params) {
    String config = (String) params.getOrDefault("config", "");
    String value = (String) params.getOrDefault("value", "");

    // 预设的配置错误payload
    Map<String, String> expectedPayloads =
        Map.of(
            "step1", "debug=true",
            "step2", "admin=true",
            "step3", "bypass=true");

    String expectedPayload = expectedPayloads.get(step);
    if (expectedPayload == null) {
      return ValidationResult.failure("无效的步骤: " + step);
    }

    boolean isValid =
        config.contains("debug") || config.contains("admin") || config.contains("bypass");

    if (isValid) {
      return ValidationResult.success(
          "配置错误利用成功！绕过安全配置",
          Map.of(
              "result",
              "成功利用配置错误: " + config,
              "vulnerabilityType",
              "A05-配置错误",
              "config",
              config,
              "configAnalysis",
              Map.of(
                  "originalConfig",
                  config,
                  "maliciousConfig",
                  expectedPayload,
                  "explanation",
                  "通过配置错误绕过安全限制",
                  "securityTip",
                  "检查安全配置，禁用调试模式")));
    } else {
      return ValidationResult.failure(
          "配置错误利用失败，请尝试其他payload。\n\n💡 攻击提示：\n"
              + "1. 尝试配置: "
              + expectedPayload
              + "\n"
              + "2. 常见配置: debug=true, admin=true, bypass=true\n"
              + "3. 攻击原理: 利用配置错误绕过安全限制\n"
              + "4. 防护建议: 检查安全配置，禁用调试模式");
    }
  }

  /** 文件上传模板类 */
  public static class FileUploadTemplate {
    public final String expectedFilename;
    public final String expectedContent;
    public final String explanation;
    public final String securityTip;

    public FileUploadTemplate(
        String expectedFilename, String expectedContent, String explanation, String securityTip) {
      this.expectedFilename = expectedFilename;
      this.expectedContent = expectedContent;
      this.explanation = explanation;
      this.securityTip = securityTip;
    }
  }

  /** 业务逻辑模板类 */
  public static class BusinessLogicTemplate {
    public final String expectedAction;
    public final String expectedAmount;
    public final String expectedTarget;
    public final String explanation;
    public final String securityTip;

    public BusinessLogicTemplate(
        String expectedAction,
        String expectedAmount,
        String expectedTarget,
        String explanation,
        String securityTip) {
      this.expectedAction = expectedAction;
      this.expectedAmount = expectedAmount;
      this.expectedTarget = expectedTarget;
      this.explanation = explanation;
      this.securityTip = securityTip;
    }
  }

  /** JWT模板类 */
  public static class JwtTemplate {
    public final String expectedAlgorithm;
    public final String expectedToken;
    public final String explanation;
    public final String securityTip;

    public JwtTemplate(
        String expectedAlgorithm, String expectedToken, String explanation, String securityTip) {
      this.expectedAlgorithm = expectedAlgorithm;
      this.expectedToken = expectedToken;
      this.explanation = explanation;
      this.securityTip = securityTip;
    }
  }

  /** 验证结果类 */
  public static class ValidationResult {
    private boolean success;
    private String message;
    private Map<String, Object> data;

    public static ValidationResult success(String message) {
      ValidationResult result = new ValidationResult();
      result.success = true;
      result.message = message;
      return result;
    }

    public static ValidationResult success(String message, Map<String, Object> data) {
      ValidationResult result = new ValidationResult();
      result.success = true;
      result.message = message;
      result.data = data;
      return result;
    }

    public static ValidationResult failure(String message) {
      ValidationResult result = new ValidationResult();
      result.success = false;
      result.message = message;
      return result;
    }

    // Getters and Setters
    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Map<String, Object> getData() {
      return data;
    }

    public void setData(Map<String, Object> data) {
      this.data = data;
    }
  }
}
