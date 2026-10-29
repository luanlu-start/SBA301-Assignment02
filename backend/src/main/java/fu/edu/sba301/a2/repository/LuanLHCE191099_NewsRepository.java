package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_NewsArticle;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LuanLHCE191099_NewsRepository
  extends JpaRepository<LuanLHCE191099_NewsArticle, Long>
{
  boolean existsByCategory_Id(Long id);
  boolean existsByCreatedBy_Id(Long id);
  List<LuanLHCE191099_NewsArticle> findByCreatedBy_IdOrderByIdDesc(Long id);

  @Modifying
  @Query("update LuanLHCE191099_NewsArticle n set n.updatedBy = null where n.updatedBy.id = :id")
  void clearUpdatedBy(@Param("id") Long id);
}
