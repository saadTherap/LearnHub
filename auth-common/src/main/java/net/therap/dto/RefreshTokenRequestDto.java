package net.therap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author apurboturjo
 * @since 8/4/25
 */
@Data
@AllArgsConstructor
public class RefreshTokenRequestDto {
    
    private String refreshToken;
}