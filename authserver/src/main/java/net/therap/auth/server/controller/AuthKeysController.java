package net.therap.auth.server.controller;

import lombok.RequiredArgsConstructor;
import net.therap.auth.server.entity.AuthKey;
import net.therap.auth.server.service.AuthKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@RestController
@RequestMapping("/admin/keys")
@RequiredArgsConstructor
public class AuthKeysController {
    
    private final AuthKeyService keyService;
    
    @PostMapping("/generate")
    public ResponseEntity<AuthKey> generateKey() {
        return ResponseEntity.ok(keyService.generateAndSaveKeyPair());
    }
    
    @GetMapping("/active")
    public ResponseEntity<AuthKey> getActiveKey() {
        return ResponseEntity.ok(keyService.getActiveKey());
    }
    
    @PutMapping("/{kid}/retire")
    public ResponseEntity<Void> retireKey(@PathVariable String kid) {
        keyService.retireKey(kid);
        
        return ResponseEntity.ok().build();
    }
}