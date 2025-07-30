package net.therap.secureFileServer.advice;

import net.therap.secureFileServer.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author avidewan
 * @since 7/22/25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFound(FileNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<?> handleEmptyFile(EmptyFileException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Empty File", ex.getMessage());
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<?> handleFileSizeExceeded(FileSizeExceededException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "File Too Large", ex.getMessage());
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<?> handleUnsupportedFileType(UnsupportedFileTypeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Unsupported File Type", ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large", "Uploaded file exceeds the configured maximum upload size.");
    }

    @ExceptionHandler(MaliciousFileDetectedException.class)
    public ResponseEntity<?> handleMalware(MaliciousFileDetectedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malicious File", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", status.value(),
                        "error", error,
                        "message", message
                ));
    }
}