package fu.edu.sba301.a2.repository;

import fu.edu.sba301.a2.entity.LuanLHCE191099_NewsArticle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Persistence operations used by NewsService and the account/category deletion guards.
// Underscores explicitly mark relation traversal, for example CreatedBy_Id means createdBy.id.
public interface LuanLHCE191099_NewsRepository
    extends JpaRepository<LuanLHCE191099_NewsArticle, Long>
{
    boolean existsByCategory_Id(Long id);

    boolean existsByCreatedBy_Id(Long id);

    // Load one creator's articles with newest IDs first for the authenticated Staff history.
    List<LuanLHCE191099_NewsArticle> findByCreatedBy_IdOrderByIdDesc(Long id);

    // JPQL uses the entity class/property names, not NewsArticle/UpdatedByID SQL identifiers.
    // @Modifying marks the query as a write. It runs inside AccountService.delete's transaction
    // and clears only optional editor references; authors remain protected by the separate createdBy guard.
    @Modifying
    @Query("update LuanLHCE191099_NewsArticle n set n.updatedBy = null where n.updatedBy.id = :id")
    void clearUpdatedBy(@Param("id") Long id);
}
