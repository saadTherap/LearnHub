package net.therap.auth.provider.service;

import lombok.RequiredArgsConstructor;
import net.therap.auth.provider.record.PublicKeyRecord;
import net.therap.auth.provider.repository.PublicKeyRepository;
import net.therap.auth.provider.util.JwkUtils;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.stream.Collectors;

import static net.therap.auth.provider.util.Constants.KEY_STATUS;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Service
@RequiredArgsConstructor
public class PublicKeyProviderService {
    
    private final PublicKeyRepository publicKeyRepository;
    
    private PublicKeyRecord getActiveRecord(String kid) {
        PublicKeyRecord record = publicKeyRepository.findByKid(kid)
                .orElseThrow(() -> new IllegalArgumentException("Public key not found for kid: " + kid));
        
        if (!record.isActive()) {
            throw new IllegalArgumentException("Public key is not active for kid: " + kid);
        }
        
        return record;
    }
    
    public RSAPublicKey getPublicKey(String kid) {
        try {
            String base64Key = getActiveRecord(kid).getPublicKey();
            
            return JwkUtils.getRSAPublicKey(base64Key);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key for kid: " + kid, e);
        }
    }
    
    public String getPublicKeyAsJWK(String kid) {
        RSAPublicKey publicKey = getPublicKey(kid);
        
        return JwkUtils.RsaToJwkFormat(publicKey, kid);
    }
    
    public List<String> getAllActivePublicKeysAsJWK() {
        return publicKeyRepository.findAllByStatus(KEY_STATUS)
                .stream()
                .map(record -> {
                    try {
                        RSAPublicKey pub = JwkUtils.getRSAPublicKey(record.getPublicKey());
                        
                        return JwkUtils.RsaToJwkFormat(pub, record.getKid());
                        
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to convert key to JWK for kid: " + record.getKid(), e);
                    }
                })
                .collect(Collectors.toList());
    }
}