package net.therap.app.exception;

/**
 * @author gazizafor
 * @since 14/8/25
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
}
