<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/service/VerificationTokenService.java
package net.therap.auth.server.service;

import lombok.RequiredArgsConstructor;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.entity.VerificationToken;
import net.therap.auth.server.respository.VerificationTokenRepository;
========
package net.therap.server.app.service;

import lombok.RequiredArgsConstructor;
import net.therap.server.app.entity.User;
import net.therap.server.app.entity.VerificationToken;
import net.therap.server.app.respository.VerificationTokenRepository;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/service/VerificationTokenService.java
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
    public void generateAndSendVerificationToken(User user) {
        verificationTokenRepository.deleteByUser(user);
        verificationTokenRepository.flush();
        
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
        
//        emailService.sendVerificationEmail(user.getEmail(), token);
        emailService.sendLinkToConsole(token);
    }
}