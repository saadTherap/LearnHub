package net.therap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.exception.RegistrationTokenVerificationException;
import net.therap.service.interfaces.EmailService;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    
    private final String frontendVerificationUrl = "http://localhost:3000/verify";
    
    @Override
    public void sendVerificationEmail(String to, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        
        try {
            String subject = messageSource.getMessage("email.verification.subject", null, Locale.getDefault());
            String body = messageSource.getMessage("email.verification.body",
                    new Object[]{frontendVerificationUrl + "?token=" + token}, Locale.getDefault());
            
            helper.setFrom("apurbo.turjo@therapservices.net");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            
            throw new RegistrationTokenVerificationException("Failed to send verification email.", e);
        }
    }
}