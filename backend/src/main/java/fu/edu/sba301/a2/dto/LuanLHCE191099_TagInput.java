package fu.edu.sba301.a2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** One tag submitted inside a news request. */
public record LuanLHCE191099_TagInput(
    @NotBlank @Size(max = 50) String name,
    @Size(max = 250) String note
) {}
