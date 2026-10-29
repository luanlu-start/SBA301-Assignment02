package fu.edu.sba301.a2.controller;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LuanLHCE191099_AuthController {

  private final LuanLHCE191099_AuthService auth;

  public LuanLHCE191099_AuthController(LuanLHCE191099_AuthService auth) {
    this.auth = auth;
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginInput input) {
    return auth.login(input);
  }

  @GetMapping("/me")
  public LuanLHCE191099_UserPrincipal me(
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return user;
  }
}
