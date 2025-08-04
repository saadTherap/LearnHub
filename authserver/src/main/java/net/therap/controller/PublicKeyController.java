package net.therap.controller;

import lombok.RequiredArgsConstructor;
import net.therap.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PublicKeyController {
    
    @Value("${jwt.public-key-path}")
    private String publicKeyPath;
    
    @GetMapping("/pk")
    public ResponseEntity<String> getPublicKey() throws Exception {
        return ResponseEntity.ok().body(JwtUtil.getPublicKey(publicKeyPath));
    }
}