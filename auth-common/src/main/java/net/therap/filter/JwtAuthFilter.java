package net.therap.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.therap.exception.AuthenticationException;
import net.therap.validator.TokenValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final TokenValidator tokenValidator;
    
    public JwtAuthFilter(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractTokenFromRequest(request);
        
        if (!StringUtils.hasText(token)) {
            log.debug("No JWT token found in request");
            sendUnauthorizedError(response, "Authentication token required");
            
            return;
        }
        
        try {
            JWTClaimsSet claims = tokenValidator.validate(token);
            log.debug("Token validated successfully for user: {}", claims.getSubject());
            
            filterChain.doFilter(request, response);
            
        } catch (AuthenticationException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            sendUnauthorizedError(response, "Invalid or expired token");
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(authHeader)) {
            if (authHeader.startsWith(BEARER_PREFIX)) {
                return authHeader.substring(BEARER_PREFIX.length());
            }
            
            return authHeader;
        }
        
        return null;
    }
    
    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        log.warn("Authentication failed: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Authentication required\", \"message\": \"" + message + "\"}");
    }
}