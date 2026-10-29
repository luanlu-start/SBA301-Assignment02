package fu.edu.sba301.a2.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
@Entity
@Table(name = "SystemAccount")
public class LuanLHCE191099_Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name="AccountID") public Long id;
    @Nationalized @Column(name="AccountName",nullable=false,length=100) public String name;
    @Column(name="AccountEmail",nullable=false,unique=true,length=150) public String email;
    @Column(name="AccountRole",nullable=false) public Integer role;
    @Column(name="AccountPassword",nullable=false,length=100) public String password;
}
