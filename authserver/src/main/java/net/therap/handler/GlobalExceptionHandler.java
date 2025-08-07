package net.therap.handler;

import lombok.RequiredArgsConstructor;
import net.therap.auth.dto.ErrorResponse;
import net.therap.auth.exception.InvalidRoleSpecifiedException;
import net.therap.auth.exception.TokenVerificationException;
import net.therap.auth.exception.UserExistenceException;
import net.therap.auth.exception.UserPersistenceException;
import net.therap.auth.util.MessageUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static net.therap.auth.util.HandlerUtil.buildErrorResponse;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private static final String ERROR = "error";
    
    private final MessageUtil messageUtil;
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("msg.validation.failed"),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(UserExistenceException.class)
    public ResponseEntity<ErrorResponse> handleUserExistence(UserExistenceException ex) {
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("msg.user.exists"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(UserPersistenceException.class)
    public ResponseEntity<ErrorResponse> handleUserPersistence(UserPersistenceException ex) {
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("msg.user.persist.failed"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(TokenVerificationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationTokenVerification(TokenVerificationException ex) {
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("err.verify.token.failed"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(InvalidRoleSpecifiedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleSpecification(InvalidRoleSpecifiedException ex) {
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("err.user.role.invalid"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse response = buildErrorResponse(
                messageUtil.getMessage("err.user.bad-credentials"),
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}