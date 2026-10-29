package fu.edu.sba301.a2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "NewsArticle")
public class LuanLHCE191099_NewsArticle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "NewsArticleID")
  private Long id;

  @Nationalized
  @Column(name = "NewsTitle", nullable = false, length = 200)
  private String title;

  @Nationalized
  @Column(name = "Headline", length = 500)
  private String headline;

  @Column(name = "CreatedDate", nullable = false)
  private LocalDateTime createdDate;

  @Nationalized
  @Column(name = "NewsContent", nullable = false, length = 10000)
  private String content;

  @Nationalized
  @Column(name = "NewsSource", length = 255)
  private String source;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CategoryID", nullable = false)
  private LuanLHCE191099_Category category;

  @Column(name = "NewsStatus", nullable = false)
  private Integer status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CreatedByID", nullable = false)
  private LuanLHCE191099_Account createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "UpdatedByID")
  private LuanLHCE191099_Account updatedBy;

  @Column(name = "ModifiedDate")
  private LocalDateTime modifiedDate;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
    name = "NewsTag",
    joinColumns = @JoinColumn(name = "NewsArticleID"),
    inverseJoinColumns = @JoinColumn(name = "TagID"),
    uniqueConstraints = @UniqueConstraint(columnNames = "TagID")
  )
  private List<LuanLHCE191099_Tag> tags = new ArrayList<>();

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String value) {
    this.title = value;
  }

  public String getHeadline() {
    return this.headline;
  }

  public void setHeadline(String value) {
    this.headline = value;
  }

  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(LocalDateTime value) {
    this.createdDate = value;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String value) {
    this.content = value;
  }

  public String getSource() {
    return this.source;
  }

  public void setSource(String value) {
    this.source = value;
  }

  public LuanLHCE191099_Category getCategory() {
    return this.category;
  }

  public void setCategory(LuanLHCE191099_Category value) {
    this.category = value;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer value) {
    this.status = value;
  }

  public LuanLHCE191099_Account getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(LuanLHCE191099_Account value) {
    this.createdBy = value;
  }

  public LuanLHCE191099_Account getUpdatedBy() {
    return this.updatedBy;
  }

  public void setUpdatedBy(LuanLHCE191099_Account value) {
    this.updatedBy = value;
  }

  public LocalDateTime getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(LocalDateTime value) {
    this.modifiedDate = value;
  }

  public List<LuanLHCE191099_Tag> getTags() {
    return this.tags;
  }

  public void setTags(List<LuanLHCE191099_Tag> value) {
    this.tags = value;
  }
}
