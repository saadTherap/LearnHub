package net.therap.handler;

import lombok.RequiredArgsConstructor;
import net.therap.exception.UserExistenceException;
import net.therap.exception.UserPersistenceException;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static net.therap.util.HandlerUtil.buildErrorResponse;
import static net.therap.util.MessageUtil.getMessage;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private static final String ERROR = "error";
    
    private final MessageSource messageSource;
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        
        ErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                getMessage(messageSource, "msg.validation.failed"),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(UserExistenceException.class)
    public ResponseEntity<ErrorResponse> handleUserExistence(UserExistenceException ex) {
        ErrorResponse response = buildErrorResponse(
                HttpStatus.CONFLICT,
                getMessage(messageSource, "msg.user.exists"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(UserPersistenceException.class)
    public ResponseEntity<ErrorResponse> handleUserPersistence(UserPersistenceException ex) {
        ErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getMessage(messageSource, "msg.user.persist.failed"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}