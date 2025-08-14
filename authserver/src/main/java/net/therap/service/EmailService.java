package net.therap.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.util.MessageUtil;
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
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    private final MessageUtil messageUtil;
    
    public void sendLinkToConsole(String token) {
        String link = "https://app-rnd01.therapbd.net/auth/api/verify-email?token=" + token;
        
        String RED = "\u001B[31m";
        String PURPLE = "\u001B[35m";
        String RESET = "\u001B[0m";
        
        System.out.println(RED + "Verify your email using this link: " + PURPLE + link + RESET);
        
        log.info("Verification email sent to: console");
    }

    
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