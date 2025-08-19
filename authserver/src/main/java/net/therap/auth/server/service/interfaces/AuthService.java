package net.therap.auth.server.service.interfaces;

import net.therap.auth.server.dto.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    JwtResponse refreshToken(String refreshToken);
    
    JwtResponse verifyEmail(String token);
    
    JwtResponse delete(DeleteRequest request);
}
