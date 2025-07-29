package net.therap.exception;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
public class RegistrationTokenVerificationException extends RuntimeException {
    
    public RegistrationTokenVerificationException() {
    }
    
    public RegistrationTokenVerificationException(String message) {
        super(message);
    }
    
    public RegistrationTokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}