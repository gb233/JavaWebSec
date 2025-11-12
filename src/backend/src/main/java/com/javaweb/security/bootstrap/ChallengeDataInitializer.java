package com.javaweb.security.bootstrap;

import com.javaweb.security.entity.ChallengeTask;
import com.javaweb.security.repository.ChallengeTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** 初始化挑战场景数据 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ChallengeDataInitializer implements CommandLineRunner {

  private final ChallengeTaskRepository challengeTaskRepository;

  @Value("${app.demo.seed-challenge-data:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) throws Exception {
    if (!seedEnabled) {
      log.info("Challenge data seeding disabled via app.demo.seed-challenge-data=false");
      return;
    }

    if (challengeTaskRepository.count() > 0) {
      return;
    }

    log.info("Seeding challenge scenarios...");

    // 1. 电商平台漏洞链
    saveChallenge(
        "ecommerce_chain_attack",
        "电商平台漏洞链",
        "登录绕过 → 权限提升 → 敏感信息泄露",
        "你是一名安全研究员，需要测试一个电商平台的安全性。该平台存在多个安全漏洞，你需要利用这些漏洞获取管理员权限并访问敏感数据。\n\n**背景故事：**\n你发现了一个名为\"SecureShop\"的电商平台，该平台声称具有完善的安全防护。但通过初步测试，你发现了一些可疑的安全问题。\n\n**任务目标：**\n1. 绕过登录验证机制\n2. 提升权限至管理员级别\n3. 获取用户敏感信息\n4. 访问管理员功能\n\n**技术要点：**\n- SQL注入绕过认证\n- 水平权限提升\n- 垂直权限提升\n- 敏感数据泄露",
        "综合",
        "expert",
        100,
        "FLAG{ecosystem_chain_attack_success}",
        "注意观察登录接口的SQL查询逻辑，可能存在注入点。",
        "1. 分析登录接口的SQL注入漏洞\n2. 利用SQL注入绕过认证\n3. 通过修改用户ID实现水平权限提升\n4. 利用业务逻辑缺陷实现垂直权限提升\n5. 访问管理员功能获取敏感信息",
        1L);

    // 2. 博客系统综合测试
    saveChallenge(
        "blog_security_test",
        "博客系统综合测试",
        "XSS → CSRF → 文件上传",
        "测试一个博客系统的综合安全性，该系统允许用户发布文章、上传文件和管理内容。\n\n**背景故事：**\n\"SecureBlog\"是一个功能丰富的博客平台，支持富文本编辑、文件上传、评论系统等功能。你需要测试这些功能的安全性。\n\n**任务目标：**\n1. 利用XSS攻击获取用户会话\n2. 通过CSRF攻击执行恶意操作\n3. 上传恶意文件获取服务器控制权\n4. 获取系统敏感信息\n\n**技术要点：**\n- 存储型XSS攻击\n- CSRF令牌绕过\n- 文件上传漏洞利用\n- 代码执行漏洞",
        "综合",
        "hard",
        90,
        "FLAG{blog_security_breach_detected}",
        "注意文件上传功能的文件类型验证机制。",
        "1. 寻找XSS注入点并构造恶意脚本\n2. 利用XSS获取管理员会话\n3. 分析CSRF防护机制并绕过\n4. 上传恶意文件并执行\n5. 获取系统控制权",
        1L);

    // 3. 管理后台渗透
    saveChallenge(
        "admin_panel_penetration",
        "管理后台渗透",
        "SQL注入 → 反序列化 → 命令执行",
        "渗透测试一个企业级管理后台系统，该系统使用Java开发，具有复杂的业务逻辑。\n\n**背景故事：**\n\"EnterpriseAdmin\"是一个大型企业的内部管理系统，用于管理员工信息、财务数据、系统配置等。你需要找到进入系统的途径。\n\n**任务目标：**\n1. 通过SQL注入获取数据库访问权限\n2. 利用反序列化漏洞执行代码\n3. 获取服务器命令执行权限\n4. 访问敏感业务数据\n\n**技术要点：**\n- 盲注SQL注入技术\n- Java反序列化漏洞利用\n- 命令注入攻击\n- 权限提升技术",
        "综合",
        "expert",
        100,
        "FLAG{enterprise_admin_compromised}",
        "注意观察系统的序列化数据格式和反序列化处理逻辑。",
        "1. 分析SQL注入点并构造盲注payload\n2. 获取数据库敏感信息\n3. 寻找反序列化入口点\n4. 构造恶意序列化对象\n5. 实现远程代码执行",
        1L);

    // 4. API安全测试
    saveChallenge(
        "api_security_test",
        "API安全测试",
        "JWT漏洞 → 业务逻辑缺陷 → 数据泄露",
        "测试一个RESTful API系统的安全性，该系统使用JWT进行身份认证。\n\n**背景故事：**\n\"SecureAPI\"是一个提供数据服务的API系统，支持多种数据格式和认证方式。你需要测试其安全防护机制。\n\n**任务目标：**\n1. 利用JWT漏洞绕过认证\n2. 发现业务逻辑缺陷\n3. 获取敏感数据访问权限\n4. 实现数据泄露\n\n**技术要点：**\n- JWT算法混淆攻击\n- 业务逻辑漏洞利用\n- API参数污染\n- 数据访问控制绕过",
        "综合",
        "hard",
        85,
        "FLAG{api_security_breach_success}",
        "仔细分析JWT令牌的签名算法和密钥强度。",
        "1. 分析JWT令牌结构和签名算法\n2. 利用算法混淆攻击伪造令牌\n3. 发现业务逻辑缺陷\n4. 绕过数据访问控制\n5. 获取敏感数据",
        1L);

    // 5. 文件管理系统
    saveChallenge(
        "file_management_system",
        "文件管理系统",
        "路径穿越 → 文件上传 → 任意文件读取",
        "测试一个企业文件管理系统的安全性，该系统支持文件上传、下载、预览等功能。\n\n**背景故事：**\n\"SecureFileManager\"是一个企业内部文件管理系统，用于存储和管理各种文档。你需要测试其文件处理功能的安全性。\n\n**任务目标：**\n1. 利用路径穿越漏洞访问系统文件\n2. 上传恶意文件获取控制权\n3. 实现任意文件读取\n4. 获取系统敏感信息\n\n**技术要点：**\n- 目录遍历攻击\n- 文件上传漏洞利用\n- 本地文件包含\n- 系统文件访问",
        "综合",
        "hard",
        80,
        "FLAG{file_manager_security_breach}",
        "注意文件路径的处理逻辑和过滤机制。",
        "1. 分析文件路径处理逻辑\n2. 构造路径穿越payload\n3. 上传恶意文件并执行\n4. 利用文件包含漏洞\n5. 读取系统敏感文件",
        1L);

    // 6. 社交平台安全
    saveChallenge(
        "social_platform_security",
        "社交平台安全",
        "XSS → SSRF → 信息收集",
        "测试一个社交网络平台的安全性，该平台具有用户互动、内容分享、好友系统等功能。\n\n**背景故事：**\n\"SecureSocial\"是一个新兴的社交网络平台，用户可以在上面分享动态、添加好友、创建群组等。你需要测试其安全防护。\n\n**任务目标：**\n1. 利用XSS攻击获取用户信息\n2. 通过SSRF攻击访问内网服务\n3. 收集系统敏感信息\n4. 实现用户数据泄露\n\n**技术要点：**\n- 反射型XSS攻击\n- SSRF漏洞利用\n- 内网信息收集\n- 用户数据枚举",
        "综合",
        "hard",
        85,
        "FLAG{social_platform_compromised}",
        "注意观察用户输入的处理和URL请求的验证机制。",
        "1. 寻找XSS注入点并构造攻击\n2. 利用XSS获取用户会话\n3. 分析SSRF漏洞点\n4. 通过SSRF访问内网服务\n5. 收集敏感信息",
        1L);

    // 7. 支付系统测试
    saveChallenge(
        "payment_system_test",
        "支付系统测试",
        "逻辑漏洞 → 条件竞争 → 金额篡改",
        "测试一个在线支付系统的安全性，该系统处理用户的支付请求和资金转移。\n\n**背景故事：**\n\"SecurePay\"是一个在线支付平台，支持多种支付方式和货币。你需要测试其支付逻辑的安全性。\n\n**任务目标：**\n1. 发现支付逻辑漏洞\n2. 利用条件竞争攻击\n3. 篡改支付金额\n4. 获取资金控制权\n\n**技术要点：**\n- 业务逻辑漏洞分析\n- 条件竞争攻击\n- 金额篡改技术\n- 支付流程绕过",
        "综合",
        "expert",
        95,
        "FLAG{payment_system_breach}",
        "仔细分析支付流程的并发处理机制。",
        "1. 分析支付业务逻辑\n2. 发现逻辑漏洞点\n3. 构造条件竞争攻击\n4. 篡改支付参数\n5. 实现资金控制",
        1L);

    // 8. 内容管理系统
    saveChallenge(
        "cms_security_test",
        "内容管理系统",
        "XXE → 反序列化 → 远程代码执行",
        "测试一个企业级内容管理系统的安全性，该系统支持多种内容格式和数据处理。\n\n**背景故事：**\n\"SecureCMS\"是一个功能强大的内容管理系统，支持XML处理、数据序列化、模板渲染等功能。你需要测试其安全性。\n\n**任务目标：**\n1. 利用XXE漏洞读取系统文件\n2. 通过反序列化漏洞执行代码\n3. 实现远程代码执行\n4. 获取系统控制权\n\n**技术要点：**\n- XXE漏洞利用\n- 反序列化攻击\n- 远程代码执行\n- 系统权限提升",
        "综合",
        "expert",
        100,
        "FLAG{cms_security_compromised}",
        "注意XML处理配置和反序列化安全机制。",
        "1. 分析XML处理功能\n2. 构造XXE攻击payload\n3. 读取系统敏感文件\n4. 寻找反序列化入口\n5. 实现远程代码执行",
        1L);

    // 9. 在线教育平台
    saveChallenge(
        "online_education_platform",
        "在线教育平台",
        "访问控制 → 业务逻辑 → 数据篡改",
        "测试一个在线教育平台的安全性，该平台管理学生信息、课程内容、成绩数据等。\n\n**背景故事：**\n\"SecureEdu\"是一个在线教育平台，学生可以选课、提交作业、查看成绩等。你需要测试其安全防护机制。\n\n**任务目标：**\n1. 绕过访问控制获取敏感数据\n2. 利用业务逻辑缺陷篡改数据\n3. 获取管理员权限\n4. 修改系统数据\n\n**技术要点：**\n- 访问控制绕过\n- 业务逻辑漏洞利用\n- 数据篡改技术\n- 权限提升攻击",
        "综合",
        "hard",
        85,
        "FLAG{education_platform_breach}",
        "注意观察用户权限验证和数据访问控制机制。",
        "1. 分析访问控制机制\n2. 寻找权限绕过方法\n3. 发现业务逻辑缺陷\n4. 利用漏洞篡改数据\n5. 获取管理员权限",
        1L);

    // 10. 企业应用综合
    saveChallenge(
        "enterprise_app_comprehensive",
        "企业应用综合",
        "LDAP注入 → 配置错误 → 权限提升",
        "测试一个企业级应用系统的综合安全性，该系统集成了多种企业服务。\n\n**背景故事：**\n\"EnterpriseApp\"是一个大型企业应用系统，集成了LDAP认证、数据库服务、文件系统等功能。你需要全面测试其安全性。\n\n**任务目标：**\n1. 利用LDAP注入获取用户信息\n2. 发现配置错误漏洞\n3. 实现权限提升\n4. 获取系统完全控制权\n\n**技术要点：**\n- LDAP注入攻击\n- 配置错误利用\n- 权限提升技术\n- 系统渗透测试",
        "综合",
        "expert",
        100,
        "FLAG{enterprise_app_fully_compromised}",
        "注意LDAP查询语法和系统配置细节。",
        "1. 分析LDAP认证机制\n2. 构造LDAP注入攻击\n3. 发现配置错误\n4. 利用漏洞提升权限\n5. 获取系统完全控制权",
        1L);

    log.info(
        "Challenge scenarios seed finished, total challenges={}", challengeTaskRepository.count());
  }

  private void saveChallenge(
      String challengeName,
      String title,
      String description,
      String solution,
      String categoryName,
      String difficultyLevel,
      Integer points,
      String flag,
      String hint,
      String detailedSolution,
      Long createdBy) {

    ChallengeTask challenge =
        new ChallengeTask()
            .setChallengeName(challengeName)
            .setTitle(title)
            .setDescription(description)
            .setCategoryCode("综合")
            .setCategoryName(categoryName)
            .setDifficultyLevel(difficultyLevel)
            .setPoints(points)
            .setFlag(flag)
            .setHint(hint)
            .setSolution(detailedSolution)
            .setIsActive(true)
            .setCreatedBy(createdBy);

    challengeTaskRepository.save(challenge);
  }
}
