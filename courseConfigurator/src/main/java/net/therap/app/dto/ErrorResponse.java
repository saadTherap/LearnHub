package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author gazizafor
 * @since 29/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
    
    public ErrorResponse(HttpStatus httpStatus, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.details = null;
    }
    
    public ErrorResponse(HttpStatus httpStatus, String message, String path, Map<String, String> errors) {
        this(httpStatus, message, path);
        this.details = errors;
    }
}