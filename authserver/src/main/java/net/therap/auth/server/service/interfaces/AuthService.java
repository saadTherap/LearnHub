package net.therap.auth.server.service.interfaces;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import net.therap.auth.server.dto.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    JwtResponse updateUser(UpdateUserRequest request);
    
    JwtResponse refreshToken(String refreshToken);
    
    JwtResponse verifyEmail(String token);
    
    JwtResponse delete(DeleteRequest request);
    
    JwtResponse verifyResetPassword(String email);
}