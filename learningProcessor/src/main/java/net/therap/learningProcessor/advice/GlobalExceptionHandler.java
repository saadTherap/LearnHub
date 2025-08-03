package net.therap.learningProcessor.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
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