package com.javaweb.security.bootstrap;

import com.javaweb.security.entity.VulnerabilityCategory;
import com.javaweb.security.entity.VulnerabilityContent;
import com.javaweb.security.repository.VulnerabilityCategoryRepository;
import com.javaweb.security.repository.VulnerabilityContentRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Initializes baseline knowledge center data for local/demo environments so that the front-end can
 * render meaningful content without manual database seeding.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class KnowledgeDataInitializer implements CommandLineRunner {

  private final VulnerabilityCategoryRepository categoryRepository;
  private final VulnerabilityContentRepository contentRepository;

  @Value("${app.demo.seed-knowledge:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      log.info("Knowledge data seeding disabled via app.demo.seed-knowledge=false");
      return;
    }

    log.info("Seeding baseline knowledge data for demo environment...");
    Map<String, VulnerabilityCategory> categories = seedCategories();
    seedContents(categories);
    log.info(
        "Knowledge data seeding completed. categories={}, contents={}",
        categoryRepository.count(),
        contentRepository.count());
  }

  private Map<String, VulnerabilityCategory> seedCategories() {
    Map<String, VulnerabilityCategory> result = new HashMap<>();
    result.put(
        "A01",
        ensureCategory("A01", "Broken Access Control", "访问控制失效", "常见越权访问场景及防护策略", "high", 1));
    result.put(
        "A02",
        ensureCategory("A02", "Cryptographic Failures", "加密失败", "敏感数据加密与传输保护", "critical", 2));
    result.put("A03", ensureCategory("A03", "Injection", "注入漏洞", "SQL/XSS 等注入漏洞防御", "high", 3));
    result.put(
        "A07",
        ensureCategory(
            "A07",
            "Identification and Authentication Failures",
            "身份验证失败",
            "身份认证和会话管理安全",
            "high",
            7));
    result.put(
        "A05",
        ensureCategory("A05", "Security Misconfiguration", "安全配置错误", "系统配置和框架安全", "medium", 5));
    result.put(
        "A08",
        ensureCategory(
            "A08", "Software and Data Integrity Failures", "软件和数据完整性失效", "反序列化和依赖注入安全", "high", 8));
    result.put(
        "A06",
        ensureCategory(
            "A06", "Vulnerable and Outdated Components", "过时组件", "组件安全更新和版本管理", "medium", 6));
    result.put(
        "A09",
        ensureCategory(
            "A09", "Security Logging and Monitoring Failures", "日志监控失效", "安全日志记录和监控", "medium", 9));
    result.put(
        "A10",
        ensureCategory("A10", "Server-Side Request Forgery", "服务端请求伪造", "SSRF攻击防护", "high", 10));
    result.put("A04", ensureCategory("A04", "Insecure Design", "不安全设计", "安全架构和设计缺陷", "high", 4));

    return result;
  }

  private VulnerabilityCategory ensureCategory(
      String code,
      String englishName,
      String name,
      String description,
      String severity,
      int order) {
    return categoryRepository
        .findByCategoryCodeAndOwaspYear(code, 2021)
        .orElseGet(
            () -> {
              try {
                return categoryRepository.save(
                    createCategory(code, englishName, name, description, severity, order));
              } catch (DataIntegrityViolationException ex) {
                log.debug(
                    "Category {} already exists, reuse existing entry. cause={}",
                    code,
                    ex.getMessage());
                return categoryRepository
                    .findByCategoryCodeAndOwaspYear(code, 2021)
                    .orElseThrow(() -> ex);
              }
            });
  }

  private VulnerabilityCategory createCategory(
      String code,
      String englishName,
      String name,
      String description,
      String severity,
      int order) {
    VulnerabilityCategory category = new VulnerabilityCategory();
    category.setCategoryCode(code);
    category.setCategoryName(name);
    category.setCategoryDescription("%s (%s)".formatted(description, englishName));
    category.setSeverityLevel(severity);
    category.setOwaspYear(2021);
    category.setOrderNum(order);
    category.setActive(Boolean.TRUE);
    category.setIconUrl("/assets/icons/%s.svg".formatted(code.toLowerCase()));
    category.setColorTheme(
        switch (severity) {
          case "critical" -> "#be123c";
          case "high" -> "#ef4444";
          case "medium" -> "#f97316";
          default -> "#22c55e";
        });
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedAt(LocalDateTime.now());
    return category;
  }

  private void seedContents(Map<String, VulnerabilityCategory> categories) {

    List<VulnerabilityContent> contents =
        List.of(
            createContent(
                categories.get("A01"),
                "API 越权访问实战",
                "通过构造请求绕过身份验证",
                "演示如何利用缺失的鉴权逻辑读取他人数据，并通过 RBAC 修复。",
                "# API越权访问漏洞详解\n\n## 漏洞定义\n\nAPI越权访问是指应用程序在验证用户权限时存在缺陷，导致攻击者可以访问本应受限的API资源或功能。这种漏洞通常分为水平越权和垂直越权两种类型。\n\n## 漏洞类型\n\n### 1. 水平越权（Horizontal Privilege Escalation）\n- 攻击者可以访问同级别其他用户的资源\n- 例如：用户A可以查看用户B的订单信息\n- 影响：数据泄露、隐私侵犯\n\n### 2. 垂直越权（Vertical Privilege Escalation）\n- 攻击者可以获取更高权限的功能\n- 例如：普通用户获得管理员权限\n- 影响：系统控制、权限滥用\n\n## 攻击场景分析\n\n### 场景1：订单接口越权访问\n**攻击步骤：**\n1. 攻击者登录普通用户账户\n2. 通过修改URL参数中的订单ID\n3. 访问其他用户的订单信息\n4. 获取敏感商业数据\n\n**技术原理：**\n- 后端缺少权限验证逻辑\n- 直接根据ID查询数据库\n- 没有验证资源所有权\n\n### 场景2：用户信息越权访问\n**攻击步骤：**\n1. 攻击者发现用户信息接口\n2. 修改用户ID参数\n3. 获取其他用户的个人信息\n4. 进行社会工程学攻击\n\n**技术原理：**\n- 接口设计不当\n- 缺少用户身份验证\n- 没有资源访问控制\n\n## 危害场景分析\n\n### 系统安全风险\n**风险等级：高危**\n\n**具体风险：**\n1. **数据泄露风险**\n   - 用户个人信息泄露：姓名、手机号、邮箱、身份证号等敏感信息被非法获取\n   - 商业机密数据暴露：客户订单、财务数据、商业策略等核心信息泄露\n   - 财务信息被盗取：银行账户、支付信息、交易记录等金融数据泄露\n\n2. **业务风险**\n   - 客户信任度下降：用户对平台安全性失去信心，用户流失率增加\n   - 法律合规问题：违反数据保护法规，面临巨额罚款和法律诉讼\n   - 品牌声誉受损：安全事件被媒体曝光，企业形象严重受损\n\n3. **技术风险**\n   - 系统完整性破坏：攻击者可能篡改系统数据，影响业务正常运行\n   - 权限体系失效：整个权限控制系统被绕过，安全防护失效\n   - 安全控制绕过：其他安全措施也可能被类似方式绕过\n\n### 实际案例\n**案例1：GitLab越权漏洞CVE-2020-10977**\n- 影响：通过修改项目ID参数，攻击者可以访问其他用户的私有项目，导致敏感代码泄露\n- 损失：企业源代码泄露，商业机密暴露，竞争对手获得技术优势\n- 修复：添加项目访问权限验证，实施严格的资源所有权检查\n\n**案例2：某电商平台订单越权**\n- 影响：用户通过修改订单ID参数，可以查看其他用户的订单信息，包括商品详情、价格、收货地址等敏感信息\n- 损失：客户隐私泄露，法律合规问题，平台声誉受损\n- 修复：实施订单所有权验证，添加用户身份检查\n\n**案例3：某社交平台用户信息越权**\n- 影响：通过修改用户ID参数，攻击者可以获取其他用户的个人信息，包括手机号、邮箱、真实姓名等\n- 损失：用户隐私泄露，平台声誉受损，用户信任度下降\n- 修复：添加用户身份验证和权限检查，实施数据访问控制\n\n**案例4：某企业管理系统权限绕过**\n- 影响：普通员工通过修改URL参数，可以访问管理员功能，包括用户管理、系统配置等\n- 损失：权限体系失效，系统安全风险，内部数据泄露\n- 修复：实施严格的角色权限控制，添加权限验证机制\n\n## 攻击演示\n\n### 不安全实现\n**漏洞代码分析：**\n```java\n@GetMapping(\"/api/orders/{id}\")\npublic Order getOrder(@PathVariable Long id) {\n    // 危险：直接返回订单，没有权限检查\n    return orderRepository.findById(id).orElseThrow();\n}\n```\n\n**攻击载荷：**\n- 修改URL参数：/api/orders/123 → /api/orders/456\n- 绕过权限验证\n- 获取他人订单信息\n\n**攻击流程：**\n1. 攻击者登录普通用户账户\n2. 访问自己的订单：GET /api/orders/123\n3. 修改订单ID：GET /api/orders/456\n4. 成功获取他人订单信息\n5. 利用获取的信息进行进一步攻击\n\n### 安全实现\n**安全代码：**\n```java\n@PreAuthorize(\"hasRole('ADMIN') or @orderPermissionEvaluator.isOwner(#id)\")\n@GetMapping(\"/api/orders/{id}\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.getAuthorizedOrder(id);\n}\n```\n\n**防护机制：**\n- 基于角色的访问控制\n- 资源所有权验证\n- 权限评估器检查\n\n**安全流程：**\n1. 用户请求访问订单\n2. 系统验证用户身份\n3. 检查用户是否有权限访问该订单\n4. 验证通过后返回订单信息\n5. 记录访问日志",
                "通过演示订单接口越权访问，展示如何利用缺失的权限验证获取他人敏感数据。",
                """
@GetMapping("/api/v1/demo/a01/orders/{orderId}/vulnerable")
public ResponseEntity<ApiResult<Order>> getOrderVulnerable(@PathVariable Long orderId) {
    // ⚠️ 危险实现：直接根据ID查询，没有权限验证
    Optional<Order> orderOpt = orderRepository.findById(orderId);
    if (orderOpt.isPresent()) {
        return ResponseEntity.ok(ApiResult.success("成功获取订单信息（不安全实现）", orderOpt.get()));
    }
    return ResponseEntity.ok(ApiResult.error("订单不存在"));
}
""",
                """
@GetMapping("/api/v1/demo/a01/orders/{orderId}/secure")
public ResponseEntity<ApiResult<Order>> getOrderSecure(@PathVariable Long orderId) {
    // ✅ 安全实现：验证用户权限
    Long currentUserId = authenticationService.getCurrentUserId();
    Optional<Order> orderOpt = orderRepository.findByIdAndUserId(orderId, currentUserId);
    if (orderOpt.isPresent()) {
        return ResponseEntity.ok(ApiResult.success("成功获取订单信息（安全实现）", orderOpt.get()));
    }
    return ResponseEntity.ok(ApiResult.error("订单不存在或无权限访问"));
}
""",
                "## 修复建议\n\n### 代码层面修复\n**1. 服务端权限验证**\n```java\n@GetMapping(\"/api/orders/{id}\")\npublic Order getOrder(@PathVariable Long id, Authentication auth) {\n    // 验证用户身份\n    if (auth == null || !auth.isAuthenticated()) {\n        throw new AuthenticationException(\"用户未认证\");\n    }\n    \n    // 验证资源所有权\n    Order order = orderService.findById(id);\n    if (!order.getUserId().equals(getCurrentUserId(auth))) {\n        throw new AccessDeniedException(\"无权访问他人订单\");\n    }\n    \n    return order;\n}\n```\n\n**2. 权限注解使用**\n```java\n@PreAuthorize(\"hasRole('ADMIN') or @orderPermissionEvaluator.isOwner(#id)\")\n@GetMapping(\"/api/orders/{id}\")\npublic Order getOrder(@PathVariable Long id) {\n    return orderService.getAuthorizedOrder(id);\n}\n```\n\n**3. 业务层权限检查**\n```java\n@Service\npublic class OrderService {\n    \n    public Order getAuthorizedOrder(Long orderId) {\n        Order order = orderRepository.findById(orderId)\n            .orElseThrow(() -> new OrderNotFoundException(\"订单不存在\"));\n        \n        // 权限检查\n        if (!hasPermissionToAccess(order)) {\n            throw new AccessDeniedException(\"无权访问该订单\");\n        }\n        \n        return order;\n    }\n}\n```\n\n### 配置层面修复\n**1. Spring Security配置**\n```java\n@Configuration\n@EnableWebSecurity\npublic class SecurityConfig {\n    \n    @Bean\n    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {\n        http\n            .authorizeHttpRequests(authz -> authz\n                .requestMatchers(\"/api/orders/**\").hasRole(\"USER\")\n                .requestMatchers(\"/api/admin/**\").hasRole(\"ADMIN\")\n                .anyRequest().authenticated()\n            )\n            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));\n        \n        return http.build();\n    }\n}\n```\n\n**2. 权限评估器配置**\n```java\n@Component(\"orderPermissionEvaluator\")\npublic class OrderPermissionEvaluator {\n    \n    public boolean isOwner(Long orderId) {\n        // 实现订单所有权验证逻辑\n        return orderService.isOwner(orderId, getCurrentUserId());\n    }\n}\n```\n\n### 安全策略建议\n**1. 最小权限原则**\n- 用户只能访问自己创建的资源\n- 实施基于角色的访问控制\n- 定期审查权限配置\n\n**2. 安全开发流程**\n- 代码审查时检查权限验证\n- 使用自动化安全测试\n- 实施安全编码标准\n\n**3. 权限管理策略**\n- 定期审计用户权限\n- 实施权限变更审批\n- 建立权限回收机制\n\n### 检测与监控\n**1. 安全监控工具**\n- ELK Stack (Elasticsearch, Logstash, Kibana)：开源日志分析平台\n- Splunk企业安全解决方案：商业级安全信息管理平台\n- OWASP ZAP API安全测试：开源Web应用安全扫描器\n\n**2. 监控指标**\n- 异常访问模式检测：短时间内大量API调用、跨用户资源访问尝试\n- 权限验证失败统计：频繁的403错误、权限检查失败日志\n- 越权访问尝试告警：未授权访问行为检测\n\n**3. 告警机制**\n- 实时越权访问告警：立即通知安全团队\n- 权限验证失败通知：监控权限系统状态\n- 异常API调用模式检测：检测自动化攻击",
                "[{\"title\":\"GitLab越权漏洞CVE-2020-10977\",\"description\":\"通过修改项目ID参数，攻击者可以访问其他用户的私有项目，导致敏感代码泄露。影响版本：GitLab 8.5-12.9。修复方案：添加项目访问权限验证。\"},{\"title\":\"某电商平台订单越权\",\"description\":\"用户通过修改订单ID参数，可以查看其他用户的订单信息，包括商品详情、价格、收货地址等敏感信息。影响：客户隐私泄露，法律合规问题。修复：实施订单所有权验证。\"},{\"title\":\"某社交平台用户信息越权\",\"description\":\"通过修改用户ID参数，攻击者可以获取其他用户的个人信息，包括手机号、邮箱、真实姓名等。影响：用户隐私泄露，平台声誉受损。修复：添加用户身份验证和权限检查。\"},{\"title\":\"某企业管理系统权限绕过\",\"description\":\"普通员工通过修改URL参数，可以访问管理员功能，包括用户管理、系统配置等。影响：权限体系失效，系统安全风险。修复：实施严格的角色权限控制。\"}]",
                "[{\"title\":\"OWASP Authorization Cheat Sheet\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html\"},{\"title\":\"Spring Security官方文档\",\"url\":\"https://spring.io/projects/spring-security\"},{\"title\":\"API安全最佳实践\",\"url\":\"https://owasp.org/www-project-api-security/\"}]",
                "intermediate",
                35,
                268,
                24,
                1),
            createContent(
                categories.get("A02"),
                "明文传输导致的凭证泄露",
                "通过HTTP传输敏感数据",
                "演示如何利用明文传输和弱加密算法导致的数据泄露风险。",
                "# 加密失败漏洞详解\n\n## 漏洞定义\n\n加密失败（Cryptographic Failures），也称为敏感数据暴露，是指应用程序在处理敏感数据时，未能正确使用加密技术，或者使用了弱加密算法、不安全的密钥管理方式，导致敏感数据在传输或存储过程中被泄露或篡改。\n\n## 漏洞类型\n\n### 1. 敏感数据明文传输\n- **描述**：数据在网络传输过程中未加密，或使用HTTP而非HTTPS。\n- **影响**：凭证泄露、会话劫持、数据窃听。\n- **示例**：HTTP登录请求中传输明文密码。\n\n### 2. 弱加密算法使用\n- **描述**：使用过时、已知漏洞或强度不足的加密算法（如MD5、DES）。\n- **影响**：加密数据易被破解，敏感信息暴露。\n- **示例**：使用MD5存储用户密码，或使用DES加密敏感文件。\n\n### 3. 密钥管理不当\n- **描述**：加密密钥硬编码在代码中、默认密钥、密钥未轮换、密钥存储不安全。\n- **影响**：攻击者获取密钥后可解密所有数据。\n- **示例**：API密钥直接写在前端代码中，或所有用户使用相同加密密钥。\n\n### 4. 证书管理缺陷\n- **描述**：SSL/TLS证书过期、自签名证书、证书验证不严格。\n- **影响**：中间人攻击、信任链中断。\n- **示例**：客户端未验证服务器证书，导致中间人伪造证书。\n\n## 攻击场景分析\n\n### 场景1：HTTP明文登录凭证泄露\n**攻击步骤：**\n1. 攻击者在公共Wi-Fi环境下部署抓包工具（如Wireshark）。\n2. 用户通过HTTP访问网站并登录。\n3. 抓包工具捕获到HTTP请求，直接获取到用户的明文用户名和密码。\n4. 攻击者使用获取到的凭证登录用户账户。\n\n**技术原理：**\n- 应用程序未强制使用HTTPS。\n- 敏感数据（如用户名、密码）在HTTP请求体中明文传输。\n- 缺乏HSTS（HTTP Strict Transport Security）等安全机制防止协议降级。\n\n### 场景2：弱密码哈希导致密码泄露\n**攻击步骤：**\n1. 攻击者通过SQL注入或其他方式获取到数据库中用户密码的哈希值。\n2. 发现密码哈希使用的是MD5等弱算法。\n3. 使用彩虹表或暴力破解工具对哈希值进行破解。\n4. 成功还原出用户明文密码。\n\n**技术原理：**\n- 应用程序使用弱哈希算法（如MD5、SHA1）存储密码。\n- 未使用加盐（Salt）或迭代次数不足，导致哈希值容易被破解。\n- 缺乏强密码策略，用户设置的密码简单。\n\n## 危害场景分析\n\n### 系统安全风险\n**风险等级：高危**\n\n**具体风险：**\n1. **敏感数据泄露**\n   - 用户凭证（用户名、密码、Token）泄露。\n   - 个人身份信息（PII）、财务数据、健康数据泄露。\n   - 商业机密、知识产权泄露。\n\n2. **身份盗用与欺诈**\n   - 攻击者利用泄露凭证登录用户账户。\n   - 进行未经授权的交易或操作。\n   - 冒充用户进行钓鱼或诈骗。\n\n3. **系统完整性破坏**\n   - 攻击者获取密钥后可篡改数据。\n   - 绕过安全控制，执行恶意操作。\n   - 导致系统不可用或数据损坏。\n\n### 实际案例\n**案例1：Equifax数据泄露事件（2017）**\n- **影响**：1.47亿美国用户敏感数据泄露，包括社会安全号、出生日期、地址等。\n- **原因**：部分原因是未及时修补Apache Struts漏洞，以及内部系统加密和数据隔离不足。\n- **损失**：公司声誉严重受损，高管辞职，面临巨额罚款和法律诉讼。\n- **修复**：加强漏洞管理，改进数据加密和访问控制策略。\n\n**案例2：LinkedIn密码泄露事件（2012）**\n- **影响**：640万用户密码哈希值泄露。\n- **原因**：使用不加盐的SHA-1哈希算法存储密码。\n- **损失**：大量用户密码被破解，导致身份盗用风险。\n- **修复**：迁移到更安全的哈希算法（如bcrypt），并对所有密码加盐。\n\n**案例3：某银行API密钥泄露**\n- **影响**：API密钥硬编码在前端代码中，导致第三方服务访问权限泄露。\n- **损失**：攻击者可以访问银行API，进行未授权交易。\n- **修复**：实施安全的密钥管理，使用环境变量和密钥管理服务。\n\n**案例4：某电商平台明文传输**\n- **影响**：用户支付信息通过HTTP传输，被中间人攻击截获。\n- **损失**：用户信用卡信息泄露，导致金融欺诈。\n- **修复**：强制使用HTTPS，实施HSTS策略。\n\n## 攻击演示\n\n### 不安全实现\n**漏洞代码分析：**\n```java\n// 使用弱加密算法MD5\npublic String hashPassword(String password) {\n    MessageDigest md = MessageDigest.getInstance(\"MD5\");\n    return Base64.getEncoder().encodeToString(md.digest(password.getBytes()));\n}\n\n// 硬编码密钥\nprivate static final String SECRET_KEY = \"weakkey123\";\n\n// HTTP明文传输\n@PostMapping(\"/login\")\npublic ResponseEntity<?> login(@RequestBody LoginRequest request) {\n    // 危险：明文传输密码\n    return userService.authenticate(request.getUsername(), request.getPassword());\n}\n```\n\n**攻击载荷：**\n- 使用Wireshark等抓包工具监听网络流量。\n- 访问HTTP登录页面，输入用户名和密码。\n- 在抓包结果中直接看到明文传输的凭证。\n\n**攻击流程：**\n1. 攻击者部署网络监听工具\n2. 用户通过HTTP访问登录页面\n3. 用户输入用户名和密码并提交\n4. 攻击者捕获到明文传输的凭证\n5. 攻击者使用获取的凭证登录系统\n\n### 安全实现\n**安全代码：**\n```java\n// 使用强加密算法bcrypt\n@Bean\npublic PasswordEncoder passwordEncoder() {\n    return new BCryptPasswordEncoder(12);\n}\n\n// 安全的密钥管理\n@Value(\"${app.encryption.key}\")\nprivate String encryptionKey;\n\n// HTTPS强制传输\n@PostMapping(\"/login\")\n@RequiresChannel(\"https\")\npublic ResponseEntity<?> login(@RequestBody LoginRequest request) {\n    return userService.authenticate(request.getUsername(), request.getPassword());\n}\n```\n\n**防护机制：**\n- 强制使用HTTPS，确保所有通信加密。\n- 配置TLS 1.3等强加密协议和密码套件。\n- 启用HSTS，防止协议降级攻击。\n\n**安全流程：**\n1. 用户通过HTTPS访问登录页面\n2. 系统验证TLS证书有效性\n3. 用户输入凭证并加密传输\n4. 服务器使用强加密算法处理密码\n5. 记录安全日志\n\n## 修复建议\n\n### 代码层面修复\n**1. 强制使用HTTPS**\n- 在应用代码中实现HTTP到HTTPS的重定向。\n- 使用Spring Security等框架配置强制HTTPS。\n```java\n@Configuration\n@EnableWebSecurity\npublic class SecurityConfig {\n    @Bean\n    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {\n        http\n            .requiresChannel(channel -> channel.anyRequest().requiresSecure()) // 强制HTTPS\n            // ... 其他配置\n            return http.build();\n    }\n}\n```\n\n**2. 强密码哈希**\n- 使用BCrypt、Argon2或PBKDF2等现代密码哈希算法。\n- 对每个密码使用独立的随机盐值。\n```java\n// 使用BCryptPasswordEncoder\n@Bean\npublic PasswordEncoder passwordEncoder() {\n    return new BCryptPasswordEncoder();\n}\n\n// 存储密码\nString hashedPassword = passwordEncoder.encode(\"userPassword\");\n\n// 验证密码\nboolean matches = passwordEncoder.matches(\"userPassword\", hashedPassword);\n```\n\n**3. 安全密钥管理**\n- 避免硬编码密钥，使用环境变量、密钥管理服务（KMS）或配置文件安全存储。\n- 定期轮换密钥。\n- 对敏感数据使用加密库进行加密。\n```java\n// 从安全配置中获取密钥\n@Value(\"${app.encryption.key}\")\nprivate String encryptionKey;\n\npublic String encrypt(String data) {\n    // 使用AES等强算法加密数据\n    // ...\n}\n```\n\n### 配置层面修复\n**1. Web服务器配置**\n- 配置Nginx/Apache强制HTTPS，并启用HSTS。\n- 禁用弱TLS协议版本（如TLS 1.0/1.1）和弱密码套件。\n```nginx\n# Nginx HTTPS 配置示例\nserver {\n    listen 443 ssl;\n    server_name yourdomain.com;\n\n    ssl_certificate /etc/nginx/ssl/yourdomain.com.crt;\n    ssl_certificate_key /etc/nginx/ssl/yourdomain.com.key;\n    ssl_protocols TLSv1.2 TLSv1.3; # 禁用弱协议\n    ssl_ciphers 'TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256'; # 强密码套件\n    add_header Strict-Transport-Security \"max-age=31536000; includeSubDomains\" always; # HSTS\n\n    # ... 其他配置\n}\n```\n\n**2. 数据库加密**\n- 对存储在数据库中的敏感数据进行字段级加密。\n- 使用数据库提供的透明数据加密（TDE）功能。\n\n**3. 文件系统加密**\n- 对存储敏感文件的服务器磁盘进行全盘加密。\n\n### 安全策略建议\n**1. 数据分类与保护**\n- 对数据进行分类（敏感、非敏感），并根据分类制定不同的保护策略。\n- 明确哪些数据需要加密，以及加密的强度要求。\n\n**2. 密钥管理策略**\n- 制定完善的密钥生成、存储、分发、轮换和销毁策略。\n- 使用硬件安全模块（HSM）或云KMS服务管理密钥。\n\n**3. 安全开发培训**\n- 对开发人员进行加密最佳实践和安全编码培训。\n- 强调在设计和实现阶段考虑数据保护。\n\n## 检测与监控\n\n### 安全监控工具\n**1. 网络流量监控**\n- Wireshark：用于捕获和分析网络流量，检测明文传输。\n- IDS/IPS（入侵检测/防御系统）：检测异常流量和攻击模式。\n\n**2. 日志管理系统**\n- ELK Stack (Elasticsearch, Logstash, Kibana)：收集、分析和可视化应用和服务器日志。\n- Splunk：企业级日志管理和安全信息事件管理（SIEM）平台。\n\n**3. 漏洞扫描工具**\n- Nessus、OpenVAS：扫描系统和应用，发现弱加密配置和SSL/TLS漏洞。\n- SSL Labs Server Test：在线测试网站的SSL/TLS配置安全性。\n\n### 监控指标\n**1. 未加密流量**\n- 检测到HTTP流量中包含敏感信息。\n- 非HTTPS端口的敏感数据传输。\n\n**2. 弱加密使用**\n- 日志中出现使用弱加密算法的警告。\n- 证书过期或配置错误的告警。\n\n**3. 密钥访问异常**\n- 密钥管理系统（KMS）的异常访问尝试。\n- 密钥轮换失败或长时间未轮换。\n\n### 告警机制\n**1. 实时告警**\n- 检测到明文传输敏感数据时立即告警。\n- 弱加密算法被使用或证书异常时告警。\n\n**2. 定期报告**\n- 加密配置合规性报告。\n- 密钥管理审计报告。\n- 敏感数据访问审计报告。\n\n## 参考资料\n- [OWASP Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)\n- [NIST SP 800-57 Part 1 Revision 5: Recommendation for Key Management](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-57pt1r5.pdf)\n- [TLS/SSL Best Practices](https://mozilla.github.io/server-side-tls/ssl-config-generator/)",
                "通过演示弱加密算法和密钥管理问题，展示加密失败的安全风险。",
                """
// 存在漏洞的代码 - 弱加密算法
public class WeakEncryption {
    public String encrypt(String data) {
        // 使用弱加密算法MD5
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // 硬编码密钥
    private static final String SECRET_KEY = "weakkey123";

    public String encryptWithKey(String data) {
        // 简单的XOR加密
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            result.append((char) (data.charAt(i) ^ SECRET_KEY.charAt(i % SECRET_KEY.length())));
        }
        return result.toString();
    }
}
""",
                """
// 安全的代码 - 强加密算法
public class SecureEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 256;

    public String encrypt(String data, String password) {
        try {
            // 生成强密钥
            SecretKeySpec key = generateKey(password);

            // 使用AES-GCM模式
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedData = cipher.doFinal(data.getBytes());
            byte[] iv = cipher.getIV();

            // 组合IV和加密数据
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    private SecretKeySpec generateKey(String password) {
        // 使用PBKDF2生成密钥
        byte[] salt = generateSalt();
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100000, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, ALGORITHM);
    }
}
""",
                "## 修复建议\n\n### 1. 使用强加密算法\n- 使用AES-256进行对称加密\n- 使用RSA-2048或ECC进行非对称加密\n- 使用SHA-256或更强的哈希算法\n- 避免使用MD5、SHA1、DES等弱算法\n\n### 2. 安全的密钥管理\n- 使用密钥管理服务（KMS）\n- 定期轮换密钥\n- 避免硬编码密钥\n- 使用环境变量或配置文件存储密钥\n\n### 3. 传输安全\n- 强制使用HTTPS\n- 配置HSTS防止降级攻击\n- 使用TLS 1.2或更高版本\n- 正确配置证书\n\n### 4. 加密实现最佳实践\n- 使用经过验证的加密库\n- 正确使用初始化向量（IV）\n- 实施适当的填充方案\n- 定期进行安全审计",
                "[{\"title\":\"Equifax数据泄露\",\"description\":\"弱加密算法导致大规模数据泄露\"},{\"title\":\"银行系统加密缺陷\",\"description\":\"密钥管理不当导致客户数据泄露\"}]",
                "[{\"title\":\"OWASP加密失败指南\",\"url\":\"https://owasp.org/Top10/A02_2021-Cryptographic_Failures/\"},{\"title\":\"加密最佳实践\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html\"}]",
                "intermediate",
                50,
                285,
                38,
                2),
            createContent(
                categories.get("A03"),
                "SQL 注入从入门到防御",
                "利用拼接 SQL 绕过登录验证",
                "提供经典注入 payload 与参数化查询修复方案，适合课堂演示。",
                "## 学习目标\n- 熟悉 Union/布尔盲注\n- 掌握参数化查询与输入校验",
                "通过在线靶场执行联合注入并提取敏感数据，随后切换到预编译语句验证修复效果。",
                """
String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);
""",
                """
PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
stmt.setString(1, username);
stmt.setString(2, password);
ResultSet rs = stmt.executeQuery();
""",
                "- 使用参数化查询避免拼接 SQL。\n- 对输入进行白名单校验。\n- 限制数据库账号权限。",
                "[{\"title\":\"大型门户 SQL 注入事件\",\"description\":\"登录接口存在注入导致数据泄露\"}]",
                "[{\"title\":\"OWASP SQL Injection Prevention\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html\"}]",
                "beginner",
                40,
                310,
                42,
                3),
            createContent(
                categories.get("A07"),
                "身份验证失败 - 弱密码策略",
                "绕过身份验证获取系统访问权限",
                "演示如何利用弱密码策略、会话管理缺陷等身份验证漏洞获取系统访问权限。",
                "# 身份验证失败详解\n\n## 什么是身份验证失败\n\n身份验证失败（Identification and Authentication Failures）是指应用程序在验证用户身份过程中存在的安全缺陷，包括弱密码策略、会话管理不当、多因素认证缺失等问题。\n\n## 漏洞类型\n\n### 1. 弱密码策略\n- 允许使用弱密码\n- 密码复杂度要求不足\n- 缺乏密码历史检查\n\n### 2. 会话管理缺陷\n- 会话令牌可预测\n- 会话固定攻击\n- 会话超时设置不当\n\n### 3. 认证绕过\n- 默认凭据未修改\n- 认证逻辑缺陷\n- 多因素认证缺失\n\n## 攻击影响\n\n- 账户接管：完全控制用户账户\n- 权限提升：获得更高系统权限\n- 数据泄露：访问敏感信息\n- 系统控制：获得系统管理权限",
                "通过演示弱密码攻击、会话劫持等方式，展示身份验证漏洞的危害。",
                """
// 存在漏洞的代码 - 弱密码策略
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 没有密码复杂度检查
    User user = userService.findByUsername(request.getUsername());
    if (user != null && user.getPassword().equals(request.getPassword())) {
        // 直接比较明文密码，没有哈希验证
        String token = generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }
    return ResponseEntity.status(401).body("登录失败");
}

// 会话管理缺陷
@GetMapping("/profile")
public UserProfile getProfile(HttpSession session) {
    // 没有验证会话是否有效
    Long userId = (Long) session.getAttribute("userId");
    return userService.getUserProfile(userId);
}
""",
                """
// 安全的代码 - 强密码策略和会话管理
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 密码复杂度验证
    if (!isPasswordStrong(request.getPassword())) {
        return ResponseEntity.badRequest().body("密码不符合安全要求");
    }

    User user = userService.findByUsername(request.getUsername());
    if (user != null && passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        // 使用安全的密码哈希比较
        String token = generateSecureToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // 记录失败登录尝试
    auditService.logFailedLogin(request.getUsername(), getClientIP());
    return ResponseEntity.status(401).body("登录失败");
}

@GetMapping("/profile")
public UserProfile getProfile(Authentication auth) {
    // 使用Spring Security的认证机制
    if (auth == null || !auth.isAuthenticated()) {
        throw new AuthenticationException("用户未认证");
    }
    Long userId = getCurrentUserId(auth);
    return userService.getUserProfile(userId);
}
""",
                "## 修复建议\n\n### 1. 强密码策略\n- 实施最小密码长度要求（至少8位）\n- 要求包含大小写字母、数字和特殊字符\n- 禁止使用常见弱密码\n- 实施密码历史检查\n\n### 2. 安全的会话管理\n- 使用不可预测的会话令牌\n- 实施适当的会话超时\n- 在权限变更时重新生成会话\n- 使用HttpOnly和Secure Cookie标志\n\n### 3. 多因素认证\n- 实施双因素认证（2FA）\n- 支持TOTP、SMS、邮件验证\n- 为敏感操作要求额外验证\n\n### 4. 安全监控\n- 记录所有登录尝试\n- 监控异常登录模式\n- 实施账户锁定机制\n- 设置安全告警",
                "[{\"title\":\"Equifax数据泄露\",\"description\":\"弱密码和默认凭据导致大规模数据泄露\"},{\"title\":\"Twitter账户劫持\",\"description\":\"会话管理缺陷导致名人账户被攻击\"}]",
                "[{\"title\":\"OWASP身份验证指南\",\"url\":\"https://owasp.org/Top10/A07_2021-Identification_and_Authentication_Failures/\"},{\"title\":\"密码安全最佳实践\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html\"}]",
                "intermediate",
                50,
                285,
                38,
                4),
            createContent(
                categories.get("A05"),
                "安全配置错误 - CSRF攻击",
                "跨站请求伪造攻击演示",
                "演示如何利用缺失的CSRF防护机制，通过构造恶意请求执行未授权操作。",
                "# 安全配置错误详解\n\n## 什么是安全配置错误\n\n安全配置错误（Security Misconfiguration）是指应用程序、框架、库、服务器等组件的安全配置不当，导致系统存在安全风险。\n\n## 常见配置错误\n\n### 1. CSRF（跨站请求伪造）\n- 缺少CSRF令牌验证\n- 不正确的同源策略配置\n- 敏感操作缺少二次确认\n\n### 2. XXE（XML外部实体）\n- XML解析器配置不当\n- 外部实体引用未禁用\n- 文件系统访问权限过大\n\n### 3. 安全头缺失\n- 缺少Content-Security-Policy\n- 缺少X-Frame-Options\n- 缺少X-XSS-Protection\n\n### 4. 默认配置\n- 使用默认用户名密码\n- 启用不必要的服务\n- 暴露敏感信息\n\n## 攻击影响\n\n- 未授权操作：以用户身份执行敏感操作\n- 数据泄露：获取敏感信息\n- 系统控制：获得系统访问权限\n- 服务中断：导致服务不可用",
                "通过构造恶意请求，演示CSRF攻击如何绕过用户确认执行敏感操作。",
                """
// 存在漏洞的代码 - 缺少CSRF防护
@PostMapping("/api/transfer")
public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
    // 没有CSRF令牌验证
    if (accountService.transfer(request.getFromAccount(),
                               request.getToAccount(),
                               request.getAmount())) {
        return ResponseEntity.ok("转账成功");
    }
    return ResponseEntity.badRequest().body("转账失败");
}

@PostMapping("/api/delete-account")
public ResponseEntity<?> deleteAccount(@RequestParam Long accountId) {
    // 没有二次确认机制
    accountService.deleteAccount(accountId);
    return ResponseEntity.ok("账户已删除");
}
""",
                """
// 安全的代码 - 添加CSRF防护
@PostMapping("/api/transfer")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> transfer(@RequestBody TransferRequest request,
                                 @RequestHeader("X-CSRF-Token") String csrfToken,
                                 Authentication auth) {
    // 验证CSRF令牌
    if (!csrfTokenService.validateToken(csrfToken, auth.getName())) {
        return ResponseEntity.status(403).body("CSRF令牌验证失败");
    }

    // 验证账户所有权
    if (!accountService.isOwner(request.getFromAccount(), auth.getName())) {
        return ResponseEntity.status(403).body("无权操作他人账户");
    }

    if (accountService.transfer(request.getFromAccount(),
                               request.getToAccount(),
                               request.getAmount())) {
        return ResponseEntity.ok("转账成功");
    }
    return ResponseEntity.badRequest().body("转账失败");
}

@PostMapping("/api/delete-account")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> deleteAccount(@RequestParam Long accountId,
                                      @RequestParam String confirmationCode,
                                      Authentication auth) {
    // 要求二次确认
    if (!confirmationService.validateCode(confirmationCode, auth.getName())) {
        return ResponseEntity.badRequest().body("确认码无效");
    }

    if (!accountService.isOwner(accountId, auth.getName())) {
        return ResponseEntity.status(403).body("无权删除他人账户");
    }

    accountService.deleteAccount(accountId);
    return ResponseEntity.ok("账户已删除");
}
""",
                "## 修复建议\n\n### 1. CSRF防护\n- 实施CSRF令牌验证\n- 使用SameSite Cookie属性\n- 验证Referer头\n- 对敏感操作要求二次确认\n\n### 2. 安全头配置\n- 设置Content-Security-Policy\n- 配置X-Frame-Options防止点击劫持\n- 启用X-XSS-Protection\n- 设置Strict-Transport-Security\n\n### 3. 默认配置安全化\n- 修改所有默认密码\n- 禁用不必要的服务和功能\n- 限制文件系统访问权限\n- 隐藏敏感错误信息\n\n### 4. 定期安全审计\n- 定期检查安全配置\n- 使用自动化安全扫描工具\n- 建立配置变更审批流程\n- 实施配置基线管理",
                "[{\"title\":\"GitHub CSRF漏洞\",\"description\":\"CSRF攻击导致用户仓库被删除\"},{\"title\":\"银行系统配置错误\",\"description\":\"安全头缺失导致XSS攻击\"}]",
                "[{\"title\":\"OWASP安全配置指南\",\"url\":\"https://owasp.org/Top10/A05_2021-Security_Misconfiguration/\"},{\"title\":\"CSRF防护最佳实践\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html\"}]",
                "intermediate",
                40,
                245,
                32,
                5),
            createContent(
                categories.get("A08"),
                "软件和数据完整性失效 - 反序列化漏洞",
                "不安全的反序列化导致代码执行",
                "演示如何利用不安全的反序列化漏洞执行任意代码，获取系统控制权。",
                "# 软件和数据完整性失效详解\n\n## 什么是软件和数据完整性失效\n\n软件和数据完整性失效（Software and Data Integrity Failures）是指应用程序在验证软件更新、关键数据或CI/CD管道完整性时存在缺陷，包括不安全的反序列化、依赖注入攻击等问题。\n\n## 常见漏洞类型\n\n### 1. 不安全的反序列化\n- 反序列化不可信数据\n- 缺少输入验证\n- 使用不安全的反序列化库\n\n### 2. 依赖注入攻击\n- 恶意依赖包\n- 供应链攻击\n- 依赖版本管理不当\n\n### 3. 软件更新机制缺陷\n- 缺少数字签名验证\n- 更新过程不安全\n- 回滚机制不当\n\n### 4. CI/CD管道安全\n- 构建过程不安全\n- 部署密钥泄露\n- 自动化脚本漏洞\n\n## 攻击影响\n\n- 代码执行：执行任意系统命令\n- 系统控制：获得服务器控制权\n- 数据泄露：访问敏感信息\n- 供应链污染：影响整个软件生态",
                "通过演示反序列化攻击，展示如何利用不安全的反序列化执行任意代码。",
                """
// 存在漏洞的代码 - 不安全的反序列化
@RestController
public class VulnerableController {

    @PostMapping("/deserialize")
    public String deserialize(@RequestBody byte[] data) {
        try {
            // 直接反序列化用户输入，没有验证
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();

            // 处理反序列化的对象
            return "反序列化成功: " + obj.toString();
        } catch (Exception e) {
            return "反序列化失败: " + e.getMessage();
        }
    }

    // 可被攻击的类
    public static class VulnerableClass implements Serializable {
        private String command;

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            // 危险：直接执行命令
            Runtime.getRuntime().exec(command);
        }
    }
}
""",
                """
// 安全的代码 - 安全的反序列化
@RestController
public class SecureController {

    @PostMapping("/deserialize")
    public String deserialize(@RequestBody byte[] data) {
        try {
            // 使用白名单验证
            if (!isAllowedClass(data)) {
                return "不允许的反序列化类型";
            }

            // 使用安全的反序列化方法
            Object obj = safeDeserialize(data);

            // 验证反序列化对象的完整性
            if (!validateObject(obj)) {
                return "对象验证失败";
            }

            return "安全反序列化成功: " + obj.toString();
        } catch (Exception e) {
            return "反序列化失败: " + e.getMessage();
        }
    }

    private Object safeDeserialize(byte[] data) throws Exception {
        // 使用自定义的ObjectInputStream
        ObjectInputStream ois = new SecureObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    private boolean isAllowedClass(byte[] data) {
        // 检查数据中是否包含允许的类
        String dataStr = new String(data);
        return dataStr.contains("SafeClass") && !dataStr.contains("Runtime");
    }

    private boolean validateObject(Object obj) {
        // 验证对象的完整性和合法性
        return obj != null && obj.getClass().getName().startsWith("com.safe");
    }
}

// 安全的ObjectInputStream实现
class SecureObjectInputStream extends ObjectInputStream {
    private static final Set<String> ALLOWED_CLASSES = Set.of(
        "java.lang.String",
        "java.lang.Integer",
        "com.safe.SafeClass"
    );

    public SecureObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();

        // 只允许白名单中的类
        if (!ALLOWED_CLASSES.contains(className)) {
            throw new SecurityException("不允许反序列化类: " + className);
        }

        return super.resolveClass(desc);
    }
}
""",
                "## 修复建议\n\n### 1. 安全的反序列化\n- 避免反序列化不可信数据\n- 使用白名单验证允许的类\n- 实施输入验证和完整性检查\n- 使用安全的序列化格式（如JSON）\n\n### 2. 依赖管理安全\n- 定期更新依赖包\n- 使用依赖扫描工具\n- 验证依赖包的完整性\n- 实施供应链安全策略\n\n### 3. 软件更新安全\n- 使用数字签名验证更新包\n- 实施安全的更新机制\n- 建立回滚策略\n- 监控更新过程\n\n### 4. CI/CD管道安全\n- 保护构建密钥和凭证\n- 使用安全的构建环境\n- 实施代码签名\n- 定期审计自动化脚本",
                "[{\"title\":\"Apache Struts反序列化漏洞\",\"description\":\"CVE-2017-5638导致大规模数据泄露\"},{\"title\":\"Java反序列化攻击\",\"description\":\"利用Java反序列化执行任意代码\"}]",
                "[{\"title\":\"OWASP反序列化指南\",\"url\":\"https://owasp.org/Top10/A08_2021-Software_and_Data_Integrity_Failures/\"},{\"title\":\"Java反序列化安全\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html\"}]",
                "advanced",
                60,
                320,
                45,
                6),
            createContent(
                categories.get("A06"),
                "过时组件漏洞 - 已知CVE利用",
                "使用存在已知漏洞的组件",
                "演示如何利用过时组件中的已知漏洞进行攻击，强调组件安全更新的重要性。",
                "# 过时组件漏洞详解\n\n## 什么是过时组件漏洞\n\n过时组件漏洞（Vulnerable and Outdated Components）是指应用程序使用了存在已知安全漏洞的组件、库、框架或其他软件模块。这些组件可能包含公开的CVE（Common Vulnerabilities and Exposures）漏洞。\n\n## 常见漏洞类型\n\n### 1. 已知CVE漏洞\n- 未修复的安全漏洞\n- 公开的漏洞利用代码\n- 影响范围广泛的组件\n\n### 2. 过时版本问题\n- 缺少安全更新\n- 已停止维护的组件\n- 版本管理不当\n\n### 3. 依赖链漏洞\n- 间接依赖的安全问题\n- 传递性漏洞\n- 依赖版本冲突\n\n### 4. 组件配置错误\n- 默认配置不安全\n- 缺少安全加固\n- 权限配置过高\n\n## 攻击影响\n\n- 远程代码执行：利用组件漏洞执行任意代码\n- 权限提升：获得更高系统权限\n- 数据泄露：访问敏感信息\n- 服务拒绝：导致系统崩溃或不可用\n- 供应链攻击：影响整个软件生态",
                "通过演示已知CVE漏洞的利用，展示过时组件的安全风险。",
                """
// 存在漏洞的代码 - 使用过时的Apache Commons Collections
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

// 使用存在CVE-2015-4852漏洞的Commons Collections 3.1
public class VulnerableComponent {

    public void processData(Object input) {
        // 危险：直接使用可能被攻击者控制的输入
        Transformer transformer = new ChainedTransformer(
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod",
                new Class[]{String.class, Class[].class},
                new Object[]{"getRuntime", new Class[0]}),
            new InvokerTransformer("invoke",
                new Class[]{Object.class, Object[].class},
                new Object[]{null, new Object[0]}),
            new InvokerTransformer("exec",
                new Class[]{String.class},
                new Object[]{"calc.exe"})
        );

        // 执行可能包含恶意代码的转换
        transformer.transform(input);
    }

    // 使用过时的Spring Security版本
    @Autowired
    private PasswordEncoder passwordEncoder; // 版本3.x，存在已知漏洞

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // 使用不安全的密码编码器
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
""",
                """
// 安全的代码 - 使用最新版本和安全的组件
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 使用最新版本的Spring Security
public class SecureComponent {

    // 使用最新版本的密码编码器
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // 使用安全的密码验证
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 安全的输入处理
    public void processData(String input) {
        // 输入验证
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("输入不能为空");
        }

        // 长度限制
        if (input.length() > 1000) {
            throw new IllegalArgumentException("输入长度超过限制");
        }

        // 白名单验证
        if (!input.matches("[a-zA-Z0-9\\s]+")) {
            throw new IllegalArgumentException("输入包含非法字符");
        }

        // 安全的处理逻辑
        processSecurely(input);
    }

    private void processSecurely(String input) {
        // 安全的处理实现
        System.out.println("安全处理: " + input);
    }
}

// 组件版本管理
@Component
public class ComponentVersionChecker {

    public void checkComponentVersions() {
        // 检查关键组件版本
        checkSpringVersion();
        checkDatabaseDriverVersion();
        checkSecurityLibraryVersion();
    }

    private void checkSpringVersion() {
        String springVersion = SpringVersion.getVersion();
        if (isVulnerableVersion(springVersion)) {
            throw new SecurityException("检测到存在安全漏洞的Spring版本: " + springVersion);
        }
    }

    private boolean isVulnerableVersion(String version) {
        // 检查版本是否在已知漏洞列表中
        return VULNERABLE_VERSIONS.contains(version);
    }
}
""",
                "## 修复建议\n\n### 1. 组件版本管理\n- 定期更新所有依赖组件\n- 使用自动化依赖扫描工具\n- 建立组件安全策略\n- 监控安全公告和CVE信息\n\n### 2. 依赖安全扫描\n- 集成OWASP Dependency Check\n- 使用Snyk、SonarQube等工具\n- 建立CI/CD安全检查流程\n- 定期进行安全审计\n\n### 3. 组件选择策略\n- 选择活跃维护的组件\n- 避免使用已停止维护的库\n- 评估组件的安全记录\n- 考虑组件的安全特性\n\n### 4. 运行时保护\n- 使用WAF保护已知漏洞\n- 实施运行时应用自保护（RASP）\n- 监控异常行为\n- 建立应急响应机制",
                "[{\"title\":\"Apache Struts CVE-2017-5638\",\"description\":\"影响全球数百万网站的远程代码执行漏洞\"},{\"title\":\"Spring4Shell CVE-2022-22965\",\"description\":\"Spring Framework远程代码执行漏洞\"}]",
                "[{\"title\":\"OWASP组件安全指南\",\"url\":\"https://owasp.org/Top10/A06_2021-Vulnerable_and_Outdated_Components/\"},{\"title\":\"依赖扫描工具\",\"url\":\"https://owasp.org/www-project-dependency-check/\"}]",
                "intermediate",
                50,
                280,
                38,
                7),
            createContent(
                categories.get("A09"),
                "日志监控失效 - 安全事件检测不足",
                "缺少关键安全事件的日志记录和监控",
                "演示如何利用日志监控不足进行攻击，强调安全日志记录和实时监控的重要性。",
                "# 日志监控失效详解\n\n## 什么是日志监控失效\n\n日志监控失效（Security Logging and Monitoring Failures）是指应用程序在记录、监控和响应安全事件方面存在缺陷，导致无法及时发现和应对安全威胁。\n\n## 常见问题类型\n\n### 1. 日志记录不足\n- 缺少关键安全事件的日志\n- 日志信息不完整\n- 日志格式不统一\n- 敏感信息泄露到日志中\n\n### 2. 监控覆盖不全\n- 缺少实时监控\n- 监控指标不全面\n- 告警机制不完善\n- 监控数据不准确\n\n### 3. 日志存储问题\n- 日志存储不安全\n- 日志保留期不当\n- 日志访问控制不足\n- 日志完整性无法保证\n\n### 4. 响应机制缺陷\n- 缺少自动化响应\n- 响应时间过长\n- 响应措施不当\n- 缺少事件追踪\n\n## 攻击影响\n\n- 攻击无法被发现：恶意活动被忽略\n- 数据泄露难以追踪：无法确定泄露范围\n- 合规性问题：违反安全法规要求\n- 取证困难：缺少攻击证据\n- 业务连续性风险：无法及时应对威胁",
                "通过演示日志监控不足的场景，展示安全事件检测的重要性。",
                """
// 存在漏洞的代码 - 缺少安全日志记录
@RestController
public class VulnerableController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 危险：没有记录登录尝试
        User user = userService.findByUsername(request.getUsername());
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // 成功登录，但没有记录
            return ResponseEntity.ok("登录成功");
        }
        // 失败登录，但没有记录
        return ResponseEntity.status(401).body("登录失败");
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getUsers() {
        // 危险：没有记录敏感操作
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        // 危险：没有记录金融交易
        boolean success = accountService.transfer(request.getFromAccount(),
                                                request.getToAccount(),
                                                request.getAmount());
        if (success) {
            // 转账成功，但没有记录
            return ResponseEntity.ok("转账成功");
        }
        return ResponseEntity.badRequest().body("转账失败");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // 危险：没有记录文件上传
        String filename = file.getOriginalFilename();
        // 没有验证文件类型和大小
        fileService.saveFile(file);
        return ResponseEntity.ok("文件上传成功");
    }
}

// 缺少监控的配置
@Configuration
public class MonitoringConfig {

    // 危险：没有配置安全监控
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    // 危险：没有配置告警
    @Bean
    public AlertManager alertManager() {
        return new AlertManager();
    }
}
""",
                """
// 安全的代码 - 完善的安全日志和监控
@RestController
@Slf4j
public class SecureController {

    @Autowired
    private UserService userService;
    @Autowired
    private SecurityEventService securityEventService;
    @Autowired
    private MonitoringService monitoringService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            User user = userService.findByUsername(request.getUsername());
            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                // 记录成功登录
                securityEventService.logSuccessfulLogin(user.getId(), clientIp, userAgent);
                monitoringService.incrementLoginSuccess();

                // 检查异常登录
                if (isSuspiciousLogin(user, clientIp)) {
                    securityEventService.logSuspiciousActivity("异常登录", user.getId(), clientIp);
                    monitoringService.incrementSuspiciousActivity();
                }

                return ResponseEntity.ok("登录成功");
            } else {
                // 记录失败登录
                securityEventService.logFailedLogin(request.getUsername(), clientIp, userAgent);
                monitoringService.incrementLoginFailure();

                // 检查暴力破解
                if (isBruteForceAttempt(request.getUsername(), clientIp)) {
                    securityEventService.logSecurityThreat("暴力破解尝试", request.getUsername(), clientIp);
                    monitoringService.triggerBruteForceAlert();
                }

                return ResponseEntity.status(401).body("登录失败");
            }
        } catch (Exception e) {
            // 记录系统错误
            securityEventService.logSystemError("登录处理异常", e.getMessage(), clientIp);
            monitoringService.incrementSystemError();
            throw e;
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(Authentication auth, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // 记录敏感操作
        securityEventService.logSensitiveOperation("查看用户列表", auth.getName(), clientIp);
        monitoringService.incrementSensitiveOperation();

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request, Authentication auth, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);

        try {
            // 记录转账尝试
            securityEventService.logFinancialTransaction("转账尝试", auth.getName(),
                request.getFromAccount(), request.getToAccount(), request.getAmount(), clientIp);

            boolean success = accountService.transfer(request.getFromAccount(),
                                                    request.getToAccount(),
                                                    request.getAmount());

            if (success) {
                // 记录成功转账
                securityEventService.logFinancialTransaction("转账成功", auth.getName(),
                    request.getFromAccount(), request.getToAccount(), request.getAmount(), clientIp);
                monitoringService.incrementFinancialTransaction();

                // 检查异常交易
                if (isSuspiciousTransaction(request)) {
                    securityEventService.logSecurityThreat("异常交易", auth.getName(), clientIp);
                    monitoringService.triggerTransactionAlert();
                }

                return ResponseEntity.ok("转账成功");
            } else {
                securityEventService.logFinancialTransaction("转账失败", auth.getName(),
                    request.getFromAccount(), request.getToAccount(), request.getAmount(), clientIp);
                return ResponseEntity.badRequest().body("转账失败");
            }
        } catch (Exception e) {
            securityEventService.logSystemError("转账处理异常", e.getMessage(), clientIp);
            throw e;
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Authentication auth, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // 记录文件上传
        securityEventService.logFileOperation("文件上传", auth.getName(),
            file.getOriginalFilename(), file.getSize(), clientIp);

        // 验证文件
        if (!isValidFile(file)) {
            securityEventService.logSecurityThreat("恶意文件上传尝试", auth.getName(), clientIp);
            monitoringService.incrementMaliciousFileUpload();
            return ResponseEntity.badRequest().body("文件类型不允许");
        }

        fileService.saveFile(file);
        monitoringService.incrementFileUpload();
        return ResponseEntity.ok("文件上传成功");
    }

    private boolean isSuspiciousLogin(User user, String clientIp) {
        // 检查登录地点、时间等异常
        return !user.getLastLoginIp().equals(clientIp) ||
               isUnusualLoginTime() ||
               isNewDevice();
    }

    private boolean isBruteForceAttempt(String username, String clientIp) {
        // 检查短时间内多次失败登录
        return securityEventService.getFailedLoginCount(username, clientIp, Duration.ofMinutes(5)) > 5;
    }

    private boolean isSuspiciousTransaction(TransferRequest request) {
        // 检查异常交易模式
        return request.getAmount() > 10000 ||
               isUnusualTransferTime() ||
               isHighRiskAccount(request.getToAccount());
    }

    private boolean isValidFile(MultipartFile file) {
        // 文件类型和大小验证
        return file.getSize() < 10 * 1024 * 1024 && // 10MB限制
               Arrays.asList("jpg", "png", "pdf", "doc").contains(getFileExtension(file.getOriginalFilename()));
    }
}

// 安全监控配置
@Configuration
@EnableScheduling
public class SecurityMonitoringConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public AlertManager alertManager() {
        AlertManager manager = new AlertManager();

        // 配置安全告警规则
        manager.addRule(new BruteForceAlertRule());
        manager.addRule(new SuspiciousActivityAlertRule());
        manager.addRule(new DataExfiltrationAlertRule());
        manager.addRule(new PrivilegeEscalationAlertRule());

        return manager;
    }

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkSecurityMetrics() {
        // 检查安全指标
        if (monitoringService.getFailedLoginRate() > 0.1) {
            alertManager().triggerAlert("高失败登录率");
        }

        if (monitoringService.getSuspiciousActivityCount() > 10) {
            alertManager().triggerAlert("异常活动增加");
        }
    }
}
""",
                "## 修复建议\n\n### 1. 完善日志记录\n- 记录所有安全相关事件\n- 使用结构化日志格式\n- 保护日志数据完整性\n- 避免敏感信息泄露\n\n### 2. 实施实时监控\n- 部署SIEM系统\n- 配置安全告警规则\n- 建立事件响应流程\n- 定期审查监控效果\n\n### 3. 日志存储安全\n- 加密存储日志数据\n- 控制日志访问权限\n- 设置合适的保留期\n- 定期备份日志\n\n### 4. 自动化响应\n- 实施自动阻断机制\n- 配置安全工具集成\n- 建立事件分级处理\n- 定期演练响应流程",
                "[{\"title\":\"Equifax数据泄露\",\"description\":\"缺少日志监控导致大规模数据泄露未被及时发现\"},{\"title\":\"SolarWinds供应链攻击\",\"description\":\"缺少安全监控导致长期潜伏攻击\"}]",
                "[{\"title\":\"OWASP日志安全指南\",\"url\":\"https://owasp.org/Top10/A09_2021-Security_Logging_and_Monitoring_Failures/\"},{\"title\":\"SIEM系统部署\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html\"}]",
                "intermediate",
                45,
                260,
                35,
                8),
            createContent(
                categories.get("A10"),
                "服务端请求伪造 - SSRF攻击",
                "利用服务器发起恶意请求",
                "演示如何利用SSRF漏洞访问内网资源、进行端口扫描和绕过防火墙限制。",
                "# 服务端请求伪造详解\n\n## 什么是SSRF\n\n服务端请求伪造（Server-Side Request Forgery，SSRF）是指攻击者能够控制服务器发起请求的漏洞。攻击者可以诱导服务器向任意URL发起请求，包括内网地址、云服务元数据接口等。\n\n## 常见攻击场景\n\n### 1. 内网扫描\n- 扫描内网端口和服务\n- 发现内网应用和数据库\n- 识别内网架构\n- 寻找内网漏洞\n\n### 2. 云服务攻击\n- 访问云服务元数据接口\n- 获取云服务凭据\n- 访问云存储服务\n- 利用云服务权限\n\n### 3. 协议攻击\n- 利用不同协议（file://、gopher://、dict://）\n- 读取本地文件\n- 访问数据库\n- 绕过防火墙\n\n### 4. 应用攻击\n- 攻击内网应用\n- 利用内网服务漏洞\n- 获取敏感信息\n- 执行远程代码\n\n## 攻击影响\n\n- 内网渗透：访问内网资源和服务\n- 数据泄露：获取敏感配置信息\n- 权限提升：利用内网服务权限\n- 服务拒绝：攻击内网关键服务\n- 云服务滥用：利用云服务进行攻击",
                "通过演示SSRF攻击，展示如何利用服务器访问内网资源和云服务。",
                """
// 存在漏洞的代码 - 不安全的URL请求
@RestController
public class VulnerableController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/fetch")
    public ResponseEntity<String> fetchUrl(@RequestParam String url) {
        // 危险：直接使用用户提供的URL
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("请求失败: " + e.getMessage());
        }
    }

    @PostMapping("/proxy")
    public ResponseEntity<String> proxyRequest(@RequestBody ProxyRequest request) {
        // 危险：没有验证目标URL
        String targetUrl = request.getUrl();
        String method = request.getMethod();

        if ("GET".equals(method)) {
            ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);
            return ResponseEntity.ok(response.getBody());
        } else if ("POST".equals(method)) {
            ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, request.getBody(), String.class);
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.badRequest().body("不支持的请求方法");
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> loadImage(@RequestParam String imageUrl) {
        // 危险：没有验证图片URL
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus(@RequestParam String url) {
        // 危险：可以用于端口扫描
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok("服务可用: " + response.getStatusCode());
        } catch (Exception e) {
            return ResponseEntity.ok("服务不可用: " + e.getMessage());
        }
    }
}

// 不安全的配置
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 危险：没有配置超时和重定向限制
        return new RestTemplate();
    }
}
""",
                """
// 安全的代码 - SSRF防护
@RestController
@Slf4j
public class SecureController {

    @Autowired
    private RestTemplate secureRestTemplate;
    @Autowired
    private UrlValidator urlValidator;
    @Autowired
    private SecurityService securityService;

    @GetMapping("/fetch")
    public ResponseEntity<String> fetchUrl(@RequestParam String url, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // 记录请求日志
        log.info("URL请求: {} from {}", url, clientIp);

        // 验证URL
        if (!urlValidator.isAllowed(url)) {
            securityService.logSecurityThreat("SSRF尝试", clientIp, url);
            return ResponseEntity.badRequest().body("不允许的URL");
        }

        try {
            ResponseEntity<String> response = secureRestTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("URL请求失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("请求失败");
        }
    }

    @PostMapping("/proxy")
    public ResponseEntity<String> proxyRequest(@RequestBody ProxyRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);

        // 验证请求
        if (!isValidProxyRequest(request)) {
            securityService.logSecurityThreat("恶意代理请求", clientIp, request.getUrl());
            return ResponseEntity.badRequest().body("无效的代理请求");
        }

        String targetUrl = request.getUrl();

        // 白名单验证
        if (!urlValidator.isInWhitelist(targetUrl)) {
            securityService.logSecurityThreat("非白名单URL", clientIp, targetUrl);
            return ResponseEntity.badRequest().body("URL不在允许列表中");
        }

        try {
            ResponseEntity<String> response = secureRestTemplate.postForEntity(targetUrl, request.getBody(), String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("代理请求失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("代理请求失败");
        }
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> loadImage(@RequestParam String imageUrl, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // 验证图片URL
        if (!urlValidator.isValidImageUrl(imageUrl)) {
            securityService.logSecurityThreat("恶意图片URL", clientIp, imageUrl);
            return ResponseEntity.badRequest().build();
        }

        try {
            ResponseEntity<byte[]> response = secureRestTemplate.getForEntity(imageUrl, byte[].class);

            // 验证图片内容
            if (!isValidImage(response.getBody())) {
                securityService.logSecurityThreat("恶意图片内容", clientIp, imageUrl);
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(response.getBody());
        } catch (Exception e) {
            log.error("图片加载失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidProxyRequest(ProxyRequest request) {
        // 验证请求参数
        return request.getUrl() != null &&
               request.getMethod() != null &&
               request.getMethod().matches("GET|POST") &&
               request.getUrl().length() < 2048; // 限制URL长度
    }

    private boolean isValidImage(byte[] imageData) {
        // 验证图片格式和内容
        if (imageData == null || imageData.length == 0) {
            return false;
        }

        // 检查文件头
        if (imageData.length < 4) {
            return false;
        }

        // 检查JPEG文件头
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
            return true;
        }

        // 检查PNG文件头
        if (imageData[0] == (byte) 0x89 && imageData[1] == 0x50 &&
            imageData[2] == 0x4E && imageData[3] == 0x47) {
            return true;
        }

        return false;
    }
}

// URL验证器
@Component
public class UrlValidator {

    private final Set<String> allowedHosts;
    private final Set<String> blockedHosts;
    private final Set<String> allowedProtocols;

    public UrlValidator() {
        // 允许的主机
        allowedHosts = Set.of("api.example.com", "cdn.example.com", "images.example.com");

        // 阻止的主机
        blockedHosts = Set.of("localhost", "127.0.0.1", "0.0.0.0", "169.254.169.254", "metadata.google.internal");

        // 允许的协议
        allowedProtocols = Set.of("http", "https");
    }

    public boolean isAllowed(String url) {
        try {
            URI uri = new URI(url);

            // 检查协议
            if (!allowedProtocols.contains(uri.getScheme())) {
                return false;
            }

            // 检查主机
            String host = uri.getHost();
            if (host == null) {
                return false;
            }

            // 检查是否在阻止列表中
            if (blockedHosts.contains(host.toLowerCase())) {
                return false;
            }

            // 检查内网地址
            if (isInternalAddress(host)) {
                return false;
            }

            // 检查是否在允许列表中
            return allowedHosts.contains(host.toLowerCase());

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInWhitelist(String url) {
        return isAllowed(url);
    }

    public boolean isValidImageUrl(String url) {
        if (!isAllowed(url)) {
            return false;
        }

        // 检查文件扩展名
        return url.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp)$");
    }

    private boolean isInternalAddress(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isSiteLocalAddress() ||
                   address.isLoopbackAddress() ||
                   address.isLinkLocalAddress() ||
                   address.isAnyLocalAddress();
        } catch (Exception e) {
            return false;
        }
    }
}

// 安全的RestTemplate配置
@Configuration
public class SecureRestTemplateConfig {

    @Bean
    public RestTemplate secureRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        // 配置超时
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        // 配置重定向限制
        CloseableHttpClient httpClient = HttpClients.custom()
            .setRedirectStrategy(new DefaultRedirectStrategy() {
                @Override
                public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
                    // 限制重定向次数
                    return super.isRedirected(request, response, context) &&
                           getRedirectCount(context) < 3;
                }
            })
            .build();

        factory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);

        // 添加请求拦截器
        restTemplate.setInterceptors(List.of(new SecurityRequestInterceptor()));

        return restTemplate;
    }
}

// 安全请求拦截器
public class SecurityRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 记录请求
        log.info("发起HTTP请求: {} {}", request.getMethod(), request.getURI());

        // 验证请求头
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey("X-Forwarded-For") || headers.containsKey("X-Real-IP")) {
            throw new SecurityException("不允许的请求头");
        }

        return execution.execute(request, body);
    }
}
""",
                "## 修复建议\n\n### 1. URL验证和过滤\n- 实施URL白名单机制\n- 阻止内网地址访问\n- 验证URL格式和协议\n- 限制重定向次数\n\n### 2. 网络隔离\n- 使用网络分段\n- 限制服务器网络访问\n- 配置防火墙规则\n- 监控网络流量\n\n### 3. 输入验证\n- 严格验证用户输入\n- 使用正则表达式过滤\n- 实施长度限制\n- 检查恶意模式\n\n### 4. 安全配置\n- 配置HTTP客户端超时\n- 禁用自动重定向\n- 使用代理服务器\n- 实施请求频率限制",
                "[{\"title\":\"GitHub SSRF漏洞\",\"description\":\"利用SSRF访问内网服务获取敏感信息\"},{\"title\":\"AWS元数据服务攻击\",\"description\":\"通过SSRF访问AWS元数据服务获取凭据\"}]",
                "[{\"title\":\"OWASP SSRF防护指南\",\"url\":\"https://owasp.org/Top10/A10_2021-Server_Side_Request_Forgery/\"},{\"title\":\"SSRF攻击防护\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html\"}]",
                "advanced",
                55,
                300,
                42,
                9),
            createContent(
                categories.get("A04"),
                "不安全设计 - 业务逻辑缺陷",
                "架构和设计层面的安全缺陷",
                "演示如何利用业务逻辑缺陷进行攻击，强调安全设计在架构层面的重要性。",
                "# 不安全设计详解\n\n## 什么是不安全设计\n\n不安全设计（Insecure Design）是指应用程序在架构和设计层面存在安全缺陷，这些缺陷通常不是实现错误，而是设计决策不当导致的根本性安全问题。\n\n## 常见设计缺陷\n\n### 1. 业务逻辑缺陷\n- 缺少关键业务逻辑验证\n- 业务流程设计不当\n- 状态管理缺陷\n- 权限控制设计错误\n\n### 2. 架构安全缺陷\n- 缺少威胁建模\n- 安全控制设计不当\n- 数据流设计不安全\n- 组件间通信不安全\n\n### 3. 安全控制缺失\n- 缺少输入验证设计\n- 没有安全边界设计\n- 缺少安全监控设计\n- 没有应急响应设计\n\n### 4. 设计模式问题\n- 使用不安全的默认配置\n- 缺少安全抽象层\n- 没有安全编码标准\n- 缺少安全测试设计\n\n## 攻击影响\n\n- 业务逻辑绕过：利用设计缺陷绕过安全控制\n- 数据泄露：设计不当导致敏感信息暴露\n- 权限提升：利用架构缺陷获得更高权限\n- 服务拒绝：利用设计缺陷导致系统不可用\n- 合规性问题：设计不符合安全标准",
                "通过演示业务逻辑缺陷，展示不安全设计的安全风险。",
                "// 存在漏洞的代码 - 不安全的业务逻辑设计\n@RestController\npublic class VulnerableController {\n    \n    @Autowired\n    private OrderService orderService;\n    \n    @PostMapping(\"/order\")\n    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {\n        // 危险：直接创建订单，没有验证业务规则\n        Order order = new Order();\n        order.setUserId(request.getUserId());\n        order.setProductId(request.getProductId());\n        order.setQuantity(request.getQuantity());\n        order.setTotalPrice(request.getTotalPrice());\n        \n        orderService.save(order);\n        return ResponseEntity.ok(\"订单创建成功\");\n    }\n}",
                "// 安全的代码 - 安全的业务逻辑设计\n@RestController\n@PreAuthorize(\"hasRole('USER')\")\npublic class SecureController {\n    \n    @PostMapping(\"/order\")\n    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request, Authentication auth) {\n        // 业务逻辑验证\n        if (!orderService.validateOrderRequest(request)) {\n            return ResponseEntity.badRequest().body(\"订单请求无效\");\n        }\n        \n        // 验证库存和价格\n        if (!orderService.checkInventory(request.getProductId(), request.getQuantity())) {\n            return ResponseEntity.badRequest().body(\"库存不足\");\n        }\n        \n        Order order = orderService.createOrder(request, auth.getName());\n        return ResponseEntity.ok(\"订单创建成功\");\n    }\n}",
                "## 修复建议\n\n### 1. 安全架构设计\n- 实施威胁建模\n- 设计安全边界\n- 建立安全控制矩阵\n- 实施安全抽象层\n\n### 2. 业务逻辑安全\n- 实施业务规则验证\n- 建立状态机管理\n- 设计安全的业务流程\n- 实施业务逻辑测试\n\n### 3. 安全控制设计\n- 设计输入验证机制\n- 实施权限控制设计\n- 建立安全监控设计\n- 设计应急响应机制\n\n### 4. 安全开发流程\n- 建立安全编码标准\n- 实施安全代码审查\n- 设计安全测试策略\n- 建立安全部署流程",
                "[{\"title\":\"业务逻辑漏洞案例\",\"description\":\"利用业务逻辑缺陷绕过安全控制\"},{\"title\":\"架构安全缺陷\",\"description\":\"设计层面的安全缺陷导致系统性问题\"}]",
                "[{\"title\":\"OWASP不安全设计指南\",\"url\":\"https://owasp.org/Top10/A04_2021-Insecure_Design/\"},{\"title\":\"安全架构设计\",\"url\":\"https://cheatsheetseries.owasp.org/cheatsheets/Threat_Modeling_Cheat_Sheet.html\"}]",
                "advanced",
                60,
                320,
                48,
                10));

    contents.forEach(
        content -> {
          if (content.getCategory() == null) {
            log.warn(
                "Skipping knowledge content '{}' because category is null", content.getTitle());
            return;
          }
          Long categoryId = content.getCategory().getId();
          if (contentRepository.existsByTitleAndCategoryId(content.getTitle(), categoryId)) {
            log.debug(
                "Knowledge seed skipped existing content '{}' for category {}",
                content.getTitle(),
                categoryId);
            return;
          }
          try {
            contentRepository.save(content);
          } catch (DataIntegrityViolationException ex) {
            log.debug(
                "Knowledge content '{}' already exists, skip insert. cause={}",
                content.getTitle(),
                ex.getMessage());
          }
        });
  }

  private VulnerabilityContent createContent(
      VulnerabilityCategory category,
      String title,
      String subtitle,
      String description,
      String knowledgeContent,
      String demoDescription,
      String vulnerableCode,
      String secureCode,
      String repairSuggestions,
      String realWorldExamples,
      String referenceLinks,
      String difficulty,
      int estimatedMinutes,
      int viewCount,
      int likeCount,
      int order) {

    VulnerabilityContent content = new VulnerabilityContent();
    // 确保分类已经被持久化
    if (category.getId() == null) {
      category = categoryRepository.save(category);
    }
    content.setCategory(category);
    content.setTitle(title);
    content.setSubtitle(subtitle);
    content.setDescription(description);
    content.setKnowledgeContent(knowledgeContent);
    content.setDemoDescription(demoDescription);
    content.setVulnerableCode(vulnerableCode);
    content.setSecureCode(secureCode);
    content.setRepairSuggestions(repairSuggestions);
    content.setRealWorldExamples(realWorldExamples);
    content.setReferenceLinks(referenceLinks);
    content.setDifficultyLevel(difficulty);
    content.setEstimatedTime(estimatedMinutes);
    content.setViewCount(viewCount);
    content.setLikeCount(likeCount);
    content.setOrderNum(order);
    content.setActive(Boolean.TRUE);
    content.setCreatedAt(LocalDateTime.now());
    content.setUpdatedAt(LocalDateTime.now());
    return content;
  }
}
