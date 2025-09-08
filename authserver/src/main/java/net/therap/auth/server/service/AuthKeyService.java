package net.therap.auth.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.entity.AuthKey;
import net.therap.auth.server.enums.KeyStatus;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.AuthKeyRepository;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;

import static net.therap.auth.server.util.Constants.ENC_ALGO;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthKeyService {
    
    private final AuthKeyRepository authKeyRepository;
    
    public AuthKey generateAndSaveKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ENC_ALGO);
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            
            RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
            
            String kid = "key-" + UUID.randomUUID();
            
            AuthKey authKey = AuthKey.builder()
                    .kid(kid)
                    .publicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                    .privateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()))
                    .status(KeyStatus.ACTIVE)
                    .build();
            
            log.info("Generated new RSA keypair with kid={}", kid);
            return authKeyRepository.save(authKey);
            
        } catch (Exception e) {
            throw new AuthServerException("Key generation failed", e);
        }
    }
    
    public AuthKey getActiveKey() {
        return authKeyRepository.findByStatus(KeyStatus.ACTIVE)
                .orElseThrow(() -> new AuthServerException("No active signing key found"));
    }
    
    public void retireKey(String kid) {
        AuthKey key = authKeyRepository.findByKid(kid)
                .orElseThrow(() -> new AuthServerException("Key not found"));
        
        key.setStatus(KeyStatus.RETIRED);
        authKeyRepository.save(key);
    }
}