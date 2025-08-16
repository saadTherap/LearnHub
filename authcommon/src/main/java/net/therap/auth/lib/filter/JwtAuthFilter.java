package net.therap.auth.lib.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.exception.AuthenticationException;
import net.therap.auth.lib.validator.TokenValidator;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USER_ROLE = "role";

    private final List<String> excludedPaths;
    private final TokenValidator tokenValidator;
    
    public JwtAuthFilter(TokenValidator tokenValidator, List<String> excludedPaths) {
        this.tokenValidator = tokenValidator;
        this.excludedPaths = excludedPaths;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = contextPath.isEmpty() ? requestURI : requestURI.substring(contextPath.length());

        log.debug("=== SHOULD NOT FILTER DEBUG ===");
        log.debug("Request URI: " + requestURI);
        log.debug("Context Path: '" + contextPath + "'");
        log.debug("Extracted Path: '" + path + "'");
        log.debug("Excluded Paths: " + excludedPaths);

        boolean shouldExclude = excludedPaths.stream().anyMatch(excludedPath -> {
            boolean matches = path.startsWith(excludedPath);
            log.debug("Checking '" + path + "' starts with '" + excludedPath + "': " + matches);
            return matches;
        });

        log.debug("Should exclude (shouldNotFilter): " + shouldExclude);
        log.debug("===============================");

        return shouldExclude;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("=== JWT FILTER CALLED ===");
        log.debug("Request URI: " + request.getRequestURI());
        log.debug("Context Path: " + request.getContextPath());
        log.debug("========================");

        String token = extractTokenFromRequest(request);
        log.debug("token ===> " + token);

        if (!StringUtils.hasText(token)) {
            log.debug("No JWT token found in request");
            sendUnauthorizedError(response, "Authentication token required");

            return;
        }

        try {
            JWTClaimsSet claims = tokenValidator.validate(token);
            
            Long userId = (Long) claims.getClaim(CLAIM_USER_ID);
            String email = (String) claims.getSubject();
            String role = (String) claims.getClaim(CLAIM_USER_ROLE);
            UserRequestCache.put(userId, email, role);
            request.setAttribute(CLAIM_USER_ID, userId);
            
            log.debug("Token validated successfully for user: {} ===> " + email);

            log.debug("Response: " + response);
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