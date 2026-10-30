package fu.edu.sba301.a2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Login request containing the submitted email and password. */
public record LuanLHCE191099_LoginInput(
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 100) String password
) {}
