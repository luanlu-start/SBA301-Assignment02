package fu.edu.sba301.a2.service;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.entity.*;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_NewsService {

  private final LuanLHCE191099_NewsRepository news;
  private final LuanLHCE191099_CategoryRepository categories;
  private final LuanLHCE191099_AccountRepository accounts;

  public LuanLHCE191099_NewsService(
    LuanLHCE191099_NewsRepository news,
    LuanLHCE191099_CategoryRepository categories,
    LuanLHCE191099_AccountRepository accounts
  ) {
    this.news = news;
    this.categories = categories;
    this.accounts = accounts;
  }

  public List<NewsResponse> list(String q, boolean publicOnly, Long author) {
    String keyword = q.trim().toLowerCase(Locale.ROOT);
    return (
      author == null
        ? news.findAll(Sort.by(Sort.Direction.DESC, "id"))
        : news.findByCreatedBy_IdOrderByIdDesc(author)
    )
      .stream()
      .filter(n -> !publicOnly || n.getStatus() == 1)
      .filter(n -> (n.getTitle() + " " + n.getContent()).toLowerCase(Locale.ROOT).contains(keyword))
      .map(LuanLHCE191099_NewsService::view)
      .toList();
  }

  public NewsResponse get(Long id, boolean publicOnly) {
    LuanLHCE191099_NewsArticle n = required(id);
    if (publicOnly && n.getStatus() != 1) throw new LuanLHCE191099_ApiException(
      404,
      "News article not found."
    );
    return view(n);
  }

  @Transactional
  public NewsResponse create(NewsInput input, Long actor) {
    LuanLHCE191099_NewsArticle n = new LuanLHCE191099_NewsArticle();
    n.setCreatedBy(account(actor));
    n.setCreatedDate(LocalDateTime.now());
    apply(n, input);
    return view(news.saveAndFlush(n));
  }

  @Transactional
  public NewsResponse update(Long id, NewsInput input, Long actor) {
    LuanLHCE191099_NewsArticle n = required(id);
    apply(n, input);
    n.setUpdatedBy(account(actor));
    n.setModifiedDate(LocalDateTime.now());
    return view(news.saveAndFlush(n));
  }

  @Transactional
  public void delete(Long id) {
    news.delete(required(id));
    news.flush();
  }

  private void apply(LuanLHCE191099_NewsArticle n, NewsInput input) {
    n.setCategory(
      categories
        .findById(input.categoryId())
        .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Category not found."))
    );
    n.setTitle(input.title().trim());
    n.setHeadline(input.headline());
    n.setContent(input.content().trim());
    n.setSource(input.source());
    n.setStatus(input.status());
    n.getTags().clear();
    for (TagInput value : input.tags()) {
      LuanLHCE191099_Tag tag = new LuanLHCE191099_Tag();
      tag.setName(value.name().trim());
      tag.setNote(value.note());
      n.getTags().add(tag);
    }
  }

  private LuanLHCE191099_Account account(Long id) {
    return accounts
      .findById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(401, "Account no longer exists."));
  }

  private LuanLHCE191099_NewsArticle required(Long id) {
    return news
      .findById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "News article not found."));
  }

  public static NewsResponse view(LuanLHCE191099_NewsArticle n) {
    return new NewsResponse(
      n.getId(),
      n.getTitle(),
      n.getHeadline(),
      n.getContent(),
      n.getSource(),
      n.getCategory().getId(),
      n.getCategory().getName(),
      n.getStatus(),
      n.getCreatedBy().getId(),
      n.getCreatedBy().getName(),
      n.getUpdatedBy() == null ? null : n.getUpdatedBy().getName(),
      n.getCreatedDate(),
      n.getModifiedDate(),
      n
        .getTags()
        .stream()
        .map(t -> new TagResponse(t.getId(), t.getName(), t.getNote()))
        .toList()
    );
  }
}
