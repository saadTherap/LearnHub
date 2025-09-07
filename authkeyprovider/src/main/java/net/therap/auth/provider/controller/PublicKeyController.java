package net.therap.auth.provider.controller;

import lombok.RequiredArgsConstructor;
import net.therap.auth.provider.service.PublicKeyProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
public class PublicKeyController {
    
    private final String KEYS = "keys";
    private final PublicKeyProviderService publicKeyProviderService;
    
    @GetMapping("/pk")
    public ResponseEntity<String> getPublicKey(@RequestParam("kid") String kid) {
        String jwk = publicKeyProviderService.getPublicKeyAsJWK(kid);
        
        return ResponseEntity.ok(jwk);
    }
    
    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, Object>> getJwks() {
        return ResponseEntity.ok(Map.of(
                KEYS, publicKeyProviderService.getAllActivePublicKeysAsJWK()
        ));
    }
}