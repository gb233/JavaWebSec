package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 收藏项实体类 */
@Entity
@Table(name = "collection_items")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CollectionItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "collection_id", nullable = false)
  private Long collectionId;

  @Column(name = "item_type", nullable = false, length = 50)
  private String itemType; // vulnerability, note, challenge, test

  @Column(name = "item_id", nullable = false)
  private Long itemId;

  @Column(name = "item_title", nullable = false, length = 200)
  private String itemTitle;

  @Column(name = "item_description", columnDefinition = "TEXT")
  private String itemDescription;

  @Column(name = "item_url", length = 500)
  private String itemUrl;

  @Column(name = "item_metadata", columnDefinition = "JSON")
  private String itemMetadata;

  @Column(name = "added_at", nullable = false)
  private LocalDateTime addedAt;

  // 多对一关系：收藏项属于一个收藏夹
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection_id", insertable = false, updatable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Collection collection;

  // 多对多关系：收藏项可以有多个标签
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "collection_item_tags",
      joinColumns = @JoinColumn(name = "collection_item_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<CollectionTag> tags = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    addedAt = LocalDateTime.now();
  }
}
