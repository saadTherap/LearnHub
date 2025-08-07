package net.therap.service.interfaces;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
public interface EmailService {
    
    void sendVerificationEmail(String to, String token);
}