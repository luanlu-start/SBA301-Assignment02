package fu.edu.sba301.a2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.service.LuanLHCE191099_AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LuanLHCE191099_JwtFilter extends OncePerRequestFilter {

  private final LuanLHCE191099_JwtService jwt;
  private final LuanLHCE191099_AuthService auth;
  private final ObjectMapper mapper;

  public LuanLHCE191099_JwtFilter(
    LuanLHCE191099_JwtService jwt,
    LuanLHCE191099_AuthService auth,
    ObjectMapper mapper
  ) {
    this.jwt = jwt;
    this.auth = auth;
    this.mapper = mapper;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain
  ) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      try {
        LuanLHCE191099_UserPrincipal user = auth.loadSubject(jwt.subject(header.substring(7)));
        var authentication = new UsernamePasswordAuthenticationToken(
          user,
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + user.role()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (JwtException | IllegalArgumentException | LuanLHCE191099_ApiException ex) {
        SecurityContextHolder.clearContext();
        response.setStatus(401);
        response.setContentType("application/json");
        mapper.writeValue(
          response.getOutputStream(),
          Map.of("message", "Invalid or expired login. Please sign in again.")
        );
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
