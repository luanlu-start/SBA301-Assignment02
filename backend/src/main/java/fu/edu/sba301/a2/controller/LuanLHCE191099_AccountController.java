package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_AccountInput;
import fu.edu.sba301.a2.dto.LuanLHCE191099_AccountResponse;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_AccountService;
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

// HTTP adapter for /api/accounts. Spring converts returned DTOs to JSON.
// Only ADMIN may call these methods; AccountService owns business rules and transactions.
// Constructor injection supplies the service managed by Spring, instead of creating it with new.
@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class LuanLHCE191099_AccountController {

    private final LuanLHCE191099_AccountService accountService;

    public LuanLHCE191099_AccountController(LuanLHCE191099_AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    // GET the resource collection; an empty q reads all records, otherwise it searches.
    // This method delegates rather than writing SQL or business validation in the controller.
    public List<LuanLHCE191099_AccountResponse> getAccounts(
        @RequestParam(defaultValue = "") String q
    ) {
        if (q.isBlank()) {
            return accountService.getAllAccounts();
        }
        return accountService.searchAccounts(q);
    }

    // Read one record: @PathVariable obtains id from the URL; the service returns 404 when it is absent.
    @GetMapping("/{id}")
    public LuanLHCE191099_AccountResponse getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    // Create: @RequestBody maps JSON to an Input record; @Valid checks its validation annotations.
    // Return 201 Created when the service succeeds; validation/business failures use the shared error handler.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LuanLHCE191099_AccountResponse createAccount(
        @Valid @RequestBody LuanLHCE191099_AccountInput input
    ) {
        return accountService.createAccount(input);
    }

    // Update the existing URL ID with the validated request body; the URL ID selects the target record.
    @PutMapping("/{id}")
    public LuanLHCE191099_AccountResponse updateAccount(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_AccountInput input
    ) {
        return accountService.updateAccount(id, input);
    }

    // Delete the URL ID after service checks; 204 No Content means success with no JSON response body.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
        @PathVariable Long id,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        // Use the authenticated account ID to prevent the current Admin from deleting their own account.
        accountService.deleteAccount(id, user.id());
    }
}
