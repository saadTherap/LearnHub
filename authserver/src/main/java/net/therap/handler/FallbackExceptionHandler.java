package net.therap.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Order(2)
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice()
public class FallbackExceptionHandler {
    
    private final MessageSource messageSource;
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        
        String errorMessage = messageSource.getMessage("app.global.error", null, Locale.getDefault());
        
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}