package net.therap.auth.service.interfaces;

import net.therap.auth.dto.JwtResponse;
import net.therap.auth.dto.LoginRequest;
import net.therap.auth.dto.RegisterRequest;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    JwtResponse login(LoginRequest request);
    
    JwtResponse refreshToken(String refreshToken);
    
    void verifyEmail(String token);
}
