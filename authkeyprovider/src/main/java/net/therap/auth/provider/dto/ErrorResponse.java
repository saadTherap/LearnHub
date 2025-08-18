package net.therap.auth.provider.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 14/8/25
 */
@Data
public class ErrorResponse implements Serializable {
    
    private LocalDateTime timestamp;
    
    private String message;
    
    private Map<String, String> errors;
}