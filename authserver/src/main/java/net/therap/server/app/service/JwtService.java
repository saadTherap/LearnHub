package net.therap.server.app.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.server.app.entity.User;
import net.therap.server.app.exception.AuthServerException;
import net.therap.server.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static net.therap.server.app.enums.ExceptionTypes.LOGIN_ERROR_EXCEPTION;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final long ACCESS_EXPIRATION_MINUTES = 1440L;
    private static final long REFRESH_EXPIRATION_MINUTES = 60L * 24 * 7;

    private final UserService userService;

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @Getter
    @Value("${jwt.key-id.default-key}")
    private String keyId;

    private RSAKey rsaKey;
    private JWSSigner signer;

    @PostConstruct
    public void loadKeys() throws Exception {
        RSAPrivateKey privateKey = JwtUtil.getPrivateKey(privateKeyPath);
        RSAPublicKey publicKey = JwtUtil.getPublicKey(publicKeyPath);

        this.rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();

        this.signer = new RSASSASigner(privateKey);
        log.info("JWT keys loaded successfully with key ID: {}", keyId);
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_EXPIRATION_MINUTES, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_EXPIRATION_MINUTES, "refresh");
    }

    public String getPublicKeyAsJWK() {
        return rsaKey.toPublicJWK().toJSONString();
    }

    public String extractEmail(String token) {
        try {
            JWTClaimsSet claims = parseAndValidateToken(token);
            
            return claims.getSubject();
            
        } catch (Exception e) {
            log.error("Failed to extract email from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public Long extractUserId(String token) {
        try {
            JWTClaimsSet claims = parseAndValidateToken(token);
            
            return claims.getLongClaim("userId");
            
        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractRole(String token) {
        try {
            JWTClaimsSet claims = parseAndValidateToken(token);
            
            return claims.getStringClaim("role");
            
        } catch (Exception e) {
            log.error("Failed to extract role from token", e);
            
            throw new RuntimeException("Invalid token", e);
        }
    }

    public Date extractExpiration(String token) {
        try {
            JWTClaimsSet claims = parseAndValidateToken(token);
            
            return claims.getExpirationTime();
            
        } catch (Exception e) {
            log.error("Failed to extract expiration from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public boolean isValid(String token) {
        try {
            JWTClaimsSet claims = parseAndValidateToken(token);

            if (isExpired(token)) {
                log.debug("Token is expired");
                return false;
            }

            String email = claims.getSubject();
            if (Objects.isNull(email) || email.trim().isEmpty()) {
                log.debug("Token has no subject (email)");
                return false;
            }

            User user = userService.findByEmail(email);
            if (Objects.isNull(user)) {
                log.debug("User not found for email: {}", email);
                return false;
            }

            if (!user.isEnabled()) {
                log.debug("User is disabled for email: {}", email);
                return false;
            }

            return true;

        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        
        } catch (Exception e) {
            log.debug("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }

    private JWTClaimsSet parseAndValidateToken(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);

        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        if (!signedJWT.verify(verifier)) {
            throw new AuthServerException("Token signature verification failed", LOGIN_ERROR_EXCEPTION);
        }

        return signedJWT.getJWTClaimsSet();
    }

    private String generateToken(User user, long expirationMinutes, String tokenType) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("role", user.getRole().name())
                    .claim("tokenType", tokenType)
                    .issuer("learnhub-auth-server")
                    .audience("learnhub-clients")
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            log.error("Failed to generate {} token for user: {}", tokenType, user.getEmail(), e);
            throw new AuthServerException("Token generation failed", LOGIN_ERROR_EXCEPTION);
        }
    }
}