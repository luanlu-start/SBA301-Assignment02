package fu.edu.sba301.a2.dto;

/** Category details with flat parent ID/name fields for the frontend. */
public record LuanLHCE191099_CategoryResponse(
    Long id,
    String name,
    String description,
    Long parentId,
    String parentName,
    Integer status
) {}
