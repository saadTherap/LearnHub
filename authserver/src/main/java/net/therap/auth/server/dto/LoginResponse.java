package net.therap.auth.server.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 8/19/25
 */
@Data
public class LoginResponse implements Serializable {
    
    private String email;
    
    private String role;
    
    private String accessToken;
    
    private String refreshToken;
}