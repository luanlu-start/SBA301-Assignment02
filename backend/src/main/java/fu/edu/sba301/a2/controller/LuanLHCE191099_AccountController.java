package fu.edu.sba301.a2.controller;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_AccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class LuanLHCE191099_AccountController {

  private final LuanLHCE191099_AccountService service;

  public LuanLHCE191099_AccountController(LuanLHCE191099_AccountService service) {
    this.service = service;
  }

  @GetMapping
  public List<AccountResponse> list(@RequestParam(defaultValue = "") String q) {
    return service.list(q);
  }

  @GetMapping("/{id}")
  public AccountResponse get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponse create(@Valid @RequestBody AccountInput input) {
    return service.create(input);
  }

  @PutMapping("/{id}")
  public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountInput input) {
    return service.update(id, input);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @PathVariable Long id,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    service.delete(id, user.id());
  }
}
