package fu.edu.sba301.a2.dto;

import java.time.LocalDateTime;
import java.util.List;

/** News details with category, author, audit information and saved tags. */
public record LuanLHCE191099_NewsResponse(
    Long id,
    String title,
    String headline,
    String content,
    String source,
    Long categoryId,
    String categoryName,
    Integer status,
    Long createdById,
    String createdByName,
    String updatedByName,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate,
    List<LuanLHCE191099_TagResponse> tags
) {}
