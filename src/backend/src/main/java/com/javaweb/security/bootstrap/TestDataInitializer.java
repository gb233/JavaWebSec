package com.javaweb.security.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.entity.TestQuestion;
import com.javaweb.security.repository.TestQuestionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** 初始化知识测试题库示例数据 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

  private final TestQuestionRepository questionRepository;
  private final ObjectMapper objectMapper;

  @Value("${app.demo.seed-test-data:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) throws Exception {
    if (!seedEnabled) {
      log.info("Test data seeding disabled via app.demo.seed-test-data=false");
      return;
    }

    if (questionRepository.count() > 0) {
      return;
    }

    log.info("Seeding initial test questions...");

    saveQuestion(
        "A03",
        "注入漏洞",
        "以下关于 SQL 注入防护的描述，哪一项是正确的？",
        "single_choice",
        List.of("允许用户输入后直接拼接到 SQL 语句中", "使用参数化查询并对输入进行校验", "仅在前端做输入校验即可", "通过隐藏表名即可避免 SQL 注入"),
        "使用参数化查询并对输入进行校验",
        "参数化查询能够避免用户输入作为 SQL 语句的一部分被执行。",
        "easy",
        5,
        90);

    saveQuestion(
        "A01",
        "访问控制失效",
        "为防止水平越权问题，服务端应当采取哪种策略？",
        "single_choice",
        List.of("仅依赖前端隐藏敏感功能入口", "在服务端根据资源所有者进行权限校验", "只要 JWT 存在即可访问所有资源", "将资源 ID 进行加密即可安全"),
        "在服务端根据资源所有者进行权限校验",
        "服务端需要验证请求用户是否具有访问资源的权限。",
        "medium",
        8,
        120);

    saveQuestion(
        "A02",
        "加密失败",
        "应用需要在客户端与服务端之间安全传输账号密码，正确的做法是：",
        "single_choice",
        List.of("使用 HTTP 协议便于调试", "使用 HTTPS 并启用 HSTS", "仅对密码进行 Base64 编码", "将密码拼接到 URL 参数中"),
        "使用 HTTPS 并启用 HSTS",
        "HTTPS 能够提供加密通道并防止降级攻击。",
        "easy",
        5,
        90);

    saveQuestion(
        "A03",
        "注入漏洞",
        "关于预编译语句（PreparedStatement）的优点，以下说法正确的是：",
        "single_choice",
        List.of("编译后的 SQL 可以重复利用，减少解析次数", "必须在每次执行前重新编译", "只适用于 INSERT 语句", "可以自动生成业务逻辑"),
        "编译后的 SQL 可以重复利用，减少解析次数",
        "预编译语句能避免重复解析并有效防止 SQL 注入。",
        "medium",
        8,
        120);

    saveQuestion(
        "A07",
        "身份验证失败",
        "以下哪种做法可以降低暴力破解密码的风险？",
        "single_choice",
        List.of("允许无限次尝试", "在登录失败次数过多时锁定账户", "在数据库中以明文存储密码", "使用简单的 4 位数字密码"),
        "在登录失败次数过多时锁定账户",
        "锁定策略可以有效降低暴力破解的成功率。",
        "medium",
        6,
        90);

    saveQuestion(
        "A08",
        "软件和数据完整性",
        "确保应用依赖的第三方组件安全，以下做法正确的是：",
        "single_choice",
        List.of("忽略组件的安全公告", "固定版本并定期关注安全更新", "关闭自动化构建", "将所有组件上传到公共仓库"),
        "固定版本并定期关注安全更新",
        "对第三方组件进行版本管理并及时更新补丁。",
        "medium",
        7,
        90);

    // A01 访问控制失效 - 更多题目
    saveQuestion(
        "A01",
        "访问控制失效",
        "在RESTful API设计中，防止垂直权限提升的最佳实践是：",
        "single_choice",
        List.of("仅在前端隐藏管理员功能", "在服务端验证用户角色和权限", "使用复杂的URL路径", "依赖客户端传递的角色信息"),
        "在服务端验证用户角色和权限",
        "服务端必须独立验证用户权限，不能依赖客户端信息。",
        "hard",
        10,
        120);

    saveQuestion(
        "A01",
        "访问控制失效",
        "以下哪种情况最容易导致IDOR（不安全的直接对象引用）漏洞？",
        "single_choice",
        List.of("使用随机生成的资源ID", "在URL中直接暴露数据库主键", "对资源ID进行加密", "使用UUID作为资源标识符"),
        "在URL中直接暴露数据库主键",
        "直接暴露数据库主键容易被攻击者枚举和猜测。",
        "medium",
        8,
        90);

    // A02 加密失败 - 更多题目
    saveQuestion(
        "A02",
        "加密失败",
        "对于存储用户密码，以下哪种方式最安全？",
        "single_choice",
        List.of("使用MD5哈希", "使用SHA-1哈希", "使用bcrypt等自适应哈希算法", "使用Base64编码"),
        "使用bcrypt等自适应哈希算法",
        "bcrypt等自适应哈希算法能够抵御暴力破解和彩虹表攻击。",
        "medium",
        8,
        90);

    saveQuestion(
        "A02",
        "加密失败",
        "在HTTPS配置中，以下哪个做法是错误的？",
        "single_choice",
        List.of("使用TLS 1.2或更高版本", "启用HSTS头", "使用弱加密套件以兼容老浏览器", "配置安全的SSL证书"),
        "使用弱加密套件以兼容老浏览器",
        "弱加密套件容易被破解，应该使用强加密套件。",
        "easy",
        5,
        60);

    // A03 注入漏洞 - 更多题目
    saveQuestion(
        "A03",
        "注入漏洞",
        "以下哪种输入验证方式对防止XSS攻击最有效？",
        "single_choice",
        List.of("仅在前端验证", "使用白名单验证", "使用黑名单过滤", "依赖框架自动防护"),
        "使用白名单验证",
        "白名单验证只允许已知安全的输入，比黑名单更安全。",
        "medium",
        8,
        90);

    saveQuestion(
        "A03",
        "注入漏洞",
        "防止NoSQL注入攻击的最佳方法是：",
        "single_choice",
        List.of("使用字符串拼接构建查询", "使用参数化查询或ORM", "仅在前端验证输入", "使用正则表达式过滤"),
        "使用参数化查询或ORM",
        "参数化查询能够防止用户输入被解释为查询语法。",
        "hard",
        10,
        120);

    // A04 不安全设计 - 新题目
    saveQuestion(
        "A04",
        "不安全设计",
        "在业务逻辑设计中，以下哪种做法最容易导致安全漏洞？",
        "single_choice",
        List.of("实施完整的业务规则验证", "使用状态机管理业务流程", "将安全控制完全依赖前端", "建立完整的审计日志"),
        "将安全控制完全依赖前端",
        "前端验证可以被绕过，安全控制必须在服务端实施。",
        "medium",
        8,
        90);

    saveQuestion(
        "A04",
        "不安全设计",
        "威胁建模的主要目的是：",
        "single_choice",
        List.of("提高系统性能", "识别和缓解安全风险", "减少开发成本", "简化系统架构"),
        "识别和缓解安全风险",
        "威胁建模帮助在设计和开发阶段识别潜在的安全威胁。",
        "hard",
        10,
        120);

    // A05 安全配置错误 - 新题目
    saveQuestion(
        "A05",
        "安全配置错误",
        "以下哪种HTTP头配置有助于防止XSS攻击？",
        "single_choice",
        List.of("X-Frame-Options", "Content-Security-Policy", "X-Content-Type-Options", "以上都是"),
        "以上都是",
        "这些HTTP安全头都能提供不同层面的安全防护。",
        "medium",
        8,
        90);

    saveQuestion(
        "A05",
        "安全配置错误",
        "在生产环境中，以下哪种做法是错误的？",
        "single_choice",
        List.of("关闭调试模式", "使用默认密码", "启用详细错误信息", "以上都是错误的"),
        "以上都是错误的",
        "生产环境应该关闭调试模式、使用强密码、隐藏错误信息。",
        "easy",
        5,
        60);

    // A06 过时组件 - 新题目
    saveQuestion(
        "A06",
        "过时组件",
        "以下哪种方法最适合检测依赖组件的安全漏洞？",
        "single_choice",
        List.of("手动检查每个组件", "使用自动化依赖扫描工具", "仅关注主要组件", "忽略安全公告"),
        "使用自动化依赖扫描工具",
        "自动化工具能够持续监控和检测依赖组件的安全漏洞。",
        "medium",
        8,
        90);

    saveQuestion(
        "A06",
        "过时组件",
        "CVE（Common Vulnerabilities and Exposures）的主要作用是：",
        "single_choice",
        List.of("提供统一的漏洞标识", "自动修复漏洞", "防止漏洞产生", "隐藏漏洞信息"),
        "提供统一的漏洞标识",
        "CVE为安全漏洞提供标准化的标识符，便于跟踪和管理。",
        "easy",
        5,
        60);

    // A07 身份验证失败 - 更多题目
    saveQuestion(
        "A07",
        "身份验证失败",
        "多因素认证（MFA）的主要优势是：",
        "single_choice",
        List.of("提高系统性能", "增强身份验证安全性", "简化用户登录", "减少服务器负载"),
        "增强身份验证安全性",
        "MFA通过多种验证方式提高身份验证的安全性。",
        "medium",
        8,
        90);

    saveQuestion(
        "A07",
        "身份验证失败",
        "会话固定攻击的防护方法是：",
        "single_choice",
        List.of("使用固定会话ID", "在用户认证后重新生成会话ID", "延长会话时间", "在URL中传递会话信息"),
        "在用户认证后重新生成会话ID",
        "重新生成会话ID可以防止会话固定攻击。",
        "hard",
        10,
        120);

    // A08 软件和数据完整性失效 - 更多题目
    saveQuestion(
        "A08",
        "软件和数据完整性失效",
        "防止反序列化攻击的最佳方法是：",
        "single_choice",
        List.of("允许反序列化任意对象", "使用白名单限制可反序列化的类", "禁用所有序列化功能", "仅在前端验证数据"),
        "使用白名单限制可反序列化的类",
        "白名单机制只允许安全的类进行反序列化。",
        "hard",
        10,
        120);

    saveQuestion(
        "A08",
        "软件和数据完整性失效",
        "供应链攻击的主要风险是：",
        "single_choice",
        List.of("影响系统性能", "通过受信任的组件传播恶意代码", "增加开发成本", "降低用户体验"),
        "通过受信任的组件传播恶意代码",
        "供应链攻击利用受信任的组件传播恶意代码，影响范围广泛。",
        "medium",
        8,
        90);

    // A09 日志监控失效 - 新题目
    saveQuestion(
        "A09",
        "日志监控失效",
        "安全日志记录的最佳实践不包括：",
        "single_choice",
        List.of("记录所有安全相关事件", "使用结构化日志格式", "在日志中记录敏感信息", "保护日志数据完整性"),
        "在日志中记录敏感信息",
        "日志中不应记录敏感信息，以免造成数据泄露。",
        "medium",
        8,
        90);

    saveQuestion(
        "A09",
        "日志监控失效",
        "SIEM系统的主要功能是：",
        "single_choice",
        List.of("提高系统性能", "集中管理和分析安全事件", "自动修复漏洞", "减少存储空间"),
        "集中管理和分析安全事件",
        "SIEM系统能够集中收集、分析和关联安全事件。",
        "hard",
        10,
        120);

    // A10 SSRF - 新题目
    saveQuestion(
        "A10",
        "服务端请求伪造",
        "防止SSRF攻击的最有效方法是：",
        "single_choice",
        List.of("允许访问任意URL", "使用URL白名单", "仅在前端验证URL", "禁用所有网络请求"),
        "使用URL白名单",
        "URL白名单只允许访问预定义的安全地址。",
        "medium",
        8,
        90);

    saveQuestion(
        "A10",
        "服务端请求伪造",
        "SSRF攻击可能导致的后果不包括：",
        "single_choice",
        List.of("内网扫描", "访问云服务元数据", "提高系统性能", "绕过防火墙限制"),
        "提高系统性能",
        "SSRF攻击不会提高系统性能，反而可能造成安全风险。",
        "easy",
        5,
        60);

    log.info("Test question seed finished, total questions={}", questionRepository.count());
  }

  private void saveQuestion(
      String categoryCode,
      String categoryName,
      String questionText,
      String questionType,
      List<String> options,
      String correctAnswer,
      String explanation,
      String difficultyLevel,
      int points,
      int timeLimit)
      throws Exception {
    String optionsJson = objectMapper.writeValueAsString(options);
    TestQuestion question =
        new TestQuestion()
            .setCategoryCode(categoryCode)
            .setCategoryName(categoryName)
            .setQuestionText(questionText)
            .setQuestionType(questionType)
            .setOptions(optionsJson)
            .setCorrectAnswer(correctAnswer)
            .setExplanation(explanation)
            .setDifficultyLevel(difficultyLevel)
            .setPoints(points)
            .setTimeLimit(timeLimit)
            .setIsActive(true)
            .setCreatedBy(1L);

    questionRepository.save(question);
  }
}
