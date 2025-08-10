package net.therap.controller;

import net.therap.service.JwtService;
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
@RequestMapping("/auth/pk")
public class PublicKeyController {
    
    private final JwtService jwtService;
    
    public PublicKeyController(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @GetMapping()
    public ResponseEntity<String> getPublicKey(@RequestParam("kid") String keyId) {
        System.out.println("In the method getPublicKey");

        if (!keyId.equals(jwtService.getKeyId())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(jwtService.getPublicKeyAsJWK());
    }
}