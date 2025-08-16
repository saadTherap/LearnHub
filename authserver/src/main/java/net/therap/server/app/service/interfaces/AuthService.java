package net.therap.server.app.service.interfaces;

import net.therap.server.app.dto.DeleteRequest;
import net.therap.server.app.dto.JwtResponse;
import net.therap.server.app.dto.LoginRequest;
import net.therap.server.app.dto.RegisterRequest;

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
