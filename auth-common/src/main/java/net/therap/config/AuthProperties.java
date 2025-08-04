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
    
    private int accessTokenMaxAge = 54000L;
    private int refreshTokenMaxAge = 378000L;
    private String publicKeyDir = "keys";
    private boolean secureCookies = true;
}