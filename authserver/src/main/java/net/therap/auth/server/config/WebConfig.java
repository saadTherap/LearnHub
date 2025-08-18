<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/config/WebConfig.java
package net.therap.auth.server.config;
========
package net.therap.server.app.config;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/config/WebConfig.java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author apurboturjo
 * @since 8/10/25
 */
@Configuration
public class WebConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}