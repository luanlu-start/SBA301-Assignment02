package fu.edu.sba301.a2.dto;

/** Safe account details returned to the frontend; no password is included. */
public record LuanLHCE191099_AccountResponse(Long id, String name, String email, Integer role) {}
