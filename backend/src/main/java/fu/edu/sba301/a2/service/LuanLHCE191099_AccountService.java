package fu.edu.sba301.a2.service;

import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.*;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_AccountService {

  private final LuanLHCE191099_AccountRepository accounts;
  private final LuanLHCE191099_NewsRepository news;
  private final PasswordEncoder encoder;

  public LuanLHCE191099_AccountService(
    LuanLHCE191099_AccountRepository accounts,
    LuanLHCE191099_NewsRepository news,
    PasswordEncoder encoder
  ) {
    this.accounts = accounts;
    this.news = news;
    this.encoder = encoder;
  }

  public List<AccountResponse> list(String q) {
    String keyword = q.trim().toLowerCase(Locale.ROOT);
    return accounts
      .findAll(Sort.by("id"))
      .stream()
      .filter(a -> (a.getName() + " " + a.getEmail()).toLowerCase(Locale.ROOT).contains(keyword))
      .map(LuanLHCE191099_AccountService::view)
      .toList();
  }

  public AccountResponse get(Long id) {
    return view(required(id));
  }

  @Transactional
  public AccountResponse create(AccountInput input) {
    LuanLHCE191099_Account a = new LuanLHCE191099_Account();
    apply(a, input);
    return view(accounts.saveAndFlush(a));
  }

  @Transactional
  public AccountResponse update(Long id, AccountInput input) {
    LuanLHCE191099_Account a = required(id);
    if (
      a.getRole() == 1 && input.role() != 1 && accounts.countByRole(1) <= 1
    ) throw new LuanLHCE191099_ApiException(409, "At least one Admin account must remain.");
    apply(a, input);
    return view(accounts.saveAndFlush(a));
  }

  @Transactional
  public void delete(Long id, Long actor) {
    LuanLHCE191099_Account a = required(id);
    if (id.equals(actor)) throw new LuanLHCE191099_ApiException(
      409,
      "You cannot delete your current account."
    );
    if (news.existsByCreatedBy_Id(id)) throw new LuanLHCE191099_ApiException(
      409,
      "This account has created news articles and cannot be deleted."
    );
    if (a.getRole() == 1 && accounts.countByRole(1) <= 1) throw new LuanLHCE191099_ApiException(
      409,
      "At least one Admin account must remain."
    );
    news.clearUpdatedBy(id);
    accounts.delete(a);
    accounts.flush();
  }

  @Transactional
  public AccountResponse profile(Long id, ProfileInput input) {
    LuanLHCE191099_Account a = required(id);
    apply(a, new AccountInput(input.name(), input.email(), input.password(), a.getRole()));
    return view(accounts.saveAndFlush(a));
  }

  private void apply(LuanLHCE191099_Account a, AccountInput input) {
    String email = input.email().trim().toLowerCase(Locale.ROOT);
    accounts
      .findByEmailIgnoreCase(email)
      .filter(other -> !other.getId().equals(a.getId()))
      .ifPresent(other -> {
        throw new LuanLHCE191099_ApiException(409, "Email already exists.");
      });
    if (
      a.getId() == null && (input.password() == null || input.password().isBlank())
    ) throw new LuanLHCE191099_ApiException(400, "Password is required.");
    if (input.password() != null && !input.password().isBlank()) {
      if (input.password().length() < 6) throw new LuanLHCE191099_ApiException(
        400,
        "Password must contain at least 6 characters."
      );
      a.setPassword(encoder.encode(input.password()));
    }
    a.setName(input.name().trim());
    a.setEmail(email);
    a.setRole(input.role());
  }

  private LuanLHCE191099_Account required(Long id) {
    return accounts
      .findById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Account not found."));
  }

  public static AccountResponse view(LuanLHCE191099_Account a) {
    return new AccountResponse(a.getId(), a.getName(), a.getEmail(), a.getRole());
  }
}
