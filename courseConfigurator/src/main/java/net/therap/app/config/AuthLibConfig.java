package net.therap.app.config;


import net.therap.auth.lib.filter.JwtAuthFilter;
import net.therap.auth.lib.validator.TokenValidator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author gazizafor
 * @since 18/8/25
 */


@Configuration
public class AuthLibConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(TokenValidator tokenValidator) {
        List<String> excludedPaths = List.of("/swagger-ui/", "/swagger-resources/", "/v3/api-docs", "/webjars/",
                                             "/public/");

        System.out.println("Filter bean created");
        
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(tokenValidator, excludedPaths);
        
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthFilter);
        registration.addUrlPatterns("/*"); // Apply to all paths
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // Set order
        
        System.out.println("FilterRegistrationBean bean created");
        
        return registration;
    }
}