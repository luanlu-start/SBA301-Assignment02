package fu.edu.sba301.a2.service;

import fu.edu.sba301.a2.dto.LuanLHCE191099_NewsRequest;
import fu.edu.sba301.a2.dto.LuanLHCE191099_NewsResponse;
import fu.edu.sba301.a2.dto.LuanLHCE191099_TagRequest;
import fu.edu.sba301.a2.dto.LuanLHCE191099_TagResponse;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import fu.edu.sba301.a2.entity.LuanLHCE191099_NewsArticle;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Tag;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.LuanLHCE191099_AccountRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_CategoryRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_NewsRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_NewsService {

    private final LuanLHCE191099_NewsRepository newsRepository;
    private final LuanLHCE191099_CategoryRepository categoryRepository;
    private final LuanLHCE191099_AccountRepository accountRepository;

    public LuanLHCE191099_NewsService(
        LuanLHCE191099_NewsRepository newsRepository,
        LuanLHCE191099_CategoryRepository categoryRepository,
        LuanLHCE191099_AccountRepository accountRepository
    ) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    // Staff may read both active news and drafts.
    public List<LuanLHCE191099_NewsResponse> getNews(String searchText) {
        List<LuanLHCE191099_NewsArticle> articles = newsRepository.findAll(
            Sort.by(Sort.Direction.DESC, "id")
        );
        return filterAndConvert(articles, searchText, false);
    }

    // Public readers may receive only Active=1 articles.
    public List<LuanLHCE191099_NewsResponse> getPublicNews(String searchText) {
        List<LuanLHCE191099_NewsArticle> articles = newsRepository.findAll(
            Sort.by(Sort.Direction.DESC, "id")
        );
        return filterAndConvert(articles, searchText, true);
    }

    // The controller supplies the authenticated author ID, not an ID chosen by the browser.
    public List<LuanLHCE191099_NewsResponse> getNewsHistory(String searchText, Long authorId) {
        List<LuanLHCE191099_NewsArticle> articles = newsRepository.findByCreatedBy_IdOrderByIdDesc(
            authorId
        );
        return filterAndConvert(articles, searchText, false);
    }

    private List<LuanLHCE191099_NewsResponse> filterAndConvert(
        List<LuanLHCE191099_NewsArticle> articles,
        String searchText,
        boolean activeOnly
    ) {
        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_NewsResponse> responses = new ArrayList<>();

        for (LuanLHCE191099_NewsArticle article : articles) {
            if (activeOnly && article.getStatus() != 1) {
                continue;
            }

            String articleText = article.getTitle() + " " + article.getContent();
            if (articleText.toLowerCase(Locale.ROOT).contains(keyword)) {
                responses.add(toResponse(article));
            }
        }
        return responses;
    }

    public LuanLHCE191099_NewsResponse getNewsById(Long newsId) {
        return toResponse(findNewsOrThrow(newsId));
    }

    public LuanLHCE191099_NewsResponse getPublicNewsById(Long newsId) {
        LuanLHCE191099_NewsArticle article = findNewsOrThrow(newsId);
        if (article.getStatus() != 1) {
            throw new LuanLHCE191099_ApiException(404, "News article not found.");
        }
        return toResponse(article);
    }

    @Transactional
    public LuanLHCE191099_NewsResponse createNews(
        LuanLHCE191099_NewsRequest input,
        Long currentUserId
    ) {
        LuanLHCE191099_NewsArticle article = new LuanLHCE191099_NewsArticle();
        article.setCreatedBy(findAuthorOrThrow(currentUserId));
        article.setCreatedDate(LocalDateTime.now());
        applyInput(article, input);
        return toResponse(newsRepository.saveAndFlush(article));
    }

    @Transactional
    public LuanLHCE191099_NewsResponse updateNews(
        Long newsId,
        LuanLHCE191099_NewsRequest input,
        Long currentUserId
    ) {
        LuanLHCE191099_NewsArticle article = findNewsOrThrow(newsId);
        applyInput(article, input);
        article.setUpdatedBy(findAuthorOrThrow(currentUserId));
        article.setModifiedDate(LocalDateTime.now());
        return toResponse(newsRepository.saveAndFlush(article));
    }

    @Transactional
    public void deleteNews(Long newsId) {
        LuanLHCE191099_NewsArticle article = findNewsOrThrow(newsId);
        newsRepository.delete(article);
        newsRepository.flush();
    }

    private void applyInput(LuanLHCE191099_NewsArticle article, LuanLHCE191099_NewsRequest input) {
        LuanLHCE191099_Category category = categoryRepository
            .findById(input.categoryId())
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Category not found."));

        article.setCategory(category);
        article.setTitle(input.title().trim());
        article.setHeadline(input.headline());
        article.setContent(input.content().trim());
        article.setSource(input.source());
        article.setStatus(input.status());

        // The article owns these tags: removed tags are deleted and new ones cascade on save.
        article.getTags().clear();
        for (LuanLHCE191099_TagRequest tagInput : input.tags()) {
            LuanLHCE191099_Tag tag = new LuanLHCE191099_Tag();
            tag.setName(tagInput.name().trim());
            tag.setNote(tagInput.note());
            article.getTags().add(tag);
        }
    }

    private LuanLHCE191099_Account findAuthorOrThrow(Long accountId) {
        return accountRepository
            .findById(accountId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(401, "Account no longer exists."));
    }

    private LuanLHCE191099_NewsArticle findNewsOrThrow(Long newsId) {
        return newsRepository
            .findById(newsId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "News article not found."));
    }

    // Convert related entities while the persistence context is still open.
    private LuanLHCE191099_NewsResponse toResponse(LuanLHCE191099_NewsArticle article) {
        List<LuanLHCE191099_TagResponse> tags = new ArrayList<>();
        for (LuanLHCE191099_Tag tag : article.getTags()) {
            tags.add(new LuanLHCE191099_TagResponse(tag.getId(), tag.getName(), tag.getNote()));
        }

        String updatedByName = null;
        if (article.getUpdatedBy() != null) {
            updatedByName = article.getUpdatedBy().getName();
        }

        return new LuanLHCE191099_NewsResponse(
            article.getId(),
            article.getTitle(),
            article.getHeadline(),
            article.getContent(),
            article.getSource(),
            article.getCategory().getId(),
            article.getCategory().getName(),
            article.getStatus(),
            article.getCreatedBy().getId(),
            article.getCreatedBy().getName(),
            updatedByName,
            article.getCreatedDate(),
            article.getModifiedDate(),
            tags
        );
    }
}
