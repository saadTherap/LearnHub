package net.therap.util;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class PublicEndpoints {
    
    public static final String[] WHITELIST = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };
}