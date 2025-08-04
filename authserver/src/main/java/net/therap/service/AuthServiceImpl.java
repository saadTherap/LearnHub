package net.therap.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.dto.JwtResponse;
import net.therap.dto.LoginRequest;
import net.therap.dto.RegisterRequest;
import net.therap.entity.User;
import net.therap.entity.VerificationToken;
import net.therap.exception.RegistrationTokenVerificationException;
import net.therap.respository.VerificationTokenRepository;
import net.therap.service.interfaces.AuthService;
import net.therap.service.interfaces.EmailService;
import net.therap.util.MessageUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static net.therap.util.JwtUtil.toSystemFormatUserRole;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
@Slf4j // Using Lombok for logging
public class AuthServiceImpl implements AuthService {
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authManager;
    
    private final JwtService jwtService;
    
    private final CustomUserDetailsService customUserDetailsService;
    
    private final EmailService emailService;
    
    private final VerificationTokenRepository verificationTokenRepository;
    
    private final MessageUtil messageUtil;
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(toSystemFormatUserRole(request.getRole()));
        user.setEnabled(false);
        
        User savedUser = customUserDetailsService.saveUser(user);
        
        generateAndSendVerificationToken(savedUser);
        
        return new JwtResponse(messageUtil.getMessage("reg.success.verify_pending"));
    }
    
    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        User user = getUser(authentication.getName());
        
        if (!user.isEnabled()) {
            throw new RegistrationTokenVerificationException(messageUtil.getMessage("err.user.not_enabled"));
        }
        
        return generateTokenPair(user.getId());
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        
        if (!jwtService.isValid(refreshToken, userDetails)) {
            throw new RegistrationTokenVerificationException(messageUtil.getMessage("err.refresh.token.invalid"));
        }
        
        User user = getUser(email);
        
        if (!user.isEnabled()) {
            throw new RegistrationTokenVerificationException(messageUtil.getMessage("err.user.not_enabled"));
        }
        
        String access = jwtService.generateAccessToken(user);
        
        return new JwtResponse(access, refreshToken);
    }
    
    
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RegistrationTokenVerificationException(
                        messageUtil.getMessage("err.verify.token.invalid"))
                );
        
        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            
            throw new RegistrationTokenVerificationException(
                    messageUtil.getMessage("err.verify.token.expired"));
        }
        
        User userToVerify = verificationToken.getUser();
        userToVerify.setEnabled(true);
        customUserDetailsService.updateUser(userToVerify);
        
        verificationTokenRepository.delete(verificationToken);
    }
    
    private void generateAndSendVerificationToken(User user) {
        verificationTokenRepository.deleteByUser(user);
        
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);
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