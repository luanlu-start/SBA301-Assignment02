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

// Central MVC error translation: controllers/services throw, then this advice creates consistent JSON.
// Filter-chain authentication errors occur earlier and are handled by SecurityConfig.
@RestControllerAdvice
public class LuanLHCE191099_ApiExceptionHandler {

    // Preserve the explicit business status, such as 404 not found or 409 conflicting references.
    @ExceptionHandler(LuanLHCE191099_ApiException.class)
    ResponseEntity<?> business(LuanLHCE191099_ApiException ex) {
        return ResponseEntity.status(ex.status).body(Map.of("message", ex.getMessage()));
    }

    // Collect per-field @Valid failures into { message, errors } for frontend errorText() and return 400.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
            Map.of("message", "Please check the entered information.", "errors", errors)
        );
    }

    // Malformed JSON, unexpected fields or wrong URL/body types produce a controlled 400 response.
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
    })
    ResponseEntity<?> badInput(Exception ex) {
        return ResponseEntity.badRequest().body(
            Map.of("message", "Invalid request data or field type.")
        );
    }

    // Database UNIQUE/FK violations become 409 without exposing SQL statements or internal database messages.
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> constraint(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(
            Map.of("message", "The record conflicts with existing or related data.")
        );
    }

    // Method-level authorization failure becomes 403; valid authentication alone does not grant every role.
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> forbidden(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(
            Map.of("message", "You do not have permission for this operation.")
        );
    }
}
