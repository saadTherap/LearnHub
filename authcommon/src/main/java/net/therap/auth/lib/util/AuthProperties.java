package net.therap.auth.lib.util;

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
    
    private int accessTokenMaxAge = 54000;
    private int refreshTokenMaxAge = 378000;
    private boolean secureCookies = true;
}