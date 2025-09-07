package net.therap.auth.provider.util;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static net.therap.auth.provider.util.Constants.ENC_ALGO;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class JwkUtils {
    
    public static String RsaToJwkFormat(RSAPublicKey publicKey, String keyId) {
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
    
    public static RSAPublicKey getRSAPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        
        return (RSAPublicKey) KeyFactory.getInstance(ENC_ALGO).generatePublic(spec);
    }
}