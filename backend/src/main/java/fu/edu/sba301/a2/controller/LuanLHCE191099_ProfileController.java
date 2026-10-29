package fu.edu.sba301.a2.controller;
import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a2.service.LuanLHCE191099_AccountService;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/profile")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_ProfileController {
    private final LuanLHCE191099_AccountService service;
    public LuanLHCE191099_ProfileController(LuanLHCE191099_AccountService service) {this.service=service;}
    @GetMapping public AccountResponse get(@AuthenticationPrincipal LuanLHCE191099_UserPrincipal user) {return service.get(user.id());}
    @PutMapping public AccountResponse update(@AuthenticationPrincipal LuanLHCE191099_UserPrincipal user,@Valid @RequestBody ProfileInput input) {return service.profile(user.id(),input);}
}
