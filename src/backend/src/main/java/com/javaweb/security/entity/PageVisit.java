package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 页面访问记录实体类
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Entity
@Table(name = "page_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PageVisit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "vulnerability_code", nullable = false)
  private String vulnerabilityCode;

  @Column(name = "page_type", nullable = false)
  private String pageType;

  @Column(name = "visit_time")
  private LocalDateTime visitTime;

  @Column(name = "duration")
  private Integer duration = 0;

  @Column(name = "scroll_depth")
  private Integer scrollDepth = 0;

  @Column(name = "click_count")
  private Integer clickCount = 0;
}
