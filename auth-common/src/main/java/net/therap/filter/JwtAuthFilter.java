package net.therap.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.therap.config.AuthProperties;
import net.therap.exception.AuthenticationException;
import net.therap.service.TokenRefresherService;
import net.therap.validator.TokenValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final String ACCESS_TOKEN_COOKIE_KEY = "access_token";
    private final String REFRESH_TOKEN_COOKIE_KEY = "refresh_token";
    private final TokenValidator tokenValidator;
    private final TokenRefresherService tokenRefresherService;
    private final AuthProperties authProperties;
    
    public JwtAuthFilter(TokenValidator tokenValidator,
                         TokenRefresherService tokenRefresherService,
                         AuthProperties authProperties) {
        this.tokenValidator = tokenValidator;
        this.tokenRefresherService = tokenRefresherService;
        this.authProperties = authProperties;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String accessToken = extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE_KEY);
        String refreshToken = extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_KEY);
        
        if (Objects.isNull(accessToken)) {
            sendUnauthorizedError(response, "Access token missing");
            return;
        }
        
        try {
            JWTClaimsSet claims = tokenValidator.validate(accessToken);
            log.debug("Token validated successfully for subject: {}", claims.getSubject());
            
            filterChain.doFilter(request, response);
            
        } catch (AuthenticationException e) {
            log.warn("Access token validation failed: {}", e.getMessage());
            
            if (Objects.nonNull(refreshToken)) {
                handleTokenRefresh(request, response, filterChain, refreshToken);
            } else {
                sendUnauthorizedError(response, "Token invalid");
            }
        }
    }
    
    private void handleTokenRefresh(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain,
                                    String refreshToken) throws ServletException, IOException {
        try {
            String newAccessToken = tokenRefresherService.refresh(refreshToken);
            setAccessTokenCookie(response, newAccessToken);
            
            log.info("Token refreshed successfully");
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            sendUnauthorizedError(response, "Token refresh failed");
        }
    }
    
    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(authProperties.isSecureCookies());
        cookie.setPath("/");
        cookie.setMaxAge(authProperties.getAccessTokenMaxAge());
        
        response.addCookie(cookie);
    }
    
    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        log.warn("Authentication failed: {}", message);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
    }
    
    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        
        return null;
    }
}