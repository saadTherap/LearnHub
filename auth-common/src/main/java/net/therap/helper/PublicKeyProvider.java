package net.therap.helper;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import net.therap.client.PublicKeyClient;
import net.therap.config.AuthProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
@Service
public class PublicKeyProvider {
    
    private final PublicKeyClient publicKeyClient;
    private final Path keyDir;
    private final Map<String, RSAPublicKey> keyCache = new ConcurrentHashMap<>();
    
    public PublicKeyProvider(PublicKeyClient publicKeyClient, AuthProperties authProperties) {
        this.publicKeyClient = publicKeyClient;
        this.keyDir = Paths.get(authProperties.getPublicKeyDir());
    }
    
    public RSAPublicKey getPublicKey(String kid) {
        return keyCache.computeIfAbsent(kid, this::fetchPublicKey);
    }
    
    private RSAPublicKey fetchPublicKey(String kid) {
        try {
            Path keyPath = keyDir.resolve(kid + ".pem");
            
            if (Files.exists(keyPath)) {
                log.debug("Loading public key from cache: {}", kid);
                return parsePublicKeyFromFile(keyPath);
            }
            
            log.info("Fetching public key from server: {}", kid);
            return fetchAndCachePublicKey(kid, keyPath);
            
        } catch (Exception e) {
            log.error("Failed to fetch public key for kid: {}", kid, e);
            throw new RuntimeException("Failed to fetch public key for kid: " + kid, e);
        }
    }
    
    private RSAPublicKey fetchAndCachePublicKey(String kid, Path keyPath) throws Exception {
        ResponseEntity<String> response = publicKeyClient.getPublicKey(kid);
        
        if (!response.getStatusCode().is2xxSuccessful() || Objects.isNull(response.getBody())) {
            throw new RuntimeException("Failed to fetch public key from server");
        }
        
        String jwkJson = response.getBody();
        RSAKey rsaKey = RSAKey.parse(jwkJson);
        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
        
        Files.createDirectories(keyDir);
        Files.writeString(keyPath, jwkJson);
        
        return publicKey;
    }
    
    private RSAPublicKey parsePublicKeyFromFile(Path keyPath) throws Exception {
        String jwkJson = Files.readString(keyPath);
        return RSAKey.parse(jwkJson).toRSAPublicKey();
    }
}

