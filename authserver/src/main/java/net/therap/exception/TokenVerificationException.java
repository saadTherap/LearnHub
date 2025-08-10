package net.therap.exception;

/**
* @author apurboturjo
* @since 7/30/25
*/
public class TokenVerificationException extends RuntimeException {
    
    public TokenVerificationException() {
    }
    
    public TokenVerificationException(String message) {
        super(message);
    }
}