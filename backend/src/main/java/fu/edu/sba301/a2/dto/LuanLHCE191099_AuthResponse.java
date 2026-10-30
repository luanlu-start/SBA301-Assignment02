package fu.edu.sba301.a2.dto;

import fu.edu.sba301.a2.security.LuanLHCE191099_UserPrincipal;

/** Successful login returns user details; the browser stores the session cookie automatically. */
public record LuanLHCE191099_AuthResponse(LuanLHCE191099_UserPrincipal user) {}
