package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 收藏访问记录实体类 */
@Entity
@Table(name = "collection_access_logs")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CollectionAccessLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "collection_id", nullable = false)
  private Long collectionId;

  @Column(name = "user_id")
  private Long userId; // NULL表示匿名访问

  @Column(name = "access_type", nullable = false, length = 20)
  private String accessType = "view"; // view, share, export

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", columnDefinition = "TEXT")
  private String userAgent;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  // 多对一关系：访问记录属于一个收藏夹
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection_id", insertable = false, updatable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Collection collection;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
