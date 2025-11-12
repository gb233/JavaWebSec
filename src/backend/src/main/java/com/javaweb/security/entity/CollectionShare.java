package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 收藏分享实体类 */
@Entity
@Table(name = "collection_shares")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CollectionShare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "collection_id", nullable = false)
  private Long collectionId;

  @Column(name = "share_code", nullable = false, unique = true, length = 32)
  private String shareCode;

  @Column(name = "share_type", nullable = false, length = 20)
  private String shareType = "public"; // public, private, password

  @Column(name = "password", length = 100)
  private String password;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "access_count", nullable = false)
  private Integer accessCount = 0;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // 多对一关系：分享属于一个收藏夹
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection_id", insertable = false, updatable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Collection collection;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
