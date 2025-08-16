package net.therap.server.app.util;

import java.util.List;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class Constants {

    public static final List<String> WHITELIST = List.of(
            "/api/",
            "/pk",
            "/swagger-ui/",
            "/v3/api-docs",
            "/swagger-resources/",
            "/error",
            "/public/"
    );
}