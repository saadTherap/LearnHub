package net.therap.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.service.interfaces.EmailService;
import net.therap.auth.util.MessageUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
    
    private final MessageUtil messageUtil;
    
    @Override
    public void sendVerificationEmail(String to, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        
        try {
            String subject = messageUtil.getMessage("email.verification.subject", null, Locale.getDefault());
            String body = messageUtil.getMessage("email.verification.body",
                    new Object[]{messageUtil.getMessage("frontend.verification.url") + "?token=" + token},
                    Locale.getDefault());
            
            helper.setFrom("apurbo.turjo@therapservices.net");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
            
        } catch (MessagingException e) {
            
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            
            throw new RuntimeException("Failed to send verification email.");
        }
    }
}