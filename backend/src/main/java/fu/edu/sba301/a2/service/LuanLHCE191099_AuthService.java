package fu.edu.sba301.a2.service;

import fu.edu.sba301.a2.dto.LuanLHCE191099_LoginRequest;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.LuanLHCE191099_AccountRepository;
import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_AuthService {

    private final LuanLHCE191099_AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public LuanLHCE191099_AuthService(
        LuanLHCE191099_AccountRepository accountRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Find the account, verify its password and return safe identity information.
    public LuanLHCE191099_UserPrincipal authenticate(LuanLHCE191099_LoginRequest input) {
        LuanLHCE191099_Account account = accountRepository
            .findByEmailIgnoreCase(input.email().trim())
            .orElseThrow(() ->
                new LuanLHCE191099_ApiException(401, "Email or password is incorrect.")
            );

        boolean correctPassword = passwordEncoder.matches(input.password(), account.getPassword());
        if (!correctPassword) {
            throw new LuanLHCE191099_ApiException(401, "Email or password is incorrect.");
        }
        return toPrincipal(account);
    }

    public LuanLHCE191099_UserPrincipal getCurrentUser(Long accountId) {
        LuanLHCE191099_Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(401, "Account no longer exists."));
        return toPrincipal(account);
    }

    private LuanLHCE191099_UserPrincipal toPrincipal(LuanLHCE191099_Account account) {
        String role = "STAFF";
        if (account.getRole() == 1) {
            role = "ADMIN";
        }
        return new LuanLHCE191099_UserPrincipal(
            account.getId(),
            account.getEmail(),
            account.getName(),
            role
        );
    }
}
