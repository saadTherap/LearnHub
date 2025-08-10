package net.therap.config;

import net.therap.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}