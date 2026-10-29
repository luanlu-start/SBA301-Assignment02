package fu.edu.sba301.a2.service;
import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.*;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly=true)
public class LuanLHCE191099_CategoryService {
    private final LuanLHCE191099_CategoryRepository categories;
    private final LuanLHCE191099_NewsRepository news;
    public LuanLHCE191099_CategoryService(LuanLHCE191099_CategoryRepository categories,LuanLHCE191099_NewsRepository news) {this.categories=categories;this.news=news;}
    public List<CategoryResponse> list(String q) { String keyword=q.trim().toLowerCase(Locale.ROOT);return categories.findAll(Sort.by("id")).stream().filter(c -> c.name.toLowerCase(Locale.ROOT).contains(keyword)).map(LuanLHCE191099_CategoryService::view).toList(); }
    public CategoryResponse get(Long id) {return view(required(id));}
    @Transactional public CategoryResponse create(CategoryInput input) {LuanLHCE191099_Category c=new LuanLHCE191099_Category();apply(c,input);return view(categories.saveAndFlush(c));}
    @Transactional public CategoryResponse update(Long id,CategoryInput input) {LuanLHCE191099_Category c=required(id);apply(c,input);return view(categories.saveAndFlush(c));}
    @Transactional public void delete(Long id) {
        LuanLHCE191099_Category c=required(id);
        if(news.existsByCategory_Id(id)) throw new LuanLHCE191099_ApiException(409,"This category is used by news articles and cannot be deleted.");
        if(categories.existsByParent_Id(id)) throw new LuanLHCE191099_ApiException(409,"This category has child categories.");
        categories.delete(c);categories.flush();
    }
    private void apply(LuanLHCE191099_Category c,CategoryInput input) {
        LuanLHCE191099_Category parent=input.parentId()==null?null:required(input.parentId());
        for(LuanLHCE191099_Category current=parent;current!=null;current=current.parent) {
            if(current.id.equals(c.id)) throw new LuanLHCE191099_ApiException(400,"A category cannot be its own ancestor.");
        }
        c.name=input.name().trim();c.description=input.description();c.status=input.status();c.parent=parent;
    }
    private LuanLHCE191099_Category required(Long id) {return categories.findById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Category not found."));}
    public static CategoryResponse view(LuanLHCE191099_Category c) {return new CategoryResponse(c.id,c.name,c.description,c.parent==null?null:c.parent.id,c.parent==null?null:c.parent.name,c.status);}
}
