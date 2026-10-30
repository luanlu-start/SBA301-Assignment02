package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data repository for Category; inherited CRUD requires no handwritten implementation class.
// Parent_Id navigates the parent relation to its ID, so this query detects existing child categories.
public interface LuanLHCE191099_CategoryRepository
    extends JpaRepository<LuanLHCE191099_Category, Long>
{
    boolean existsByParent_Id(Long id);
}
