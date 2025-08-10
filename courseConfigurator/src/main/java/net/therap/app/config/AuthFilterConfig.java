//package net.therap.app.config;
//
//import net.therap.auth.filter.JwtAuthFilter;
//import net.therap.auth.validator.TokenValidator;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class AuthFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean<JwtAuthFilter> authFilterFilterRegistration(TokenValidator tokenValidator) {
//        List<String> excludedPaths = List.of(
//                "/swagger-ui/",
//                "/course-configurator/public/"
//        );
//
//        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new JwtAuthFilter(tokenValidator));
//        registration.addUrlPatterns("/*");
//        registration.setName("JwtAuthFilter");
//        registration.setOrder(1);
//
//        return registration;
//    }
//}
