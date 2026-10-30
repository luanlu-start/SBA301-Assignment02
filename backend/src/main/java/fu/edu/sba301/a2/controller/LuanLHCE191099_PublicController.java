package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_NewsResponse;
import fu.edu.sba301.a2.service.LuanLHCE191099_NewsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Public read-only news endpoints; no login is needed to request them.
// NewsService public read methods filter active records and hide inactive article IDs.
@RestController
@RequestMapping("/api/public/news")
public class LuanLHCE191099_PublicController {

    private final LuanLHCE191099_NewsService newsService;

    public LuanLHCE191099_PublicController(LuanLHCE191099_NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<LuanLHCE191099_NewsResponse> list(@RequestParam(defaultValue = "") String q) {
        return newsService.getPublicNews(q);
    }

    @GetMapping("/{id}")
    public LuanLHCE191099_NewsResponse get(@PathVariable Long id) {
        return newsService.getPublicNewsById(id);
    }
}
