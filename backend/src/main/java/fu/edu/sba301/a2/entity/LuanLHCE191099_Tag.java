package fu.edu.sba301.a2.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
@Entity
@Table(name="Tag")
public class LuanLHCE191099_Tag {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="TagID") public Long id;
    @Nationalized @Column(name="TagName",nullable=false,length=50) public String name;
    @Nationalized @Column(name="Note",length=250) public String note;
}
