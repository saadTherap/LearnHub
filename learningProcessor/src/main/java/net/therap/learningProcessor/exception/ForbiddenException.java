package net.therap.learningProcessor.exception;

import lombok.Getter;

/**
 * @author avidewan
 * @since 8/13/25
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public ForbiddenException(String messageKey, Object... args) {
        super(null, null, true, false);
        this.messageKey = messageKey;
        this.args = args;
    }
}