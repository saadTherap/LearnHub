package net.therap.server.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.server.app.dto.JwtResponse;
import net.therap.server.app.dto.LoginRequest;
import net.therap.server.app.dto.RefreshRequest;
import net.therap.server.app.dto.RegisterRequest;
import net.therap.server.app.service.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author apurbo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        JwtResponse response = authService.refreshToken(refreshRequest.getRefreshToken());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        
        return ResponseEntity.ok( "Email verified successfully!");
    }
}
