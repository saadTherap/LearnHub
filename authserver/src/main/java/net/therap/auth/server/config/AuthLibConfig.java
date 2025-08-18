package net.therap.auth.server.config;

import net.therap.auth.lib.filter.JwtAuthFilter;
import net.therap.auth.lib.validator.TokenValidator;
import net.therap.auth.server.util.Constants;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Configuration
@EnableFeignClients(basePackages = "net.therap.auth.lib.client")
public class AuthLibConfig {
    
    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenValidator tokenValidator) {
        return new JwtAuthFilter(tokenValidator, Constants.WHITELIST);
    }
}