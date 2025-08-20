package net.therap.auth.server.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Data
public class ErrorResponse implements Serializable {
    
    private LocalDateTime timestamp;
    
    private String message;
    
    private String error;
    
    private Map<String, String> formErrors;
}