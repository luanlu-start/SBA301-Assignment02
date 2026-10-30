package fu.edu.sba301.a2.security;

import java.io.Serializable;

// Safe user identity stored in the server session and exposed by @AuthenticationPrincipal.
public record LuanLHCE191099_UserPrincipal(
    Long id,
    String email,
    String name,
    String role
) implements Serializable {}
