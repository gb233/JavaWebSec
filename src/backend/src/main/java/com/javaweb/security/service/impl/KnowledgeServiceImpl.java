package com.javaweb.security.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.dto.knowledge.VulnerabilityCategoryDto;
import com.javaweb.security.dto.knowledge.VulnerabilityDetailDto;
import com.javaweb.security.dto.knowledge.VulnerabilitySummaryDto;
import com.javaweb.security.entity.VulnerabilityCategory;
import com.javaweb.security.entity.VulnerabilityContent;
import com.javaweb.security.repository.VulnerabilityCategoryRepository;
import com.javaweb.security.repository.VulnerabilityContentRepository;
import com.javaweb.security.service.KnowledgeService;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

  private final VulnerabilityCategoryRepository categoryRepository;
  private final VulnerabilityContentRepository contentRepository;
  private final ObjectMapper objectMapper;

  @Override
  public List<VulnerabilityCategoryDto> listCategories() {
    List<VulnerabilityCategory> categories =
        categoryRepository.findByActiveTrueOrderByOrderNumAsc();
    return categories.stream().map(this::mapCategory).collect(Collectors.toList());
  }

  @Override
  public Page<VulnerabilitySummaryDto> listVulnerabilities(
      String categoryCode, String keyword, Pageable pageable) {

    Specification<VulnerabilityContent> spec = Specification.where(isActive());
    if (StringUtils.hasText(categoryCode)) {
      spec = spec.and(hasCategoryCode(categoryCode));
    }
    if (StringUtils.hasText(keyword)) {
      spec = spec.and(withKeyword(keyword));
    }

    Page<VulnerabilityContent> page = contentRepository.findAll(spec, pageable);
    List<VulnerabilitySummaryDto> content =
        page.getContent().stream().map(this::mapSummary).collect(Collectors.toList());
    return new PageImpl<>(content, pageable, page.getTotalElements());
  }

  @Override
  public VulnerabilityDetailDto getVulnerability(Long id) {
    VulnerabilityContent content =
        contentRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("漏洞内容不存在: " + id));
    return mapDetail(content);
  }

  private VulnerabilityCategoryDto mapCategory(VulnerabilityCategory category) {
    return VulnerabilityCategoryDto.builder()
        .id(category.getId())
        .code(category.getCategoryCode())
        .name(category.getCategoryName())
        .description(category.getCategoryDescription())
        .severity(category.getSeverityLevel())
        .iconUrl(category.getIconUrl())
        .colorTheme(category.getColorTheme())
        .owaspYear(category.getOwaspYear())
        .orderNum(category.getOrderNum())
        .build();
  }

  private VulnerabilitySummaryDto mapSummary(VulnerabilityContent content) {
    VulnerabilityCategory category = content.getCategory();
    return VulnerabilitySummaryDto.builder()
        .id(content.getId())
        .categoryCode(category != null ? category.getCategoryCode() : null)
        .categoryName(category != null ? category.getCategoryName() : null)
        .title(content.getTitle())
        .subtitle(content.getSubtitle())
        .description(Optional.ofNullable(content.getDescription()).orElse(""))
        .difficultyLevel(content.getDifficultyLevel())
        .severityLevel(category != null ? category.getSeverityLevel() : null)
        .estimatedTime(content.getEstimatedTime())
        .viewCount(content.getViewCount())
        .slug(generateSlug(content.getTitle()))
        .build();
  }

  private VulnerabilityDetailDto mapDetail(VulnerabilityContent content) {
    VulnerabilityCategory category = content.getCategory();
    return VulnerabilityDetailDto.builder()
        .id(content.getId())
        .slug(generateSlug(content.getTitle()))
        .title(content.getTitle())
        .subtitle(content.getSubtitle())
        .description(content.getDescription())
        .categoryCode(category != null ? category.getCategoryCode() : null)
        .categoryName(category != null ? category.getCategoryName() : null)
        .severityLevel(category != null ? category.getSeverityLevel() : null)
        .difficultyLevel(content.getDifficultyLevel())
        .estimatedTime(content.getEstimatedTime())
        .viewCount(content.getViewCount())
        .likeCount(content.getLikeCount())
        .knowledgeContent(content.getKnowledgeContent())
        .demoDescription(content.getDemoDescription())
        .vulnerableCode(content.getVulnerableCode())
        .secureCode(content.getSecureCode())
        .repairSuggestions(content.getRepairSuggestions())
        .realWorldExamples(parseJsonList(content.getRealWorldExamples()))
        .references(parseJsonList(content.getReferenceLinks()))
        .build();
  }

  private List<java.util.Map<String, Object>> parseJsonList(String json) {
    if (!StringUtils.hasText(json)) {
      return Collections.emptyList();
    }
    try {
      List<java.util.Map<String, Object>> value =
          objectMapper.readValue(json, new TypeReference<List<java.util.Map<String, Object>>>() {});
      return CollectionUtils.isEmpty(value) ? Collections.emptyList() : value;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  private Specification<VulnerabilityContent> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("active"));
  }

  private Specification<VulnerabilityContent> hasCategoryCode(String code) {
    return (root, query, cb) ->
        cb.equal(cb.lower(root.get("category").get("categoryCode")), code.toLowerCase(Locale.ROOT));
  }

  private Specification<VulnerabilityContent> withKeyword(String keyword) {
    return (root, query, cb) -> {
      String pattern = "%" + keyword.trim().toLowerCase(java.util.Locale.ROOT) + "%";
      return cb.or(
          cb.like(cb.lower(root.get("title")), pattern),
          cb.like(cb.lower(root.get("description")), pattern));
    };
  }

  private String generateSlug(String title) {
    String base = Optional.ofNullable(title).orElse("vulnerability");
    return base.toLowerCase(java.util.Locale.ROOT)
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", "");
  }
}
