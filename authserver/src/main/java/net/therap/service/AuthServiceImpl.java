package net.therap.service;

import net.therap.dto.JwtResponse;
import net.therap.dto.LoginRequest;
import net.therap.dto.RegisterRequest;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
public class AuthServiceImpl implements AuthService {
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        return null;
    }
    
    @Override
    public JwtResponse login(LoginRequest request) {
        return null;
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        return null;
    }
}