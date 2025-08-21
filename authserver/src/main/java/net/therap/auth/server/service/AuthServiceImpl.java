package net.therap.auth.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.dto.*;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.entity.VerificationToken;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.VerificationTokenRepository;
import net.therap.auth.server.service.interfaces.AuthService;
import net.therap.auth.server.util.MessageUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static net.therap.auth.server.util.JwtUtil.toSystemFormatUserRole;

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
    private final RegistrationService registrationService;
    
    @Override
    public JwtResponse register(RegisterRequest request) {
        log.info("REGISTRATION request received from: {}", request.getEmail());
        
        log.info("Creating new user entity for email: {}", request.getEmail());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(toSystemFormatUserRole(request.getRole()));
        user.setEnabled(false);
        
        log.info("Saving user to database for email: {}", request.getEmail());
        User savedUser = userService.saveUser(user);
        log.info("User saved successfully with ID: {} for email: {}", savedUser.getId(), request.getEmail());
        
        log.info("Generating and sending verification token for user ID: {}", savedUser.getId());
        verificationTokenService.generateAndSendVerificationToken(savedUser);
        
        log.info("REGISTRATION completed. Sent the verification mail for email: {}", request.getEmail());
        
        return new JwtResponse(MessageUtil.getMessage("ok.user.registered.verify.pending"));
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("LOGIN request RECEIVED for email: {}", request.getEmail());
        
        log.info("Authenticating user: {}", request.getEmail());
        User user = authenticateUser(request.getEmail(), request.getPassword());
        log.info("User authentication successful for: {}", request.getEmail());
        
        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled user: {}", request.getEmail());
            throw new AuthServerException(MessageUtil.getMessage("err.user.not.enabled"));
        }
        
        log.info("Generating JWT token pair for user ID: {}", user.getId());
        JwtResponse jwt = generateTokenPair(user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setAccessToken(jwt.getAccessToken());
        response.setRefreshToken(jwt.getRefreshToken());
        
        log.info("LOGIN SUCCESSFUL for user: {}. Tokens generated and sent.", request.getEmail());
        
        return response;
    }
    
    @Override
    public JwtResponse delete(DeleteRequest request) {
        log.info("DELETE request received");
        
        log.info("Validating access token for delete operation");
        if (!jwtService.isValid(request.getAccessToken())) {
            log.warn("Invalid access token provided for delete operation");
            throw new AuthServerException(MessageUtil.getMessage("err.token.access.invalid"));
        }
        
        Long userId = jwtService.extractUserId(request.getAccessToken());
        log.info("Extracted user ID from token: {}", userId);
        
        log.info("Deleting user with ID: {}", userId);
        userService.deleteById(userId);
        log.info("User deleted successfully with ID: {}", userId);
        
        return new JwtResponse(MessageUtil.getMessage("ok.user.deleted"));
    }
    
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        log.info("REFRESH TOKEN request received");
        
        log.info("Extracting email from refresh token");
        String email = jwtService.extractEmail(refreshToken);
        log.info("Email extracted from refresh token: {}", email);
        
        log.info("Validating refresh token for user: {}", email);
        if (!jwtService.isValid(refreshToken)) {
            log.warn("Invalid refresh token provided for user: {}", email);
            throw new AuthServerException(MessageUtil.getMessage("err.token.refresh.invalid"));
        }
        
        log.info("Fetching user details for email: {}", email);
        User user = getUser(email);
        
        if (!user.isEnabled()) {
            log.warn("Token refresh attempt for disabled user: {}", email);
            throw new AuthServerException(MessageUtil.getMessage("err.user.not.enabled"));
        }
        
        log.info("Generating new access token for user: {}", email);
        String access = jwtService.generateAccessToken(user);
        log.info("New access token generated successfully for user: {}", email);
        
        return new JwtResponse(access, refreshToken);
    }
    
    @Transactional
    public JwtResponse verifyEmail(String token) {
        log.info("EMAIL VERIFICATION request received with token");
        
        log.info("Looking up verification token in database");
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Invalid verification token provided");
                    return new AuthServerException(MessageUtil.getMessage("err.token.verify.invalid"));
                });
        
        log.info("Verification token found for user ID: {}", verificationToken.getUser().getId());
        
        if (verificationToken.isExpired()) {
            log.warn("Expired verification token provided for user ID: {}", verificationToken.getUser().getId());
            verificationTokenRepository.delete(verificationToken);
            throw new AuthServerException(MessageUtil.getMessage("err.token.verify.expired"));
        }
        
        User userToVerify = verificationToken.getUser();
        log.info("Enabling user account for email: {}", userToVerify.getEmail());
        userToVerify.setEnabled(true);
        userService.updateUser(userToVerify);
        log.info("User account enabled successfully for email: {}", userToVerify.getEmail());
        
        log.info("Deleting used verification token for user: {}", userToVerify.getEmail());
        verificationTokenRepository.delete(verificationToken);
        
        if (userToVerify.getRole() == UserRole.STUDENT) {
            log.info("Sending student registration info for email: {}", userToVerify.getEmail());
            registrationService.sendStudentRegistrationInfo(userToVerify.getEmail());
            
        } else if (userToVerify.getRole() == UserRole.INSTRUCTOR) {
            log.info("Sending instructor registration info for email: {}", userToVerify.getEmail());
            registrationService.sendInstructorRegistrationInfo(userToVerify.getEmail());
        }
        
        log.info("EMAIL VERIFICATION completed successfully for email: {}", userToVerify.getEmail());
        
        return new JwtResponse(MessageUtil.getMessage("ok.email.verified"));
    }
    
    private User authenticateUser(String email, String password) {
        log.info("Starting authentication process for email: {}", email);
        
        log.info("Fetching user by email: {}", email);
        User user = userService.findByEmail(email);
        
        if (Objects.isNull(user)) {
            log.warn("User not found for email: {}", email);
            throw new AuthServerException(MessageUtil.getMessage("err.auth.invalid.credentials"));
        }
        
        log.info("User found, verifying password for email: {}", email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Password verification failed for email: {}", email);
            throw new AuthServerException(MessageUtil.getMessage("err.auth.invalid.credentials"));
        }
        
        log.info("Password verification successful for email: {}", email);
        return user;
    }
    
    private JwtResponse generateTokenPair(Long userId) {
        log.info("Generating token pair for user ID: {}", userId);
        
        User user = getUser(userId);
        
        log.info("Generating access token for user: {}", user.getEmail());
        String access = jwtService.generateAccessToken(user);
        
        log.info("Generating refresh token for user: {}", user.getEmail());
        String refresh = jwtService.generateRefreshToken(user);
        
        log.info("Token pair generated successfully for user: {}", user.getEmail());
        return new JwtResponse(access, refresh);
    }
    
    private User getUser(String email) {
        log.info("Fetching user by email: {}", email);
        return userService.findByEmail(email);
    }
    
    private User getUser(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        return userService.findById(userId);
    }
}