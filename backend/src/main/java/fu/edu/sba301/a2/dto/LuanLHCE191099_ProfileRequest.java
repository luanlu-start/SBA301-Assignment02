package fu.edu.sba301.a2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Staff profile changes; the authenticated user supplies the ID and cannot change role here. */
public record LuanLHCE191099_ProfileRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 100) String password
) {}
