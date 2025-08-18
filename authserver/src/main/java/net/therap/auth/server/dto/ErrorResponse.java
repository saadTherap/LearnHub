<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/dto/ErrorResponse.java
package net.therap.auth.server.dto;
========
package net.therap.server.app.dto;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/dto/ErrorResponse.java

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Data
public class ErrorResponse implements Serializable {
    
    private LocalDateTime timestamp;
    
    private String message;
    
    private Map<String, String> errors;
}