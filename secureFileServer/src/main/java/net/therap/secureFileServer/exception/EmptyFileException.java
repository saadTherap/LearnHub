package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/23/25
 */
public class EmptyFileException extends RuntimeException {

    public EmptyFileException(String message) {
        super(message);
    }
}