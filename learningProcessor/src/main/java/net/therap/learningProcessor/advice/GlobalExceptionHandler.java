package net.therap.learningProcessor.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.ErrorResponse;
import net.therap.learningProcessor.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author avidewan
 * @since 8/3/25
 */

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();

        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
            String message = messageSource.getMessage(fieldError, req.getLocale());
            errors.put(fieldError.getField(), message);
        }

        String globalMessage = messageSource.getMessage(
                "error.validation.failed",
                null,
                "Validation failed for one or more fields.",
                req.getLocale()
        );

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST,
                globalMessage,
                req.getRequestURI(),
                errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

        String errorMessage = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getArgs(),
                request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex,
                                                            HttpServletRequest request) {

        String errorMessage = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getArgs(),
                "Authentication required",
                request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex,
                                                         HttpServletRequest request) {

        String errorMessage = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getArgs(),
                "Access denied",
                request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignStatusException(FeignException ex, HttpServletRequest request) {
        String errorMessage = messageSource.getMessage(
                "error.dependent.service.notWorking",
                null,
                request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleFeignRetryableException(RetryableException ex, HttpServletRequest request) {
        String errorMessage = messageSource.getMessage(
                "error.dependent.service.notReachable",
                null,
                request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(RemoteFileServiceException.class)
    public ResponseEntity<ErrorResponse> handleRemoteFileServiceError(RemoteFileServiceException ex,
                                                                      HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.valueOf(ex.getStatusCode()),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {

        String globalMessage = messageSource.getMessage(
                "error.unexpected", null, "An unexpected error occurred", request.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}