package net.therap.handler;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Data
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    
    private int status;
    
    private String message;
    
    private Map<String, String> errors;
}