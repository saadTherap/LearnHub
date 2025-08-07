package net.therap.auth.client;

import net.therap.auth.dto.RefreshTokenRequestDto;
import net.therap.auth.dto.TokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Component
@FeignClient(name = "tokenClient", url = "https://server.local/authenticator/api/auth/refresh")
public interface TokenClient {
    
    @PostMapping(consumes = "application/json")
    TokenResponseDto refreshToken(@RequestBody RefreshTokenRequestDto requestDto);
}