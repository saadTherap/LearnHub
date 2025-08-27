package net.therap.auth.server.dto;

import lombok.Data;

/**
 * @author apurboturjo
 * @since 8/19/25
 */
@Data
public class LoginResponse {
    
    private String email;
    
    private String role;
    
    private String accessToken;
    
    private String refreshToken;
}