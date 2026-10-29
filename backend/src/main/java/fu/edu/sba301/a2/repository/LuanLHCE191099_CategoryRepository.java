package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuanLHCE191099_CategoryRepository
  extends JpaRepository<LuanLHCE191099_Category, Long>
{
  boolean existsByParent_Id(Long id);
}
