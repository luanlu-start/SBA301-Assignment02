package fu.edu.sba301.a2.exception;

// Expected business failure carrying an HTTP status and a readable message.
// It extends RuntimeException so a transactional service rolls back when the exception escapes.
public class LuanLHCE191099_ApiException extends RuntimeException {

    public final int status;

    public LuanLHCE191099_ApiException(int status, String message) {
        super(message);
        this.status = status;
    }
}
