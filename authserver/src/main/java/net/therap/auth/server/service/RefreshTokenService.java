package net.therap.auth.server.service;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.client.PublicKeyClient;
import net.therap.auth.lib.validator.TokenValidator;
import net.therap.auth.server.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final UserService userService;
    private final TokenValidator tokenValidator;
    
    public User validate(String refreshToken) {
        JWTClaimsSet claims = tokenValidator.verifySignature(refreshToken);
        
        String email = claims.getSubject();
        
        return userService.findByEmail(email);
    }
}