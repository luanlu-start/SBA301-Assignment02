package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_AccountResponse;
import fu.edu.sba301.a2.dto.LuanLHCE191099_ProfileRequest;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_AccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Staff self-service profile endpoints. The authenticated principal always selects the target account.
// There is no account ID supplied by the browser, and LuanLHCE191099_ProfileRequest exposes no role field.
@RestController
@RequestMapping("/api/profile")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_ProfileController {

    private final LuanLHCE191099_AccountService accountService;

    public LuanLHCE191099_ProfileController(LuanLHCE191099_AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public LuanLHCE191099_AccountResponse get(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        return accountService.getAccountById(user.id());
    }

    // AccountService preserves the existing role, validates email uniqueness and optionally replaces the password.
    @PutMapping
    public LuanLHCE191099_AccountResponse update(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user,
        @Valid @RequestBody LuanLHCE191099_ProfileRequest input
    ) {
        return accountService.updateProfile(user.id(), input);
    }
}
