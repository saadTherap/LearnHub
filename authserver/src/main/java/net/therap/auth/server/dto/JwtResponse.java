<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/dto/JwtResponse.java
package net.therap.auth.server.dto;
========
package net.therap.server.app.dto;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/dto/JwtResponse.java

import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Data
public class JwtResponse implements Serializable {
    
    private String accessToken;
    
    private String refreshToken;
    
    private String tokenType = "Bearer";
    
    private String message;

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public JwtResponse(String message) {
        this.message = message;
    }
}
