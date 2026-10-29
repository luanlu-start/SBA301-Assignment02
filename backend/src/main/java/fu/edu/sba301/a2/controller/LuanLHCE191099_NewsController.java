package fu.edu.sba301.a2.controller;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_NewsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_NewsController {

  private final LuanLHCE191099_NewsService service;

  public LuanLHCE191099_NewsController(LuanLHCE191099_NewsService service) {
    this.service = service;
  }

  @GetMapping
  public List<NewsResponse> list(@RequestParam(defaultValue = "") String q) {
    return service.list(q, false, null);
  }

  @GetMapping("/{id}")
  public NewsResponse get(@PathVariable Long id) {
    return service.get(id, false);
  }

  @GetMapping("/history")
  public List<NewsResponse> history(
    @RequestParam(defaultValue = "") String q,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return service.list(q, false, user.id());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public NewsResponse create(
    @Valid @RequestBody NewsInput input,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return service.create(input, user.id());
  }

  @PutMapping("/{id}")
  public NewsResponse update(
    @PathVariable Long id,
    @Valid @RequestBody NewsInput input,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return service.update(id, input, user.id());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
