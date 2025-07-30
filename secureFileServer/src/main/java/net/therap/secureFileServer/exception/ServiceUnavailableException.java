package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/30/25
 */
public class ServiceUnavailableException extends RuntimeException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
