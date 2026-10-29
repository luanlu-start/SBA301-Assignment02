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
@Transactional(readOnly=true)
public class LuanLHCE191099_NewsService {
    private final LuanLHCE191099_NewsRepository news;
    private final LuanLHCE191099_CategoryRepository categories;
    private final LuanLHCE191099_AccountRepository accounts;
    public LuanLHCE191099_NewsService(LuanLHCE191099_NewsRepository news,LuanLHCE191099_CategoryRepository categories,LuanLHCE191099_AccountRepository accounts) {this.news=news;this.categories=categories;this.accounts=accounts;}
    public List<NewsResponse> list(String q,boolean publicOnly,Long author) {
        String keyword=q.trim().toLowerCase(Locale.ROOT);
        return (author==null?news.findAll(Sort.by(Sort.Direction.DESC,"id")):news.findByCreatedBy_IdOrderByIdDesc(author)).stream()
            .filter(n -> !publicOnly || n.status==1)
            .filter(n -> (n.title+" "+n.content).toLowerCase(Locale.ROOT).contains(keyword)).map(LuanLHCE191099_NewsService::view).toList();
    }
    public NewsResponse get(Long id,boolean publicOnly) {
        LuanLHCE191099_NewsArticle n=required(id);if(publicOnly && n.status!=1) throw new LuanLHCE191099_ApiException(404,"News article not found.");return view(n);
    }
    @Transactional public NewsResponse create(NewsInput input,Long actor) {
        LuanLHCE191099_NewsArticle n=new LuanLHCE191099_NewsArticle();n.createdBy=account(actor);n.createdDate=LocalDateTime.now();apply(n,input);return view(news.saveAndFlush(n));
    }
    @Transactional public NewsResponse update(Long id,NewsInput input,Long actor) {
        LuanLHCE191099_NewsArticle n=required(id);apply(n,input);n.updatedBy=account(actor);n.modifiedDate=LocalDateTime.now();return view(news.saveAndFlush(n));
    }
    @Transactional public void delete(Long id) {news.delete(required(id));news.flush();}
    private void apply(LuanLHCE191099_NewsArticle n,NewsInput input) {
        n.category=categories.findById(input.categoryId()).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Category not found."));
        n.title=input.title().trim();n.headline=input.headline();n.content=input.content().trim();n.source=input.source();n.status=input.status();
        n.tags.clear();
        for(TagInput value:input.tags()) {LuanLHCE191099_Tag tag=new LuanLHCE191099_Tag();tag.name=value.name().trim();tag.note=value.note();n.tags.add(tag);}
    }
    private LuanLHCE191099_Account account(Long id) {return accounts.findById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(401,"Account no longer exists."));}
    private LuanLHCE191099_NewsArticle required(Long id) {return news.findById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"News article not found."));}
    public static NewsResponse view(LuanLHCE191099_NewsArticle n) {
        return new NewsResponse(n.id,n.title,n.headline,n.content,n.source,n.category.id,n.category.name,n.status,n.createdBy.id,n.createdBy.name,
            n.updatedBy==null?null:n.updatedBy.name,n.createdDate,n.modifiedDate,n.tags.stream().map(t -> new TagResponse(t.id,t.name,t.note)).toList());
    }
}
