package net.therap.secureFileServer.exception;

/**
 * @author avidewan
 * @since 7/22/25
 */
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(Long id) {
        super("File with ID " + id + " not found.");
    }
}