package net.therap.auth.helper;

import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.client.PublicKeyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

//    @PostConstruct
//    public void initializeCache() {
//        RSAPublicKey publicKey = fetchFromDev();
//
//        CachedKey cachedKey = new CachedKey(publicKey);
//
//        keyCache.put("learnhub", cachedKey);
//
//        log.info("Initialized key cache with dev key.");
//    }

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

    private RSAPublicKey fetchFromDev() {
        String jwkJson = "{\"kty\":\"RSA\",\"e\":\"AQAB\",\"kid\":\"learnhub\",\"n\":\"rrE1uZXwj-sKPWTA_3U7yp28Lg8VsY1D6adUyTE3C1UiYBmbTqPLGrCz9LhZqKauIIn5-a6iaxCuMfAbVwkWZ2mKlOw2xB1wWt_aAOZoP9RFgpfzoC5qgQk0yUvnKqyTYHGWsyyrN1gPrCDKzetPRsJRykg9J1r7_cpa6y1sK4ro8xpx07zj7578lXevdfnEZDD50_qTXu0cz2iSOcfPQ8IWJaK-EXopmSxf_kN0orXsed25ErhiqhaFCYPEZnl6RP3zW9KiwEiPq1U6RNqX4eShXkDgc1L3c7rF67laurz_c09kWZAsReOTfLVreqePIn6tdFi3zqaz2znz3Oq3dw\"}";

        try {
            RSAKey rsaKey = RSAKey.parse(jwkJson);

            return rsaKey.toRSAPublicKey();

        } catch (Exception e) {
            log.error("Failed to parse hardcoded public key.", e);
            throw new RuntimeException("Failed to load dev public key", e);
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
    
    @Scheduled(fixedRate = 3600000)
    public void evictKeys(@Nullable String kid) {
        if (Objects.nonNull(kid) && keyCache.remove(kid) != null) {
            log.info("Evicted key from cache: {}", kid);
            
        } else if (Objects.isNull(kid)) {
            int size = keyCache.size();
            keyCache.clear();
            log.info("Cleared all {} keys from memory cache", size);
        }
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

