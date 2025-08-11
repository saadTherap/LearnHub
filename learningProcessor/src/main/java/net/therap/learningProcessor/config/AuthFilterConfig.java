package net.therap.learningProcessor.config;

import net.therap.auth.context.RequestTokenContext;
import net.therap.auth.filter.JwtAuthFilter;
import net.therap.auth.validator.TokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author apurboturjo
 * @since 8/11/25
 */
@Configuration
public class AuthFilterConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenValidator tokenValidator, RequestTokenContext requestTokenContext) {
        List<String> excludedPaths = List.of(
                "/swagger-ui/",
                "/swagger-resources/",
                "/v3/api-docs",
                "/webjars/",
                "/public/"
        );

        System.out.println("Filter bean created");

        return new JwtAuthFilter(tokenValidator, excludedPaths, requestTokenContext);
    }
}
