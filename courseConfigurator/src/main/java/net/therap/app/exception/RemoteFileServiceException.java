package net.therap.app.exception;

import lombok.Getter;

/**
 * @author avidewan
 * @since 8/12/25
 */
@Getter
public class RemoteFileServiceException extends RuntimeException {

    private final int statusCode;
    private final String error;

    public RemoteFileServiceException(int statusCode,
                                      String error,
                                      String message) {

        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }
}