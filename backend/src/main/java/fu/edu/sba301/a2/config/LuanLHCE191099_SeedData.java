package fu.edu.sba301.a2.config;
import fu.edu.sba301.a2.entity.*;
import fu.edu.sba301.a2.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Component
@ConditionalOnProperty(name="app.seed.enabled",havingValue="true",matchIfMissing=true)
public class LuanLHCE191099_SeedData implements ApplicationRunner {
    private final LuanLHCE191099_AccountRepository accounts;private final LuanLHCE191099_CategoryRepository categories;private final LuanLHCE191099_NewsRepository news;private final PasswordEncoder encoder;
    public LuanLHCE191099_SeedData(LuanLHCE191099_AccountRepository accounts,LuanLHCE191099_CategoryRepository categories,LuanLHCE191099_NewsRepository news,PasswordEncoder encoder) {this.accounts=accounts;this.categories=categories;this.news=news;this.encoder=encoder;}
    @Override @Transactional public void run(ApplicationArguments args) {
        if(accounts.count()>0) return;
        LuanLHCE191099_Account admin=new LuanLHCE191099_Account();admin.name="Admin";admin.email="admin@funews.local";admin.role=1;admin.password=encoder.encode("Admin@123");accounts.save(admin);
        LuanLHCE191099_Account staff=new LuanLHCE191099_Account();staff.name="Luan - News staff";staff.email="staff@funews.local";staff.role=2;staff.password=encoder.encode("Staff@123");accounts.save(staff);
        LuanLHCE191099_Category campus=new LuanLHCE191099_Category();campus.name="Campus life";campus.description="Activities and university announcements";campus.status=1;categories.save(campus);
        LuanLHCE191099_Category study=new LuanLHCE191099_Category();study.name="Study corner";study.description="Learning resources";study.status=1;categories.save(study);
        LuanLHCE191099_NewsArticle article=new LuanLHCE191099_NewsArticle();article.title="Welcome to FUNews";article.headline="News from our university community";article.content="FUNews brings campus announcements and learning stories together. Staff can publish and manage articles, while visitors can read active news without signing in.";article.source="University newsroom";article.category=campus;article.status=1;article.createdBy=staff;article.createdDate=LocalDateTime.now();
        LuanLHCE191099_Tag tag=new LuanLHCE191099_Tag();tag.name="campus";tag.note="University life";article.tags.add(tag);news.save(article);
        LuanLHCE191099_NewsArticle draft=new LuanLHCE191099_NewsArticle();draft.title="Upcoming study workshop";draft.headline="Draft announcement";draft.content="Workshop details are being prepared. This inactive draft is visible only in the staff area.";draft.source="Academic office";draft.category=study;draft.status=0;draft.createdBy=staff;draft.createdDate=LocalDateTime.now();news.save(draft);
    }
}
