package fu.edu.sba301.a2.service;

import fu.edu.sba301.a2.dto.LuanLHCE191099_AccountInput;
import fu.edu.sba301.a2.dto.LuanLHCE191099_AccountResponse;
import fu.edu.sba301.a2.dto.LuanLHCE191099_ProfileInput;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.LuanLHCE191099_AccountRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_NewsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Controller -> AccountService -> AccountRepository -> SQL Server.
// Read methods use the default read-only transaction; write methods override it below.
@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_AccountService {

    private final LuanLHCE191099_AccountRepository accountRepository;
    private final LuanLHCE191099_NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;

    public LuanLHCE191099_AccountService(
        LuanLHCE191099_AccountRepository accountRepository,
        LuanLHCE191099_NewsRepository newsRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Read all accounts and return DTOs without their password hashes.
    public List<LuanLHCE191099_AccountResponse> getAllAccounts() {
        List<LuanLHCE191099_Account> accounts = accountRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_AccountResponse> responses = new ArrayList<>();

        for (LuanLHCE191099_Account account : accounts) {
            responses.add(toResponse(account));
        }

        return responses;
    }

    // Search runs in Java after retrieval; the current assignment does not use pagination.
    public List<LuanLHCE191099_AccountResponse> searchAccounts(String searchText) {
        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_Account> accounts = accountRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_AccountResponse> responses = new ArrayList<>();

        for (LuanLHCE191099_Account account : accounts) {
            String accountText = account.getName() + " " + account.getEmail();
            boolean matchesKeyword = accountText.toLowerCase(Locale.ROOT).contains(keyword);

            if (matchesKeyword) {
                responses.add(toResponse(account));
            }
        }

        return responses;
    }

    public LuanLHCE191099_AccountResponse getAccountById(Long accountId) {
        LuanLHCE191099_Account account = findAccountOrThrow(accountId);
        return toResponse(account);
    }

    @Transactional
    public LuanLHCE191099_AccountResponse createAccount(LuanLHCE191099_AccountInput input) {
        LuanLHCE191099_Account account = new LuanLHCE191099_Account();
        applyInput(account, input);

        // Flush executes pending SQL; the transaction commits after this service call succeeds.
        LuanLHCE191099_Account savedAccount = accountRepository.saveAndFlush(account);
        return toResponse(savedAccount);
    }

    @Transactional
    public LuanLHCE191099_AccountResponse updateAccount(
        Long accountId,
        LuanLHCE191099_AccountInput input
    ) {
        LuanLHCE191099_Account account = findAccountOrThrow(accountId);
        boolean removesAdminRole = account.getRole() == 1 && input.role() != 1;

        if (removesAdminRole && accountRepository.countByRole(1) <= 1) {
            throw new LuanLHCE191099_ApiException(409, "At least one Admin account must remain.");
        }

        applyInput(account, input);
        LuanLHCE191099_Account savedAccount = accountRepository.saveAndFlush(account);
        return toResponse(savedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long currentUserId) {
        LuanLHCE191099_Account account = findAccountOrThrow(accountId);

        if (accountId.equals(currentUserId)) {
            throw new LuanLHCE191099_ApiException(409, "You cannot delete your current account.");
        }
        if (newsRepository.existsByCreatedBy_Id(accountId)) {
            throw new LuanLHCE191099_ApiException(
                409,
                "This account has created news articles and cannot be deleted."
            );
        }
        if (account.getRole() == 1 && accountRepository.countByRole(1) <= 1) {
            throw new LuanLHCE191099_ApiException(409, "At least one Admin account must remain.");
        }

        // Editor references are optional. Author references were protected by the check above.
        newsRepository.clearUpdatedBy(accountId);
        accountRepository.delete(account);
        accountRepository.flush();
    }

    @Transactional
    public LuanLHCE191099_AccountResponse updateProfile(
        Long currentUserId,
        LuanLHCE191099_ProfileInput input
    ) {
        LuanLHCE191099_Account account = findAccountOrThrow(currentUserId);

        // The existing account supplies the role, so a profile edit cannot grant Admin access.
        LuanLHCE191099_AccountInput accountInput = new LuanLHCE191099_AccountInput(
            input.name(),
            input.email(),
            input.password(),
            account.getRole()
        );
        applyInput(account, accountInput);

        LuanLHCE191099_Account savedAccount = accountRepository.saveAndFlush(account);
        return toResponse(savedAccount);
    }

    private void applyInput(LuanLHCE191099_Account account, LuanLHCE191099_AccountInput input) {
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        Optional<LuanLHCE191099_Account> existingAccount = accountRepository.findByEmailIgnoreCase(
            email
        );

        if (existingAccount.isPresent()) {
            boolean belongsToAnotherAccount = !existingAccount
                .get()
                .getId()
                .equals(account.getId());
            if (belongsToAnotherAccount) {
                throw new LuanLHCE191099_ApiException(409, "Email already exists.");
            }
        }

        boolean hasNewPassword = input.password() != null && !input.password().isBlank();
        if (account.getId() == null && !hasNewPassword) {
            throw new LuanLHCE191099_ApiException(400, "Password is required.");
        }
        if (hasNewPassword) {
            if (input.password().length() < 6) {
                throw new LuanLHCE191099_ApiException(
                    400,
                    "Password must contain at least 6 characters."
                );
            }
            account.setPassword(passwordEncoder.encode(input.password()));
        }

        account.setName(input.name().trim());
        account.setEmail(email);
        account.setRole(input.role());
    }

    private LuanLHCE191099_Account findAccountOrThrow(Long accountId) {
        return accountRepository
            .findById(accountId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Account not found."));
    }

    private LuanLHCE191099_AccountResponse toResponse(LuanLHCE191099_Account account) {
        return new LuanLHCE191099_AccountResponse(
            account.getId(),
            account.getName(),
            account.getEmail(),
            account.getRole()
        );
    }
}
