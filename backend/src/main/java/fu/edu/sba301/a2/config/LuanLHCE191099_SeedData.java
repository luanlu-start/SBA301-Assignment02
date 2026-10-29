package fu.edu.sba301.a2.config;

import fu.edu.sba301.a2.entity.*;
import fu.edu.sba301.a2.repository.*;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class LuanLHCE191099_SeedData implements ApplicationRunner {

  private final LuanLHCE191099_AccountRepository accounts;
  private final LuanLHCE191099_CategoryRepository categories;
  private final LuanLHCE191099_NewsRepository news;
  private final PasswordEncoder encoder;

  public LuanLHCE191099_SeedData(
    LuanLHCE191099_AccountRepository accounts,
    LuanLHCE191099_CategoryRepository categories,
    LuanLHCE191099_NewsRepository news,
    PasswordEncoder encoder
  ) {
    this.accounts = accounts;
    this.categories = categories;
    this.news = news;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (accounts.count() > 0) return;
    LuanLHCE191099_Account admin = new LuanLHCE191099_Account();
    admin.setName("Admin");
    admin.setEmail("admin@funews.local");
    admin.setRole(1);
    admin.setPassword(encoder.encode("Admin@123"));
    accounts.save(admin);
    LuanLHCE191099_Account staff = new LuanLHCE191099_Account();
    staff.setName("Luan - News staff");
    staff.setEmail("staff@funews.local");
    staff.setRole(2);
    staff.setPassword(encoder.encode("Staff@123"));
    accounts.save(staff);
    LuanLHCE191099_Category campus = new LuanLHCE191099_Category();
    campus.setName("Campus life");
    campus.setDescription("Activities and university announcements");
    campus.setStatus(1);
    categories.save(campus);
    LuanLHCE191099_Category study = new LuanLHCE191099_Category();
    study.setName("Study corner");
    study.setDescription("Learning resources");
    study.setStatus(1);
    categories.save(study);
    LuanLHCE191099_NewsArticle article = new LuanLHCE191099_NewsArticle();
    article.setTitle("Welcome to FUNews");
    article.setHeadline("News from our university community");
    article.setContent(
      "FUNews brings campus announcements and learning stories together. Staff can publish and manage articles, while visitors can read active news without signing in."
    );
    article.setSource("University newsroom");
    article.setCategory(campus);
    article.setStatus(1);
    article.setCreatedBy(staff);
    article.setCreatedDate(LocalDateTime.now());
    LuanLHCE191099_Tag tag = new LuanLHCE191099_Tag();
    tag.setName("campus");
    tag.setNote("University life");
    article.getTags().add(tag);
    news.save(article);
    LuanLHCE191099_NewsArticle draft = new LuanLHCE191099_NewsArticle();
    draft.setTitle("Upcoming study workshop");
    draft.setHeadline("Draft announcement");
    draft.setContent(
      "Workshop details are being prepared. This inactive draft is visible only in the staff area."
    );
    draft.setSource("Academic office");
    draft.setCategory(study);
    draft.setStatus(0);
    draft.setCreatedBy(staff);
    draft.setCreatedDate(LocalDateTime.now());
    news.save(draft);
  }
}
