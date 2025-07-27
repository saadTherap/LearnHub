package net.therap.dto;

import lombok.Data;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Data
public class JwtResponse {
    
    private String accessToken;
    
    private String refreshToken;
    
    private String tokenType = "Bearer";
    
    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
