package net.therap;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        
        try {
            String subject = "Test Mail from LearnHub";
            String body = "A demo mail for you";
            
            helper.setFrom("apurbo.turjo@therapservices.net");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
            
        } catch (MessagingException e) {
            
            throw new RuntimeException("Failed to send verification email.");
        }
    }
}