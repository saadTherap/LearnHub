package net.therap.util;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class PublicEndpoints {

    public static final String[] WHITELIST = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico",
            "/error"
    };
}