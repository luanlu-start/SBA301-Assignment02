package fu.edu.sba301.a2.config;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import fu.edu.sba301.a2.entity.LuanLHCE191099_NewsArticle;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Tag;
import fu.edu.sba301.a2.repository.LuanLHCE191099_AccountRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_CategoryRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_NewsRepository;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// ApplicationRunner executes after the application context is initialized.
// The app.seed.enabled property can disable this sample-data initialization.
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class LuanLHCE191099_SeedData implements ApplicationRunner {

    private final LuanLHCE191099_AccountRepository accountRepository;
    private final LuanLHCE191099_CategoryRepository categoryRepository;
    private final LuanLHCE191099_NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;

    public LuanLHCE191099_SeedData(
        LuanLHCE191099_AccountRepository accountRepository,
        LuanLHCE191099_CategoryRepository categoryRepository,
        LuanLHCE191099_NewsRepository newsRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Skip the entire seed when at least one account already exists; do not overwrite user edits.
        // This is initial sample data, not a database reset or an automatic repair of missing records.
        if (accountRepository.count() > 0) {
            return;
        }
        // Create Admin and Staff with role codes 1 and 2. Store BCrypt hashes, not plaintext passwords.
        // @Transactional groups account, category, article and tag writes into one transaction.
        LuanLHCE191099_Account admin = new LuanLHCE191099_Account();
        admin.setName("Admin");
        admin.setEmail("admin@funews.local");
        admin.setRole(1);
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        accountRepository.save(admin);
        LuanLHCE191099_Account staff = new LuanLHCE191099_Account();
        staff.setName("Luan - News staff");
        staff.setEmail("staff@funews.local");
        staff.setRole(2);
        staff.setPassword(passwordEncoder.encode("Staff@123"));
        accountRepository.save(staff);
        LuanLHCE191099_Category campus = new LuanLHCE191099_Category();
        campus.setName("Campus life");
        campus.setDescription("Activities and university announcements");
        campus.setStatus(1);
        categoryRepository.save(campus);
        LuanLHCE191099_Category study = new LuanLHCE191099_Category();
        study.setName("Study corner");
        study.setDescription("Learning resources");
        study.setStatus(1);
        categoryRepository.save(study);
        // Create one active article with an author, category and tag to demonstrate the entity relationships.
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
        newsRepository.save(article);
        // Create one inactive draft so the public-versus-Staff visibility rule can be verified.
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
        newsRepository.save(draft);
    }
}
