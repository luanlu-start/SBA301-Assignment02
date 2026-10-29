package fu.edu.sba301.a2.service;
import static fu.edu.sba301.a2.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Account;
import fu.edu.sba301.a2.repository.LuanLHCE191099_AccountRepository;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.security.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly=true)
public class LuanLHCE191099_AuthService {
    private final LuanLHCE191099_AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final LuanLHCE191099_JwtService jwt;
    public LuanLHCE191099_AuthService(LuanLHCE191099_AccountRepository accounts,PasswordEncoder encoder,LuanLHCE191099_JwtService jwt) { this.accounts=accounts;this.encoder=encoder;this.jwt=jwt; }
    public AuthResponse login(LoginInput input) {
        LuanLHCE191099_Account account=accounts.findByEmailIgnoreCase(input.email().trim()).orElseThrow(() -> new LuanLHCE191099_ApiException(401,"Email or password is incorrect."));
        if(!encoder.matches(input.password(),account.password)) throw new LuanLHCE191099_ApiException(401,"Email or password is incorrect.");
        return new AuthResponse(jwt.issue("account:"+account.id),principal(account));
    }
    public LuanLHCE191099_UserPrincipal loadSubject(String subject) {
        if(!subject.startsWith("account:")) throw new LuanLHCE191099_ApiException(401,"Invalid login.");
        return principal(accounts.findById(Long.parseLong(subject.substring(8))).orElseThrow(() -> new LuanLHCE191099_ApiException(401,"Account no longer exists.")));
    }
    public static LuanLHCE191099_UserPrincipal principal(LuanLHCE191099_Account a) { return new LuanLHCE191099_UserPrincipal(a.id,a.email,a.name,a.role==1?"ADMIN":"STAFF"); }
}
