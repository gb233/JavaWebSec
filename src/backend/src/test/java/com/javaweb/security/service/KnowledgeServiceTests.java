package com.javaweb.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaweb.security.dto.knowledge.VulnerabilityCategoryDto;
import com.javaweb.security.dto.knowledge.VulnerabilityDetailDto;
import com.javaweb.security.dto.knowledge.VulnerabilitySummaryDto;
import com.javaweb.security.entity.VulnerabilityCategory;
import com.javaweb.security.entity.VulnerabilityContent;
import com.javaweb.security.repository.VulnerabilityCategoryRepository;
import com.javaweb.security.repository.VulnerabilityContentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class KnowledgeServiceTests {

  @Autowired private KnowledgeService knowledgeService;

  @Autowired private VulnerabilityCategoryRepository categoryRepository;

  @Autowired private VulnerabilityContentRepository contentRepository;

  private Long contentId;

  @BeforeEach
  void setUp() {
    contentRepository.deleteAll();
    categoryRepository.deleteAll();

    VulnerabilityCategory category = new VulnerabilityCategory();
    category.setCategoryCode("A03");
    category.setCategoryName("注入漏洞");
    category.setCategoryDescription("SQL 注入学习示例");
    category.setSeverityLevel("high");
    category.setOwaspYear(2021);
    category.setOrderNum(3);
    category.setActive(Boolean.TRUE);
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedAt(LocalDateTime.now());

    VulnerabilityCategory savedCategory = categoryRepository.save(category);

    VulnerabilityContent content = new VulnerabilityContent();
    content.setCategory(savedCategory);
    content.setTitle("SQL 注入从入门到防御");
    content.setSubtitle("利用拼接 SQL 绕过登录验证");
    content.setDescription("提供经典注入 payload 与参数化查询修复方案。");
    content.setKnowledgeContent("## 学习目标\n- 熟悉 Union/布尔盲注\n- 掌握参数化查询与输入校验");
    content.setDemoDescription("通过在线靶场执行联合注入并提取敏感数据。");
    content.setVulnerableCode(
        """
String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);
""");
    content.setSecureCode(
        """
PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
stmt.setString(1, username);
stmt.setString(2, password);
ResultSet rs = stmt.executeQuery();
""");
    content.setRepairSuggestions("- 使用参数化查询\n- 对输入进行白名单校验");
    content.setDifficultyLevel("beginner");
    content.setEstimatedTime(40);
    content.setViewCount(120);
    content.setLikeCount(12);
    content.setOrderNum(1);
    content.setActive(Boolean.TRUE);
    content.setCreatedAt(LocalDateTime.now());
    content.setUpdatedAt(LocalDateTime.now());

    contentId = contentRepository.save(content).getId();
  }

  @Test
  @DisplayName("知识中心分类列表返回非空结果")
  void shouldListCategories() {
    List<VulnerabilityCategoryDto> categories = knowledgeService.listCategories();
    assertThat(categories).hasSize(1);
    assertThat(categories.get(0).getCode()).isEqualTo("A03");
  }

  @Test
  @DisplayName("知识中心支持分页查询漏洞内容")
  void shouldListVulnerabilitiesWithPagination() {
    Page<VulnerabilitySummaryDto> page =
        knowledgeService.listVulnerabilities("A03", null, PageRequest.of(0, 10));

    assertThat(page.getContent()).hasSize(1);
    VulnerabilitySummaryDto summary = page.getContent().get(0);
    assertThat(summary.getTitle()).contains("SQL 注入");
    assertThat(summary.getCategoryCode()).isEqualTo("A03");
  }

  @Test
  @DisplayName("知识中心可以获取漏洞详情")
  void shouldFetchVulnerabilityDetail() {
    VulnerabilityDetailDto detail = knowledgeService.getVulnerability(contentId);

    assertThat(detail.getId()).isEqualTo(contentId);
    assertThat(detail.getTitle()).contains("SQL 注入");
    assertThat(detail.getKnowledgeContent()).contains("学习目标");
  }
}
