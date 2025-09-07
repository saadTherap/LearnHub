package net.therap.auth.server.service;

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
import net.therap.auth.lib.provider.PublicKeyProvider;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.util.JwtProperties;
import net.therap.auth.server.util.JwtUtil;
import net.therap.auth.server.util.MessageUtil;
import net.therap.cache.support.HazelcastCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final PublicKeyProvider publicKeyProvider;
    private final HazelcastCacheService hazelcastCacheService;
    
    @Value("${jwt.private-key-path}")
    private String privateKeyPath;
    
    @Getter
    @Value("${jwt.key-id.default-key}")
    private String keyId;
    
    private RSAKey rsaKey;
    private JWSSigner signer;
    
    @PostConstruct
    public void loadKeys() throws Exception {
        RSAPrivateKey privateKey = JwtUtil.getPrivateKey(privateKeyPath);
        
        RSAPublicKey publicKey = publicKeyProvider.getPublicKey(keyId);
        
        this.rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
        
        this.signer = new RSASSASigner(privateKey);
        log.info("JWT keys loaded successfully with key ID: {}", keyId);
    }
    
    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.getAccessTokenExpiration(), "access");
    }
    
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtProperties.getRefreshTokenExpiration(), "refresh");
    }
    
    private String generateToken(User user, Duration expiration, String tokenType) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plus(expiration);
            
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("role", user.getRole().name())
                    .claim("tokenType", tokenType)
                    .issuer("learnhub-auth-server")
                    .audience("learnhub-clients")
                    .issueTime(Date.from(now))
                    .notBeforeTime(Date.from(now))
                    .expirationTime(Date.from(expiry))
                    .jwtID(UUID.randomUUID().toString())
                    .build();
            
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .build();
            
            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(signer);
            
            hazelcastCacheService.put("userEpoch", user.getId(), true);
            
            return signedJWT.serialize();
            
        } catch (Exception e) {
            log.error("Failed to generate {} token for user: {}", tokenType, user.getEmail(), e);
            throw new AuthServerException("Token generation failed");
        }
    }
}