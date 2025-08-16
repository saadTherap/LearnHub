package net.therap.secureFileServer.advice;

import lombok.RequiredArgsConstructor;
import net.therap.secureFileServer.exception.*;
import net.therap.secureFileServer.util.MessageUtil;
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
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFound(FileNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                messageUtil.getMessage("error.file-not-found.title"),
                ex.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<?> handleEmptyFile(EmptyFileException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageUtil.getMessage("error.empty-file.title"),
                ex.getMessage());
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<?> handleFileSizeExceeded(FileSizeExceededException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageUtil.getMessage("error.file-too-large.title"),
                ex.getMessage());
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<?> handleUnsupportedFileType(UnsupportedFileTypeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageUtil.getMessage("error.unsupported-file-type.title"),
                ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE,
                messageUtil.getMessage("error.file-too-large.title"),
                messageUtil.getMessage("error.file-too-large.message"));
    }

    @ExceptionHandler(MaliciousFileDetectedException.class)
    public ResponseEntity<?> handleMalware(MaliciousFileDetectedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                messageUtil.getMessage("error.malicious-file.title"),
                ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<?> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                messageUtil.getMessage("error.service-unavailable.title"),
                messageUtil.getMessage("error.service-unavailable.message"));
    }

    @ExceptionHandler(FileAccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(FileAccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                messageUtil.getMessage("error.access-denied.title"),
                ex.getMessage());
    }

    @ExceptionHandler(InvalidFileSignatureException.class)
    public ResponseEntity<?> handleInvalidSignature(InvalidFileSignatureException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                messageUtil.getMessage("error.invalid-signature.title"),
                ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                messageUtil.getMessage("error.internal-server-error.title"),
                ex.getMessage());
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