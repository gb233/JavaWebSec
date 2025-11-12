package com.javaweb.security.service;

import com.javaweb.security.dto.knowledge.VulnerabilityCategoryDto;
import com.javaweb.security.dto.knowledge.VulnerabilityDetailDto;
import com.javaweb.security.dto.knowledge.VulnerabilitySummaryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KnowledgeService {

  List<VulnerabilityCategoryDto> listCategories();

  Page<VulnerabilitySummaryDto> listVulnerabilities(
      String categoryCode, String keyword, Pageable pageable);

  VulnerabilityDetailDto getVulnerability(Long id);
}
