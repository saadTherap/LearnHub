package net.therap.service;

import lombok.RequiredArgsConstructor;
import net.therap.dto.JwtResponse;
import net.therap.dto.LoginRequest;
import net.therap.dto.RegisterRequest;
import net.therap.entity.User;
import net.therap.service.interfaces.AuthService;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static net.therap.util.MessageUtils.getMessage;
import static net.therap.util.ServiceUtils.toSystemFormatUserRole;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
public abstract class AuthServiceImpl implements AuthService {
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authManager;
    
    private final JwtService jwtService;
    
    private final CustomUserDetailsService customUserDetailsService;
    
    private final MessageSource messageSource;
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(toSystemFormatUserRole(request.getRole()));
        customUserDetailsService.saveUser(user);
        
        return generateTokenPair(user.getId());
    }
    
    @Override
    public JwtResponse login(LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        User user = getUser(request.getEmail());
        
        return generateTokenPair(user.getId());
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        
        if (!jwtService.isValid(refreshToken, userId)) {
            throw new RuntimeException(getMessage(messageSource, "err.refresh.token.invalid"));
        }
        
        User user = getUser(userId);
        
        String access = jwtService.generateAccessToken(user);
        
        return new JwtResponse(access, refreshToken);
    }
    
    private JwtResponse generateTokenPair(Long userId) {
        User user = getUser(userId);
        
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        
        return new JwtResponse(access, refresh);
    }
    
    private User getUser(String email) {
        return customUserDetailsService.findByEmail(email);
    }
    
    private User getUser(Long userId) {
        return customUserDetailsService.findById(userId);
    }
}