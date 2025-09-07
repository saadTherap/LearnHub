package net.therap.auth.server.service.interfaces;

import net.therap.auth.server.dto.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    JwtResponse updateUser(UpdateUserRequest request);
    
    JwtResponse acquireUpdateAccessToken(String email);
    
    JwtResponse refreshToken(Long userId);
    
    JwtResponse verifyEmail(String token);
    
    JwtResponse delete(Long userId);
}