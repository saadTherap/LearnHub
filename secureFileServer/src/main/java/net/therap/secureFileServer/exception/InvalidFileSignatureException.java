package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 8/16/25
 */
public class InvalidFileSignatureException extends RuntimeException {

    public InvalidFileSignatureException(String message) {
        super(message);
    }
}