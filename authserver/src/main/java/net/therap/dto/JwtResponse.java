package net.therap.dto;

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
    
    private String tokenMessage;

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public JwtResponse(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }
}
