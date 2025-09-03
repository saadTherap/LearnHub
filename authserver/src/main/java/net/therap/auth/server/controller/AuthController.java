package net.therap.auth.server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.therap.auth.server.dto.*;
import net.therap.auth.server.service.interfaces.AuthService;
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<JwtResponse> delete(@Valid @RequestBody DeleteRequest request) {
        JwtResponse response = authService.delete(request);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        JwtResponse response = authService.refreshToken(refreshRequest.getRefreshToken());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<JwtResponse> verifyEmail(@NotBlank @RequestParam String token) {
        JwtResponse response = authService.verifyEmail(token);
        
        return ResponseEntity.ok( response);
    }
    
    @PutMapping("/update-user")
    public ResponseEntity<JwtResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        JwtResponse response = authService.updateUser(updateUserRequest);
        
        return ResponseEntity.ok(response);
    }
}