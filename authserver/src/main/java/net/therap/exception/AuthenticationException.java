package net.therap.exception;

/**
* @author apurboturjo
* @since 7/30/25
*/
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException() {
    }
    
    public AuthenticationException(String message) {
        super(message);
    }
}