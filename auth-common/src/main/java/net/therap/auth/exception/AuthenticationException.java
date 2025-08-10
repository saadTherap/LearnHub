package net.therap.auth.exception;

/**
 * @author apurboturjo
 * @since 8/4/25
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}