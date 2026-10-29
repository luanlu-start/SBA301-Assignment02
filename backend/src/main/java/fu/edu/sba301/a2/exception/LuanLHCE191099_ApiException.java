package fu.edu.sba301.a2.exception;

public class LuanLHCE191099_ApiException extends RuntimeException {

  public final int status;

  public LuanLHCE191099_ApiException(int status, String message) {
    super(message);
    this.status = status;
  }
}
