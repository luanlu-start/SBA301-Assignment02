package fu.edu.sba301.a2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Tag")
public class LuanLHCE191099_Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TagID")
  private Long id;

  @Nationalized
  @Column(name = "TagName", nullable = false, length = 50)
  private String name;

  @Nationalized
  @Column(name = "Note", length = 250)
  private String note;

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

  public String getNote() {
    return this.note;
  }

  public void setNote(String value) {
    this.note = value;
  }
}
