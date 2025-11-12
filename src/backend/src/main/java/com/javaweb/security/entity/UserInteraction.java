package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 用户交互记录实体类
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Entity
@Table(name = "user_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class UserInteraction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "vulnerability_code", nullable = false)
  private String vulnerabilityCode;

  @Column(name = "interaction_type", nullable = false)
  private String interactionType;

  @Column(name = "interaction_detail", columnDefinition = "JSON")
  private String interactionDetail;

  @Column(name = "timestamp")
  private LocalDateTime timestamp;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
