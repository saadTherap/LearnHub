package net.therap.auth.provider.controller;

import net.therap.auth.provider.service.PublicKeyProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@RestController
@RequestMapping("/keys")
public class PublicKeyController {
    
    private final PublicKeyProviderService publicKeyProviderService;
    
    public PublicKeyController(PublicKeyProviderService publicKeyProviderService) {
        this.publicKeyProviderService = publicKeyProviderService;
    }
    
    @GetMapping("/pk")
    public ResponseEntity<String> getPublicKey(@RequestParam("kid") String keyId) {
        String jwk = publicKeyProviderService.getPublicKeyAsJWK(keyId);
        
        return ResponseEntity.ok(jwk);
    }
}