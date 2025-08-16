package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 8/14/25
 */
public class FileAccessDeniedException extends RuntimeException {

    public FileAccessDeniedException(String message) {
        super(message);
    }
}
