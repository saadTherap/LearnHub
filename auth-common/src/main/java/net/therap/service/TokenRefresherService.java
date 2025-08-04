package net.therap.service;

import lombok.extern.slf4j.Slf4j;
import net.therap.client.TokenClient;
import net.therap.dto.RefreshTokenRequestDto;
import net.therap.dto.TokenResponseDto;
import net.therap.exception.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/4/25
 */
@Slf4j
@Service
public class TokenRefresherService {
    
    private final TokenClient tokenClient;
    
    public TokenRefresherService(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }
    
    public String refresh(String refreshToken) {
        try {
            TokenResponseDto responseDto = tokenClient.refreshToken(new RefreshTokenRequestDto(refreshToken));
            
            if (Objects.isNull(responseDto) || Objects.isNull(responseDto.getAccessToken())) {
                throw new AuthenticationException("Invalid token response");
            }
            
            return responseDto.getAccessToken();
            
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new AuthenticationException("Token refresh failed", e);
        }
    }
}