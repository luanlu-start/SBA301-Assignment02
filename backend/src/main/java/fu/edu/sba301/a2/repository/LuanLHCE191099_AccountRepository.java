package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Persistence interface: Spring Data generates the implementation at runtime.
// JpaRepository<Account, Long> supplies CRUD methods such as findById, findAll, saveAndFlush and delete.
// Method names below describe additional queries; they use Java property names rather than SQL column names.
public interface LuanLHCE191099_AccountRepository
    extends JpaRepository<LuanLHCE191099_Account, Long>
{
    // An Optional models the no-account-found case explicitly; the service decides which HTTP/business error to raise.
    Optional<LuanLHCE191099_Account> findByEmailIgnoreCase(String email);

    // Used to prevent removing or demoting the last Admin account.
    long countByRole(Integer role);
}
