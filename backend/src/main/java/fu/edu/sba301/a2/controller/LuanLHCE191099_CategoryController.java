package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_CategoryRequest;
import fu.edu.sba301.a2.dto.LuanLHCE191099_CategoryResponse;
import fu.edu.sba301.a2.service.LuanLHCE191099_CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

// HTTP adapter for /api/categories. Spring converts returned DTOs to JSON.
// Only STAFF may call these methods; CategoryService owns business rules and transactions.
// Constructor injection supplies the service managed by Spring, instead of creating it with new.
@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_CategoryController {

    private final LuanLHCE191099_CategoryService categoryService;

    public LuanLHCE191099_CategoryController(LuanLHCE191099_CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    // GET the resource collection; an empty q reads all records, otherwise it searches.
    // This method delegates rather than writing SQL or business validation in the controller.
    public List<LuanLHCE191099_CategoryResponse> getCategories(
        @RequestParam(defaultValue = "") String q
    ) {
        if (q.isBlank()) {
            return categoryService.getAllCategories();
        }
        return categoryService.searchCategories(q);
    }

    // Read one record: @PathVariable obtains id from the URL; the service returns 404 when it is absent.
    @GetMapping("/{id}")
    public LuanLHCE191099_CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Create: @RequestBody maps JSON to an Input record; @Valid checks its validation annotations.
    // Return 201 Created when the service succeeds; validation/business failures use the shared error handler.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LuanLHCE191099_CategoryResponse createCategory(
        @Valid @RequestBody LuanLHCE191099_CategoryRequest input
    ) {
        return categoryService.createCategory(input);
    }

    // Update the existing URL ID with the validated request body; the URL ID selects the target record.
    @PutMapping("/{id}")
    public LuanLHCE191099_CategoryResponse updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_CategoryRequest input
    ) {
        return categoryService.updateCategory(id, input);
    }

    // Delete the URL ID after service checks; 204 No Content means success with no JSON response body.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
