package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/23/25
 */
public class FileSizeExceededException extends RuntimeException {

    public FileSizeExceededException(String message) {
        super(message);
    }
}