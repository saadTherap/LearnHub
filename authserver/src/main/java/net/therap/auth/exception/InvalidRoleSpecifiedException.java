package net.therap.auth.exception;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class InvalidRoleSpecifiedException extends RuntimeException {
    
    public InvalidRoleSpecifiedException() {
    }
    
    public InvalidRoleSpecifiedException(String message) {
        super(message);
    }
}