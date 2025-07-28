package net.therap.util;

import net.therap.handler.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class HandlerUtil {
    
    public static ErrorResponse buildErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setMessage(message);
        response.setErrors(errors);
        
        return response;
    }
    
}