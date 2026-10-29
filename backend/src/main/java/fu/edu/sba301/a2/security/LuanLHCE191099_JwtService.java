package fu.edu.sba301.a2.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class LuanLHCE191099_JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long seconds;
    public LuanLHCE191099_JwtService(@Value("${app.jwt.secret}") String secret, @Value("${spring.application.name}") String issuer,
                      @Value("${app.jwt.seconds:7200}") long seconds) {
        if (secret.startsWith("REPLACE_") || secret.length() < 32) throw new IllegalStateException("Set app.jwt.secret in application-local.properties (at least 32 characters).");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.seconds = seconds;
    }
    public String issue(String subject) {
        Instant now = Instant.now();
        return Jwts.builder().subject(subject).issuer(issuer).issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(seconds))).signWith(key).compact();
    }
    public String subject(String token) {
        return Jwts.parser().verifyWith(key).requireIssuer(issuer).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
