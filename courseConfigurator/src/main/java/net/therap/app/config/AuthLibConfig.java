package net.therap.app.config;


import net.therap.auth.lib.filter.JwtAuthFilter;
import net.therap.auth.lib.validator.TokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author gazizafor
 * @since 18/8/25
 */


//@Configuration
//public class AuthLibConfig {
//
//    @Bean
//    public JwtAuthFilter jwtAuthFilter(TokenValidator tokenValidator) {
//        List<String> excludedPaths = List.of("/swagger-ui/", "/swagger-resources/", "/v3/api-docs", "/webjars/",
//                                             "/public/");
//
//        System.out.println("Filter bean created");
//
//        return new JwtAuthFilter(tokenValidator, excludedPaths);
//    }
//}