package fu.edu.sba301.a2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Category changes, including an optional parent ID and status 0 or 1. */
public record LuanLHCE191099_CategoryRequest(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 500) String description,
    @Positive Long parentId,
    @NotNull @Min(0) @Max(1) Integer status
) {}
