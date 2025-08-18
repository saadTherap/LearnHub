package net.therap.auth.provider.service;

import lombok.RequiredArgsConstructor;
import net.therap.auth.provider.repository.PublicKeyRepository;
import net.therap.auth.provider.util.JwkUtils;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Service
@RequiredArgsConstructor
public class PublicKeyProviderService {
    
    private final PublicKeyRepository publicKeyRepository;
    
    public RSAPublicKey getPublicKey(String kid) {
        try {
            String base64Key = publicKeyRepository.findByKid(kid)
                    .orElseThrow(() -> new IllegalArgumentException("Public key not found for kid: " + kid))
                    .getPublicKey();
            
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            
            return (RSAPublicKey) kf.generatePublic(spec);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key from DB for kid: " + kid, e);
        }
    }
    
    public String getPublicKeyAsJWK(String kid) {
        RSAPublicKey publicKey = getPublicKey(kid);
        
        return JwkUtils.fromRsaPublicKey(publicKey, kid);
    }
}