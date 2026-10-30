package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_NewsRequest;
import fu.edu.sba301.a2.dto.LuanLHCE191099_NewsResponse;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_NewsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// HTTP adapter for /api/news. Spring converts returned DTOs to JSON.
// Only STAFF may call these methods; NewsService owns business rules and transactions.
// Constructor injection supplies the service managed by Spring, instead of creating it with new.
@RestController
@RequestMapping("/api/news")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_NewsController {

    private final LuanLHCE191099_NewsService newsService;

    public LuanLHCE191099_NewsController(LuanLHCE191099_NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    // GET the resource collection; an empty q reads all records, otherwise it searches.
    // This method delegates rather than writing SQL or business validation in the controller.
    public List<LuanLHCE191099_NewsResponse> getNews(@RequestParam(defaultValue = "") String q) {
        return newsService.getNews(q);
    }

    // Read one record: @PathVariable obtains id from the URL; the service returns 404 when it is absent.
    @GetMapping("/{id}")
    public LuanLHCE191099_NewsResponse getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id);
    }

    // Read only news created by this caller; obtain their ID from SecurityContext, not a query parameter.
    // The service author filter keeps personal history scoped to the authenticated Staff account.
    @GetMapping("/history")
    public List<LuanLHCE191099_NewsResponse> history(
        @RequestParam(defaultValue = "") String q,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        return newsService.getNewsHistory(q, user.id());
    }

    // Create: @RequestBody maps JSON to an Input record; @Valid checks its validation annotations.
    // Return 201 Created when the service succeeds; validation/business failures use the shared error handler.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LuanLHCE191099_NewsResponse createNews(
        @Valid @RequestBody LuanLHCE191099_NewsRequest input,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        // The server assigns CreatedBy from the authenticated Staff principal; the browser cannot choose the author.
        return newsService.createNews(input, user.id());
    }

    // Update the existing URL ID with the validated request body; the URL ID selects the target record.
    @PutMapping("/{id}")
    public LuanLHCE191099_NewsResponse updateNews(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_NewsRequest input,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        // Record the actual editor as UpdatedBy while preserving the original author.
        return newsService.updateNews(id, input, user.id());
    }

    // Delete the URL ID after service checks; 204 No Content means success with no JSON response body.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }
}
