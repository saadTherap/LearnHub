package net.therap.learningProcessor.config;


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
                "/public/", "/appStatus");

        System.out.println("Filter bean created");

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(tokenValidator, excludedPaths);

        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);

        System.out.println("FilterRegistrationBean bean created");

        return registration;
    }
}