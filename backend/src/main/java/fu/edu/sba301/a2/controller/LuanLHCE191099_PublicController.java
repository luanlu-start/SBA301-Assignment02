package fu.edu.sba301.a2.controller;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.service.LuanLHCE191099_NewsService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/news")
public class LuanLHCE191099_PublicController {

  private final LuanLHCE191099_NewsService news;

  public LuanLHCE191099_PublicController(LuanLHCE191099_NewsService news) {
    this.news = news;
  }

  @GetMapping
  public List<NewsResponse> list(@RequestParam(defaultValue = "") String q) {
    return news.list(q, true, null);
  }

  @GetMapping("/{id}")
  public NewsResponse get(@PathVariable Long id) {
    return news.get(id, true);
  }
}
