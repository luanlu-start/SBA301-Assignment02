package fu.edu.sba301.a2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request used by Admin to create or update an account. */
public record LuanLHCE191099_AccountRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 100) String password,
    @NotNull @Min(1) @Max(2) Integer role
) {}
