package fu.edu.sba301.a2.exception;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class LuanLHCE191099_ApiExceptionHandler {
    @ExceptionHandler(LuanLHCE191099_ApiException.class)
    ResponseEntity<?> business(LuanLHCE191099_ApiException ex) { return ResponseEntity.status(ex.status).body(Map.of("message", ex.getMessage())); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("message", "Please check the entered information.", "errors", errors));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<?> badInput(Exception ex) { return ResponseEntity.badRequest().body(Map.of("message", "Invalid request data or field type.")); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> constraint(DataIntegrityViolationException ex) { return ResponseEntity.status(409).body(Map.of("message", "The record conflicts with existing or related data.")); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> forbidden(AccessDeniedException ex) { return ResponseEntity.status(403).body(Map.of("message", "You do not have permission for this operation.")); }
}
