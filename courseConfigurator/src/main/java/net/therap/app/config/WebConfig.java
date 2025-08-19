package net.therap.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gazizafor
 * @since 9/8/25
 */
@Configuration
public class WebConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowedHeaders("*");
            }
        };
    }
    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, DomainService domainService) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
}