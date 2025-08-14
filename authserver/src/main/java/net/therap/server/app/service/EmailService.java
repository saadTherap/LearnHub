package net.therap.server.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.server.app.exception.AuthServerException;
import net.therap.server.app.util.MessageUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static net.therap.server.app.enums.ExceptionTypes.MAIL_VERIFICATION_ERROR_EXCEPTION;

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

    
    public void sendVerificationEmail(String to, String token) throws AuthServerException {
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
            
            throw new AuthServerException("Failed to send verification email.", MAIL_VERIFICATION_ERROR_EXCEPTION);
        }
    }
}