package net.therap.auth.server.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.dto.ErrorResponse;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.util.MessageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static net.therap.auth.server.util.HandlerUtil.buildErrorResponse;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private static final String ERROR = "error";
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        
        ErrorResponse response = buildErrorResponse(
                MessageUtil.getMessage("msg.validation.failed"),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(AuthServerException.class)
    public ResponseEntity<ErrorResponse> handleAuthServerException(AuthServerException ex) {
        log.error("Caught AuthServerException: {}", ex.getMessage());
        
        ErrorResponse response = buildErrorResponse(
                "Auth Server end error.",
                Map.of(ERROR, ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        
        String errorMessage = MessageUtil.getMessage("app.global.error");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}