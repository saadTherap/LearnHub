package net.therap.learningProcessor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;


/**
 * @author avidewan
 * @since 7/31/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

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