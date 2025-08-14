package net.therap.server.app.exception;

import net.therap.server.app.enums.ExceptionTypes;

/**
* @author apurboturjo
* @since 7/30/25
*/
public class AuthServerException extends RuntimeException {
    
    private final ExceptionTypes type;
    
    public AuthServerException() {
        this.type = null;
    }
    
    public AuthServerException(String message, ExceptionTypes type) {
        super(message);
        this.type = type;
    }
    
    public ExceptionTypes getType() {
        return type;
    }
}