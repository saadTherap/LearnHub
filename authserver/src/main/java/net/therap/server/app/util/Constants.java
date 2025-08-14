package net.therap.server.app.util;

import java.util.List;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class Constants {

    public static final List<String> WHITELIST = List.of(
            "/auth/api/",
            "/auth/pk",
            "/auth/swagger-ui/",
            "/auth/v3/api-docs",
            "/auth/swagger-resources/",
            "/error",
            "/public/"
    );
}