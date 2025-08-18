package net.therap.auth.provider.util;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPublicKey;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class JwkUtils {
    
    public static String fromRsaPublicKey(RSAPublicKey publicKey, String keyId) {
        try {
            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .keyID(keyId)
                    .build();
            
            return rsaKey.toPublicJWK().toJSONString();
        
        } catch (Exception e) {
            log.error("Failed to convert RSA public key to JWK format", e);
            throw new RuntimeException("JWK conversion failed", e);
        }
    }
}