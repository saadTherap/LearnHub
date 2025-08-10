package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/30/25
 */
public class MaliciousFileDetectedException extends RuntimeException{

    public MaliciousFileDetectedException(String message) {
        super(message);
    }
}