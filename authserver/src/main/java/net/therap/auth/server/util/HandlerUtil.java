<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/util/HandlerUtil.java
package net.therap.auth.server.util;

import net.therap.auth.server.dto.ErrorResponse;
========
package net.therap.server.app.util;

import net.therap.server.app.dto.ErrorResponse;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/util/HandlerUtil.java

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class HandlerUtil {
    
    public static ErrorResponse buildErrorResponse(String message, Map<String, String> errors) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setMessage(message);
        response.setErrors(errors);
        
        return response;
    }
}