package net.therap.learningProcessor.exception;

import lombok.Getter;

/**
 * @author avidewan
 * @since 8/3/25
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public ResourceNotFoundException(String messageKey, Object... args) {
        super(null, null, true, false);
        this.messageKey = messageKey;
        this.args = args;
    }
}