package net.therap.auth.server.util;

import net.therap.auth.server.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class HandlerUtil {
    
    public static ErrorResponse buildErrorResponse(String message, Map<String, String> errors) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setMessage(message);
        response.setErrors(errors);
        
        return response;
    }
}