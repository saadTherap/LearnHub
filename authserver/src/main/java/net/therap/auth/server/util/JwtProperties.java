package net.therap.auth.server.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author apurboturjo
 * @since 8/27/25
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final Duration accessTokenExpiration;
    
    private final Duration refreshTokenExpiration;
}