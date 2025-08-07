package net.therap.auth.validator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.exception.AuthenticationException;
import net.therap.auth.helper.PublicKeyProvider;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
@Service
public class TokenValidator {
    
    private final PublicKeyProvider keyProvider;
    
    public TokenValidator(PublicKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }
    
    public JWTClaimsSet validate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();
            
            if (Objects.isNull(kid)) {
                throw new AuthenticationException("Missing key ID in token header");
            }
            
            RSAPublicKey publicKey = keyProvider.getPublicKey(kid);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            
            if (!signedJWT.verify(verifier)) {
                throw new AuthenticationException("Token signature verification failed");
            }
            
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            validateClaims(claims);
            
            return claims;
            
        } catch (ParseException e) {
            log.error("Failed to parse JWT token", e);
            throw new AuthenticationException("Invalid token format", e);
            
        } catch (JOSEException e) {
            log.error("JOSE processing error", e);
            throw new AuthenticationException("Token processing error", e);
        }
    }
    
    private void validateClaims(JWTClaimsSet claims) {
        Date now = Date.from(Instant.now());
        
        Date expirationTime = claims.getExpirationTime();
        if (Objects.nonNull(expirationTime) && expirationTime.before(now)) {
            throw new AuthenticationException("Token has expired");
        }
        
        Date notBeforeTime = claims.getNotBeforeTime();
        if (Objects.nonNull(notBeforeTime) && notBeforeTime.after(now)) {
            throw new AuthenticationException("Token not yet valid");
        }
        
        Date issuedAtTime = claims.getIssueTime();
        if (Objects.nonNull(issuedAtTime) && issuedAtTime.after(now)) {
            throw new AuthenticationException("Token issued in the future");
        }
    }
}