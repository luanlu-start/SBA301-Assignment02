package fu.edu.sba301.a2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Category")
public class LuanLHCE191099_Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CategoryID")
  private Long id;

  @Nationalized
  @Column(name = "CategoryName", nullable = false, length = 100)
  private String name;

  @Nationalized
  @Column(name = "CategoryDescription", length = 500)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ParentCategoryID")
  private LuanLHCE191099_Category parent;

  @Column(name = "IsActive", nullable = false)
  private Integer status;

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public LuanLHCE191099_Category getParent() {
    return this.parent;
  }

  public void setParent(LuanLHCE191099_Category value) {
    this.parent = value;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer value) {
    this.status = value;
  }
}
