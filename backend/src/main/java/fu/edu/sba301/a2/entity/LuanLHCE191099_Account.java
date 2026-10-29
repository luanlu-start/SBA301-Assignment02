package fu.edu.sba301.a2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "SystemAccount")
public class LuanLHCE191099_Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AccountID")
  private Long id;

  @Nationalized
  @Column(name = "AccountName", nullable = false, length = 100)
  private String name;

  @Column(name = "AccountEmail", nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "AccountRole", nullable = false)
  private Integer role;

  @Column(name = "AccountPassword", nullable = false, length = 100)
  private String password;

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

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public Integer getRole() {
    return this.role;
  }

  public void setRole(Integer value) {
    this.role = value;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String value) {
    this.password = value;
  }
}
