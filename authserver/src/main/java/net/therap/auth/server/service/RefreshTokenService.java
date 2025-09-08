package net.therap.auth.server.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.exception.AuthenticationException;
import net.therap.auth.lib.validator.TokenValidator;
import net.therap.auth.server.entity.AuthKey;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.AuthKeyRepository;
import net.therap.auth.server.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final UserService userService;
    private final AuthKeyRepository authKeyRepository;
    private final TokenValidator tokenValidator;
    
    public RSAPublicKey getPublicKey(String kid) throws Exception {
        AuthKey publicKey = authKeyRepository.findByKid(kid)
                .orElseThrow(() -> new AuthServerException("Key not found with kid:" + kid));
        
        return JwtUtil.getRSAPublicKey(publicKey.getPublicKey());
    }
    
    public User validate(String refreshToken) {
        JWTClaimsSet claims = authServerSideValidation(refreshToken);
        
        String email = claims.getSubject();
        
        return userService.findByEmail(email);
    }
    
    private JWTClaimsSet authServerSideValidation(String token) {
        try {
            
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();
            
            if (Objects.isNull(kid)) {
                throw new AuthenticationException("Missing key ID in token header");
            }
            
            RSAPublicKey publicKey = getPublicKey(kid);
            
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            
            if (!signedJWT.verify(verifier)) {
                throw new AuthenticationException("Token is not issued from certified authority");
            }
            log.info("REFRESH token signature verified successfully");
            
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            tokenValidator.validateClaims(claims);
            
            log.info("AUTH-SERVER validated the REFRESH token successfully  =>{}", claims);
            log.info("-------------------------------------------------");
            
            return claims;
            
        } catch (ParseException e) {
            log.error("Failed to parse JWT token", e);
            throw new AuthenticationException("Invalid token format", e);
            
        } catch (Exception e) {
            log.error("JOSE processing error", e);
            throw new AuthenticationException("Token processing error", e);
        }
    }
}