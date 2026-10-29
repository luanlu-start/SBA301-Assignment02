package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuanLHCE191099_AccountRepository
  extends JpaRepository<LuanLHCE191099_Account, Long>
{
  Optional<LuanLHCE191099_Account> findByEmailIgnoreCase(String email);
  long countByRole(Integer role);
}
