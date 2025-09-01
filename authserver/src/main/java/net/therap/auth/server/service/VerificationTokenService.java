package net.therap.auth.server.service;

import lombok.RequiredArgsConstructor;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.entity.VerificationToken;
import net.therap.auth.server.respository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author apurboturjo
 * @since 8/11/25
 */
@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    
    @Transactional
    public String generateAndSendVerificationToken(User user) {
        System.out.println("Before deleting user");
        verificationTokenRepository.deleteByUser(user);
        verificationTokenRepository.flush();
        System.out.println("After deleting user");
        
        String token = UUID.randomUUID().toString();
        System.out.println("In token generation: " + token);
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
        
//        emailService.sendVerificationEmail(user.getEmail(), token);
        emailService.sendLinkToConsole(token);
        
        return token;
    }
}