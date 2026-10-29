package fu.edu.sba301.a2.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
@Entity
@Table(name="Category")
public class LuanLHCE191099_Category {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="CategoryID") public Long id;
    @Nationalized @Column(name="CategoryName",nullable=false,length=100) public String name;
    @Nationalized @Column(name="CategoryDescription",length=500) public String description;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ParentCategoryID") public LuanLHCE191099_Category parent;
    @Column(name="IsActive",nullable=false) public Integer status;
}
