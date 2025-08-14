package net.therap.auth.lib.dto;

import lombok.Data;

/**
 * @author apurboturjo
 * @since 8/4/25
 */
@Data
public class TokenResponseDto {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String tokenMessage;
}