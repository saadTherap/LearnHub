package net.therap.app.config;

import net.therap.auth.filter.JwtAuthFilter;
import net.therap.auth.validator.TokenValidator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AuthFilterConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenValidator tokenValidator) {
        List<String> excludedPaths = List.of(
                "/swagger-ui/",
                "/swagger-resources/",
                "/v3/api-docs",
                "/webjars/",
                "/public/"
        );

        System.out.println("Filter bean created");

        return new JwtAuthFilter(tokenValidator, excludedPaths);
    }
}
