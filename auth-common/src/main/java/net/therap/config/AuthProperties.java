package net.therap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author apurboturjo
 * @since 8/4/25
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    
    private int accessTokenMaxAge = 900;
    private String publicKeyDir = "keys";
    private boolean secureCookies = true;
}