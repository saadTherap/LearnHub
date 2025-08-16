package net.therap.server.app.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.server.app.dto.DeleteRequest;
import net.therap.server.app.dto.JwtResponse;
import net.therap.server.app.dto.LoginRequest;
import net.therap.server.app.dto.RegisterRequest;
import net.therap.server.app.entity.User;
import net.therap.server.app.entity.VerificationToken;
import net.therap.server.app.exception.AuthServerException;
import net.therap.server.app.respository.VerificationTokenRepository;
import net.therap.server.app.service.interfaces.AuthService;
import net.therap.server.app.util.MessageUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static net.therap.server.app.util.JwtUtil.toSystemFormatUserRole;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(toSystemFormatUserRole(request.getRole()));
        user.setEnabled(false);
        
        User savedUser = userService.saveUser(user);
        
        verificationTokenService.generateAndSendVerificationToken(savedUser);
        
        return new JwtResponse(MessageUtil.getMessage("ok.user.registered.verify.pending"));
    }
    
    @Override
    public JwtResponse login(LoginRequest request) {
        User user = authenticateUser(request.getEmail(), request.getPassword());
        
        if (!user.isEnabled()) {
            throw new AuthServerException(MessageUtil.getMessage("err.user.not.enabled"));
        }
        
        return generateTokenPair(user.getId());
    }
    
    @Override
    public JwtResponse delete(DeleteRequest request) {
        if (!jwtService.isValid(request.getAccessToken())) {
            throw new AuthServerException(MessageUtil.getMessage("err.token.access.invalid"));
        }
        
        Long userId = jwtService.extractUserId(request.getAccessToken());
        
        userService.deleteById(userId);
        
        return new JwtResponse(MessageUtil.getMessage("ok.user.deleted"));
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        
        if (!jwtService.isValid(refreshToken)) {
            throw new AuthServerException(MessageUtil.getMessage("err.token.refresh.invalid"));
        }
        
        User user = getUser(email);
        
        if (!user.isEnabled()) {
            throw new AuthServerException(MessageUtil.getMessage("err.user.not.enabled"));
        }
        
        String access = jwtService.generateAccessToken(user);
        
        return new JwtResponse(access, refreshToken);
    }
    
    @Transactional
    public JwtResponse verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthServerException(MessageUtil.getMessage("err.token.verify.invalid")));
        
        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            
            throw new AuthServerException(MessageUtil.getMessage("err.token.verify.expired"));
        }
        
        User userToVerify = verificationToken.getUser();
        userToVerify.setEnabled(true);
        userService.updateUser(userToVerify);
        
        verificationTokenRepository.delete(verificationToken);
        
        return new JwtResponse(MessageUtil.getMessage("ok.email.verified"));
    }
    
    private User authenticateUser(String email, String password) {
        User user = userService.findByEmail(email);
        
        if (Objects.isNull(user)) {
            throw new AuthServerException(MessageUtil.getMessage("err.auth.invalid.credentials"));
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthServerException(MessageUtil.getMessage("err.auth.invalid.credentials"));
        }
        
        return user;
    }
    
    private JwtResponse generateTokenPair(Long userId) {
        User user = getUser(userId);
        
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        
        return new JwtResponse(access, refresh);
    }
    
    private User getUser(String email) {
        return userService.findByEmail(email);
    }
    
    private User getUser(Long userId) {
        return userService.findById(userId);
    }
}