package fu.edu.sba301.a2.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Nationalized;
@Entity
@Table(name="NewsArticle")
public class LuanLHCE191099_NewsArticle {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="NewsArticleID") public Long id;
    @Nationalized @Column(name="NewsTitle",nullable=false,length=200) public String title;
    @Nationalized @Column(name="Headline",length=500) public String headline;
    @Column(name="CreatedDate",nullable=false) public LocalDateTime createdDate;
    @Nationalized @Column(name="NewsContent",nullable=false,length=10000) public String content;
    @Nationalized @Column(name="NewsSource",length=255) public String source;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="CategoryID",nullable=false) public LuanLHCE191099_Category category;
    @Column(name="NewsStatus",nullable=false) public Integer status;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="CreatedByID",nullable=false) public LuanLHCE191099_Account createdBy;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="UpdatedByID") public LuanLHCE191099_Account updatedBy;
    @Column(name="ModifiedDate") public LocalDateTime modifiedDate;
    @OneToMany(cascade=CascadeType.ALL,orphanRemoval=true)
    @JoinTable(name="NewsTag",joinColumns=@JoinColumn(name="NewsArticleID"),inverseJoinColumns=@JoinColumn(name="TagID"),uniqueConstraints=@UniqueConstraint(columnNames="TagID"))
    public List<LuanLHCE191099_Tag> tags=new ArrayList<>();
}
