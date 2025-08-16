package net.therap.server.app.exception;

/**
* @author apurboturjo
* @since 7/30/25
*/
public class AuthServerException extends RuntimeException {
    
    public AuthServerException(String message) {
        super(message);
    }
    
    public AuthServerException(String message, Throwable cause) {
        super(message, cause);
    }
}