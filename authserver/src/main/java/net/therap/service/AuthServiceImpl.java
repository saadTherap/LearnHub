package net.therap.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.dto.JwtResponse;
import net.therap.dto.LoginRequest;
import net.therap.dto.RegisterRequest;
import net.therap.entity.User;
import net.therap.entity.VerificationToken;
import net.therap.exception.AuthenticationException;
import net.therap.respository.VerificationTokenRepository;
import net.therap.service.interfaces.AuthService;
import net.therap.service.interfaces.EmailService;
import net.therap.util.MessageUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static net.therap.util.JwtUtil.toSystemFormatUserRole;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final MessageUtil messageUtil;
    private final EmailService emailService;
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

        generateAndSendVerificationToken(savedUser);

        return new JwtResponse(messageUtil.getMessage("reg.success.verify_pending"));
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = authenticateUser(request.getEmail(), request.getPassword());

        if (!user.isEnabled()) {
            throw new AuthenticationException(messageUtil.getMessage("err.user.not_enabled"));
        }

        return generateTokenPair(user.getId());
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isValid(refreshToken)) {
            throw new AuthenticationException(messageUtil.getMessage("err.refresh.token.invalid"));
        }

        User user = getUser(email);

        if (!user.isEnabled()) {
            throw new AuthenticationException(messageUtil.getMessage("err.user.not_enabled"));
        }

        String access = jwtService.generateAccessToken(user);

        return new JwtResponse(access, refreshToken);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException(
                        messageUtil.getMessage("err.verify.token.invalid"))
                );

        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);

            throw new AuthenticationException(
                    messageUtil.getMessage("err.verify.token.expired"));
        }

        User userToVerify = verificationToken.getUser();
        userToVerify.setEnabled(true);
        userService.updateUser(userToVerify);

        verificationTokenRepository.delete(verificationToken);
    }

    private User authenticateUser(String email, String password) {
        User user = userService.findByEmail(email);

        if (Objects.isNull(user)) {
            throw new AuthenticationException(messageUtil.getMessage("err.auth.invalid_credentials"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException(messageUtil.getMessage("err.auth.invalid_credentials"));
        }

        return user;
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
        return userService.findByEmailOrThrow(email);
    }

    private User getUser(Long userId) {
        return userService.findById(userId);
    }
}