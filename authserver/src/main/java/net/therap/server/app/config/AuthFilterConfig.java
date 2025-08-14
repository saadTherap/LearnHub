package net.therap.server.app.config;

import net.therap.auth.lib.context.RequestTokenContext;
import net.therap.auth.lib.filter.JwtAuthFilter;
import net.therap.auth.lib.validator.TokenValidator;
import net.therap.server.app.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Configuration
public class AuthFilterConfig {
    
    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenValidator tokenValidator, RequestTokenContext requestTokenContext) {
        return new JwtAuthFilter(tokenValidator, Constants.WHITELIST, requestTokenContext);
    }
}