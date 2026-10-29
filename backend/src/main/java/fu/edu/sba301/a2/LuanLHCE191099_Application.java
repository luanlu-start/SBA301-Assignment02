package fu.edu.sba301.a2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
@SpringBootApplication(exclude=UserDetailsServiceAutoConfiguration.class)
public class LuanLHCE191099_Application {
    public static void main(String[] args) { SpringApplication.run(LuanLHCE191099_Application.class, args); }
}
