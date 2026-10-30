package fu.edu.sba301.a2.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Editable news fields; author IDs and timestamps are assigned by the server. */
public record LuanLHCE191099_NewsRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 500) String headline,
    @NotBlank @Size(max = 10000) String content,
    @Size(max = 255) String source,
    @NotNull @Positive Long categoryId,
    @NotNull @Min(0) @Max(1) Integer status,
    @NotNull @Size(max = 20) List<@Valid LuanLHCE191099_TagRequest> tags
) {}
