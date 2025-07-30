package net.therap.dto;

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
    
    private String message;
    
    private Map<String, String> errors;
}