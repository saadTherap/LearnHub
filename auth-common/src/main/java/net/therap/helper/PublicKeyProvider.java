package net.therap.helper;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.therap.client.PublicKeyClient;
import net.therap.config.AuthProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPublicKey;
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
@Getter
@Service
public class PublicKeyProvider {

    private final PublicKeyClient publicKeyClient;
    private final Map<String, CachedKey> keyCache = new ConcurrentHashMap<>();
    private final Duration cacheExpiry = Duration.ofHours(1);

    public PublicKeyProvider(PublicKeyClient publicKeyClient) {
        this.publicKeyClient = publicKeyClient;
    }

    public RSAPublicKey getPublicKey(String kid) {
        CachedKey cachedKey = keyCache.get(kid);

        if (cachedKey != null && !cachedKey.isExpired()) {
            log.debug("Using cached public key: {}", kid);
            return cachedKey.getPublicKey();
        }

        log.info("Fetching public key from server: {}", kid);
        RSAPublicKey publicKey = fetchFromServer(kid);
        cacheKey(kid, publicKey);

        return publicKey;
    }

    private RSAPublicKey fetchFromServer(String kid) {
        try {
            ResponseEntity<String> response = publicKeyClient.getPublicKey(kid);

            if (!response.getStatusCode().is2xxSuccessful() || Objects.isNull(response.getBody())) {
                throw new RuntimeException("Failed to fetch public key from server");
            }

            String jwkJson = response.getBody();
            RSAKey rsaKey = RSAKey.parse(jwkJson);
            return rsaKey.toRSAPublicKey();

        } catch (Exception e) {
            log.error("Failed to fetch public key from server for kid: {}", kid, e);
            throw new RuntimeException("Failed to fetch public key for kid: " + kid, e);
        }
    }

    private void cacheKey(String kid, RSAPublicKey publicKey) {
        keyCache.put(kid, new CachedKey(publicKey));
        log.debug("Cached public key in memory: {}", kid);
    }

    @Scheduled(fixedRate = 3600000)
    public void clearExpiredKeys() {

        keyCache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();

            if (expired) {
                log.debug("Removed expired key from cache: {}", entry.getKey());
            }

            return expired;
        });
    }

    public void evictKey(String kid) {
        if (keyCache.remove(kid) != null) {
            log.info("Manually evicted key from cache: {}", kid);
        }
    }

    public void clearAllKeys() {
        int size = keyCache.size();
        keyCache.clear();
        log.info("Cleared all {} keys from memory cache", size);
    }

    @Getter
    private static class CachedKey {

        private final RSAPublicKey publicKey;
        private final Instant cachedAt;

        public CachedKey(RSAPublicKey publicKey) {
            this.publicKey = publicKey;
            this.cachedAt = Instant.now();
        }

        public boolean isExpired() {
            return Duration.between(cachedAt, Instant.now()).compareTo(Duration.ofHours(1)) > 0;
        }
    }
}

