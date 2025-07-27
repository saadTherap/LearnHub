package net.therap.service;

import lombok.RequiredArgsConstructor;
import net.therap.dto.JwtResponse;
import net.therap.dto.LoginRequest;
import net.therap.dto.RegisterRequest;
import net.therap.entity.User;
import net.therap.exception.UserExistenceException;
import net.therap.respository.UserRepository;
import net.therap.service.interfaces.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static net.therap.util.ErrorMessages.*;
import static net.therap.util.ServiceUtils.toSystemFormatUserRole;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
public abstract class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRespository;
    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        if (userRespository.existsByEmail(request.getEmail())) {
            throw new UserExistenceException(EXIST_USER_ERROR);
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(toSystemFormatUserRole(request.getRole()));
        userRespository.save(user);
        
        return generateTokenPair(user.getEmail());
    }
    
    @Override
    public JwtResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return generateTokenPair(request.getEmail());
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        
        if (!jwtService.isValid(refreshToken, username)) {
            throw new RuntimeException(REFRESH_TOKEN_ERROR);
        }
        
        String access = jwtService.generateAccessToken(username);
        
        return new JwtResponse(access, refreshToken);
    }
    
    private JwtResponse generateTokenPair(String email) {
        String access = jwtService.generateAccessToken(email);
        String refresh = jwtService.generateRefreshToken(email);
        
        return new JwtResponse(access, refresh);
    }
}