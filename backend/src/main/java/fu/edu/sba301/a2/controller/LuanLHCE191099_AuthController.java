package fu.edu.sba301.a2.controller;

import fu.edu.sba301.a2.dto.LuanLHCE191099_AuthResponse;
import fu.edu.sba301.a2.dto.LuanLHCE191099_LoginInput;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a2.service.LuanLHCE191099_AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LuanLHCE191099_AuthController {

    private final LuanLHCE191099_AuthService authService;
    private final SecurityContextRepository securityContextRepository;
    private final CsrfTokenRepository csrfTokenRepository;

    public LuanLHCE191099_AuthController(
        LuanLHCE191099_AuthService authService,
        SecurityContextRepository securityContextRepository,
        CsrfTokenRepository csrfTokenRepository
    ) {
        this.authService = authService;
        this.securityContextRepository = securityContextRepository;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    // The browser requests this value before POST/PUT/DELETE; it protects cookie-based requests.
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/login")
    public LuanLHCE191099_AuthResponse login(
        @Valid @RequestBody LuanLHCE191099_LoginInput input,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        LuanLHCE191099_UserPrincipal user = authService.authenticate(input);

        // Create a session if needed and rotate its ID after successful authentication.
        request.getSession(true);
        request.changeSessionId();
        saveAuthentication(user, request, response);
        csrfTokenRepository.saveToken(null, request, response);

        return new LuanLHCE191099_AuthResponse(user);
    }

    // Reload the account after F5 or profile edits, then refresh the stored principal.
    @GetMapping("/me")
    public LuanLHCE191099_UserPrincipal me(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        LuanLHCE191099_UserPrincipal currentUser = authService.getCurrentUser(user.id());
        saveAuthentication(currentUser, request, response);
        return currentUser;
    }

    private void saveAuthentication(
        LuanLHCE191099_UserPrincipal user,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.role());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
