package net.therap.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.dto.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final MessageSource messageSource;
    
    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Locale currentLocale = LocaleContextHolder.getLocale();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String resolvedMessage = resolveValidationMessage(error, currentLocale);
            errors.put(error.getField(), resolvedMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.validation.failed", null, currentLocale),
                                                        request.getRequestURI(), errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    private String resolveValidationMessage(FieldError error, Locale locale) {
        for (String code : Objects.requireNonNull(error.getCodes())) {
            try {
                return messageSource.getMessage(code, error.getArguments(), locale);
            } catch (NoSuchMessageException e) {
                logger.error("No message found for code: {}", code);
            }
        }
        
        String messageFromAnnotation = error.getDefaultMessage();
        if (messageFromAnnotation != null && messageFromAnnotation.startsWith("{") && messageFromAnnotation.endsWith("}")) {
            String messageKey = messageFromAnnotation.substring(1, messageFromAnnotation.length() - 1);
            try {
                return messageSource.getMessage(messageKey, error.getArguments(), locale);
            } catch (NoSuchMessageException e) {
                // Fallback to default message
            }
        }
        
        return error.getDefaultMessage();
    }
    
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException ex,
                                                                 HttpServletRequest request) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage() != null ?
                ex.getMessage() : messageSource.getMessage("error.resource.notfound", null, currentLocale),
                                                        request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(Exception ex,
                                                                      HttpServletRequest request) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        
        String errorMessageKey = "error.database.conflict";
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        
        if (ex instanceof IllegalArgumentException) {
            errorMessageKey = "error.bad.request";
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus,
                messageSource.getMessage(errorMessageKey, null, currentLocale),
                request.getRequestURI()
        );
        
        logger.error("Data integrity or illegal argument error: {}", ex.getMessage(), ex);
        
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage(
                "error.unexpected", null, currentLocale),
                                                        request.getRequestURI());
        logger.error("Unexpected runtime error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}