//package net.therap.auth.server.service;
//
//import net.therap.auth.server.dto.JwtResponse;
//import net.therap.auth.server.dto.RegisterRequest;
//import net.therap.auth.server.entity.User;
//import net.therap.auth.server.enums.UserRole;
//import net.therap.auth.server.respository.VerificationTokenRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * @author apurboturjo
// * @since 8/27/25
// */
//@ExtendWith(MockitoExtension.class)
//class AuthServiceImplTest {
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private EmailService emailService;
//
//    @Mock
//    private VerificationTokenService verificationTokenService;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private VerificationTokenRepository verificationTokenRepository;
//
//    @Mock
//    private RegistrationService registrationService;
//
//    @Test
//    void register_ShouldCreateUserAndSendVerificationToken() {
//        RegisterRequest req = new RegisterRequest();
//        req.setEmail("test@gmail.com");
//        req.setPassword("Demo@123");
//        req.setRole("STUDENT");
//
//        User user = new User(1L, req.getEmail(), "hashed", UserRole.STUDENT, false);
//
//        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashed");
//        when(userService.saveUser(any(User.class))).thenReturn(user);
//
//        JwtResponse resp = authService.register(req);
//
//        verify(userService).saveUser(any(User.class));
//        verify(verificationTokenService).generateAndSendVerificationToken(user);
//    }
//}
