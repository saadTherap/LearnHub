package net.therap.auth.server.util;

import java.util.List;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class Constants {

    public static final List<String> WHITELIST = List.of(
            "/api/login",
            "/api/register",
            "/api/refresh",
            "/api/verify-email",
            "/api/acquire-update-user-token",
            "/api/update-user",
            "/pk",
            "/swagger-ui/",
            "/v3/api-docs",
            "/swagger-resources/",
            "/error",
            "/public/",
            "/appStatus"
    );
    
    public static final String userIdAttributeKey = "userId";
}