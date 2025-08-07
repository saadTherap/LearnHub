package net.therap.auth.exception;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public class UserPersistenceException extends RuntimeException {
    
    public UserPersistenceException(String message) {
        super(message);
    }
    
    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}