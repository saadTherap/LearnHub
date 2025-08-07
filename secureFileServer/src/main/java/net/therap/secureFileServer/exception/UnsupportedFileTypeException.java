package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/23/25
 */
public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}