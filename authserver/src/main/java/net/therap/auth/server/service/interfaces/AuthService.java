package net.therap.auth.server.service.interfaces;

import net.therap.auth.server.dto.DeleteRequest;
import net.therap.auth.server.dto.JwtResponse;
import net.therap.auth.server.dto.LoginRequest;
import net.therap.auth.server.dto.RegisterRequest;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    JwtResponse login(LoginRequest request);
    
    JwtResponse refreshToken(String refreshToken);
    
    JwtResponse verifyEmail(String token);
    
    JwtResponse delete(DeleteRequest request);
}
