package net.therap.auth.server.service;

import jakarta.transaction.Transactional;
import net.therap.auth.server.config.TestServiceConfig;
import net.therap.auth.server.dto.*;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.entity.VerificationToken;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.UserRepository;
import net.therap.auth.server.respository.VerificationTokenRepository;
import net.therap.auth.server.service.interfaces.AuthService;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author apurboturjo
 * @since 9/1/25
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestServiceConfig.class)
class AuthServiceImplIntegrationTest {
    
    @MockitoBean
    private RegistrationService registrationService;
    
    @Autowired
    private JwtService jwtService;
    
    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        verificationTokenRepository.deleteAll();
    }
    
    @Test
    void register_ShouldCreateUserAndSendVerificationToken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@demo.com");
        request.setPassword("Demo@123");
        request.setRole("STUDENT");
        
        JwtResponse response = authService.register(request);
        
        assertThat(response.getMessage()).isNotNull();
        
        User savedUser = userRepository.findByEmail("test@demo.com");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@demo.com");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(passwordEncoder.matches("Demo@123", savedUser.getPassword())).isTrue();
    }
    
    @Test
    void login_ShouldReturnTokens_WhenCredentialsValid() {
        User user = new User();
        user.setEmail("login@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.INSTRUCTOR);
        user.setEnabled(true);
        userRepository.save(user);
        
        LoginRequest request = new LoginRequest();
        request.setEmail("login@demo.com");
        request.setPassword("Demo@123");
        
        LoginResponse response = authService.login(request);
        
        assertThat(response.getEmail()).isEqualTo("login@demo.com");
        assertThat(response.getRole()).isEqualTo("INSTRUCTOR");
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(jwtService.isValid(response.getAccessToken())).isTrue();
        assertThat(jwtService.isValid(response.getRefreshToken())).isTrue();
    }
    
    @Test
    void login_ShouldThrow_WhenUserDisabled() {
        User user = new User();
        user.setEmail("disabled@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(false);
        userRepository.save(user);
        
        LoginRequest request = new LoginRequest();
        request.setEmail("disabled@demo.com");
        request.setPassword("Demo@123");
        
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void login_ShouldThrow_WhenInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@demo.com");
        request.setPassword("wrongpassword");
        
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void delete_ShouldRemoveUser_WhenValidToken() {
        User user = new User();
        user.setEmail("delete@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(true);
        user = userRepository.save(user);
        
        String accessToken = jwtService.generateAccessToken(user);
        
        DeleteRequest request = new DeleteRequest();
        request.setAccessToken(accessToken);
        
        JwtResponse response = authService.delete(request);
        
        assertThat(response.getMessage()).isNotNull();
        
        User deletedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(deletedUser);
        assertThat(deletedUser.isDeleted()).isTrue();
    }
    
    @Test
    void delete_ShouldThrow_WhenInvalidToken() {
        DeleteRequest request = new DeleteRequest();
        request.setAccessToken("invalid-token");
        
        assertThatThrownBy(() -> authService.delete(request))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void refreshToken_ShouldGenerateNewAccessToken_WhenValidRefreshToken() {
        User user = new User();
        user.setEmail("refresh@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.INSTRUCTOR);
        user.setEnabled(true);
        user = userRepository.save(user);
        
        String refreshToken = jwtService.generateRefreshToken(user);
        
        JwtResponse response = authService.refreshToken(refreshToken);
        
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(jwtService.isValid(response.getAccessToken())).isTrue();
    }
    
    @Test
    void refreshToken_ShouldThrow_WhenUserDisabled() {
        User user = new User();
        user.setEmail("disabled-refresh@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        String refreshToken = jwtService.generateRefreshToken(user);
        
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void refreshToken_ShouldThrow_WhenInvalidToken() {
        assertThatThrownBy(() -> authService.refreshToken("invalid-refresh-token"))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void verifyEmail_ShouldEnableUserAndSendRegistrationInfo_WhenValidToken() {
        User user = new User();
        user.setEmail("verify@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("valid-verification-token");
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(verificationToken);
        
        JwtResponse response = authService.verifyEmail("valid-verification-token");
        
        assertThat(response.getMessage()).isNotNull();
        
        User verifiedUser = userRepository.findByEmail("verify@demo.com");
        assertThat(verifiedUser.isEnabled()).isTrue();
        
        assertThat(verificationTokenRepository.findByToken("valid-verification-token")).isEmpty();
        
        verify(registrationService, times(1))
                .sendStudentRegistrationInfo("verify@demo.com");
    }
    
    @Test
    void verifyEmail_ShouldSendInstructorInfo_WhenInstructorVerifies() {
        User user = new User();
        user.setEmail("instructor@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.INSTRUCTOR);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("instructor-verification-token");
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(verificationToken);
        
        JwtResponse response = authService.verifyEmail("instructor-verification-token");
        
        assertThat(response.getMessage()).isNotNull();
        
        verify(registrationService, times(1))
                .sendInstructorRegistrationInfo("instructor@demo.com");
    }
    
    @Test
    void verifyEmail_ShouldThrow_WhenTokenNotFound() {
        assertThatThrownBy(() -> authService.verifyEmail("nonexistent-token"))
                .isInstanceOf(AuthServerException.class);
    }
    
    @Test
    void verifyEmail_ShouldThrow_WhenTokenExpired() {
        User user = new User();
        user.setEmail("expired@demo.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        VerificationToken expiredToken = new VerificationToken();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(user);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        verificationTokenRepository.save(expiredToken);
        
        assertThatThrownBy(() -> authService.verifyEmail("expired-token"))
                .isInstanceOf(AuthServerException.class);
        
        assertThat(verificationTokenRepository.findByToken("expired-token")).isEmpty();
    }
    
    @Test
    void updateUser_ShouldModifyUserDetails() {
        User user = new User();
        user.setEmail("updated@demo.com");
        user.setPassword(passwordEncoder.encode("OldDemo@123"));
        user.setRole(UserRole.STUDENT);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        String testVerificationToken = "valid-verification-token";
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(testVerificationToken);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(verificationToken);
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("NewDemo@123");
        request.setEnabled(true);
        request.setUpdateAccessToken(testVerificationToken);
        
        JwtResponse response = authService.updateUser(request);
        
        assertThat(response.getMessage()).isNotNull();
        
        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@demo.com");
        assertThat(passwordEncoder.matches("NewDemo@123", user.getPassword())).isTrue();
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(updatedUser.isEnabled()).isTrue();
    }
}