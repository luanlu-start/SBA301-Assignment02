package fu.edu.sba301.a2.controller;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.service.LuanLHCE191099_CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_CategoryController {

  private final LuanLHCE191099_CategoryService service;

  public LuanLHCE191099_CategoryController(LuanLHCE191099_CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public List<CategoryResponse> list(@RequestParam(defaultValue = "") String q) {
    return service.list(q);
  }

  @GetMapping("/{id}")
  public CategoryResponse get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponse create(@Valid @RequestBody CategoryInput input) {
    return service.create(input);
  }

  @PutMapping("/{id}")
  public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryInput input) {
    return service.update(id, input);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
