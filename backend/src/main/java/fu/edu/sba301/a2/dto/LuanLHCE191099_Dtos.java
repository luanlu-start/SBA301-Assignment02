package fu.edu.sba301.a2.dto;

import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public final class LuanLHCE191099_Dtos {

  private LuanLHCE191099_Dtos() {}

  public record LoginInput(
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 100) String password
  ) {}

  public record AuthResponse(String token, LuanLHCE191099_UserPrincipal user) {}

  public record AccountInput(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 100) String password,
    @NotNull @Min(1) @Max(2) Integer role
  ) {}

  public record AccountResponse(Long id, String name, String email, Integer role) {}

  public record ProfileInput(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 100) String password
  ) {}

  public record CategoryInput(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 500) String description,
    @Positive Long parentId,
    @NotNull @Min(0) @Max(1) Integer status
  ) {}

  public record CategoryResponse(
    Long id,
    String name,
    String description,
    Long parentId,
    String parentName,
    Integer status
  ) {}

  public record TagInput(@NotBlank @Size(max = 50) String name, @Size(max = 250) String note) {}

  public record TagResponse(Long id, String name, String note) {}

  public record NewsInput(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 500) String headline,
    @NotBlank @Size(max = 10000) String content,
    @Size(max = 255) String source,
    @NotNull @Positive Long categoryId,
    @NotNull @Min(0) @Max(1) Integer status,
    @NotNull @Size(max = 20) List<@Valid TagInput> tags
  ) {}

  public record NewsResponse(
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
    List<TagResponse> tags
  ) {}
}
