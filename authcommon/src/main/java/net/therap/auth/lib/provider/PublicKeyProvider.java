package net.therap.auth.lib.provider;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.client.PublicKeyClient;
import net.therap.auth.lib.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
@Component
public class PublicKeyProvider {
    
    private final PublicKeyClient publicKeyClient;
    private final Map<String, CachedKey> keyCache = new ConcurrentHashMap<>();
    private final Duration cacheExpiry = Duration.ofHours(1);
    
    @Autowired
    public PublicKeyProvider(@Nullable PublicKeyClient publicKeyClient) {
        this.publicKeyClient = publicKeyClient;
        log.info("PublicKeyProvider initialized with client: {}",
                Objects.nonNull(publicKeyClient) ? "present" : "not configured");
    }
    
    public RSAPublicKey getPublicKey(String kid) throws ParseException, JOSEException {
        log.debug("Fetching public key for kid: {}", kid);
        
        CachedKey cachedKey = keyCache.get(kid);
        if (Objects.nonNull(cachedKey) && !cachedKey.isExpired()) {
            log.debug("Returning cached public key for kid: {}", kid);
            return cachedKey.getPublicKey();
        }
        
        RSAPublicKey fetchedKey = fetchFromServer(kid);
        cacheKey(kid, fetchedKey);
        
        log.debug("Successfully fetched and cached public key for kid: {}", kid);
        return fetchedKey;
    }
    
    private RSAPublicKey fetchFromServer(String kid) throws JOSEException, ParseException {
        if (Objects.isNull(publicKeyClient)) {
            throw new AuthenticationException("PublicKeyClient is not configured. Cannot fetch public key for kid: " + kid);
        }
        
        log.debug("Fetching public key from server for kid: {}", kid);
        ResponseEntity<String> response = publicKeyClient.getPublicKey(kid);
        
        if (!response.getStatusCode().is2xxSuccessful() || Objects.isNull(response.getBody())) {
            throw new AuthenticationException("Failed to fetch public key for kid: " + kid +
                    ". Status: " + response.getStatusCode());
        }
        
        String jwkJson = response.getBody();
        RSAKey rsaKey = RSAKey.parse(jwkJson);
        
        return rsaKey.toRSAPublicKey();
    }
    
    private void cacheKey(String kid, RSAPublicKey publicKey) {
        keyCache.put(kid, new CachedKey(publicKey, cacheExpiry));
        log.debug("Cached public key for kid: {} with expiry: {}", kid, cacheExpiry);
    }
    
    @Getter
    static class CachedKey {
        private final RSAPublicKey publicKey;
        private final Instant expiresAt;
        
        CachedKey(RSAPublicKey publicKey, Duration ttl) {
            this.publicKey = publicKey;
            this.expiresAt = Instant.now().plus(ttl);
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}