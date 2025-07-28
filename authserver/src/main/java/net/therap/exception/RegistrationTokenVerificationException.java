package net.therap.exception;

/**
* @author apurboturjo
* @since 7/30/25
*/
public class RegistrationTokenVerificationException extends RuntimeException {
    
    public RegistrationTokenVerificationException() {
    }
    
    public RegistrationTokenVerificationException(String message) {
        super(message);
    }
}