package net.therap.server.app.exception;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
public class UserExistenceException extends RuntimeException {
    
    public UserExistenceException(String message) {
        super(message);
    }
    
    public UserExistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}