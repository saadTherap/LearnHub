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

    private static final String PRE_FLIGHT_REQUEST_METHOD = "OPTIONS";
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

        System.out.println("=== SHOULD NOT FILTER DEBUG ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Context Path: '" + contextPath + "'");
        System.out.println("Extracted Path: '" + path + "'");
        System.out.println("Excluded Paths: " + excludedPaths);

        boolean shouldExclude = excludedPaths.stream().anyMatch(excludedPath -> {
            boolean matches = path.startsWith(excludedPath);
            System.out.println("Checking '" + path + "' starts with '" + excludedPath + "': " + matches);
            return matches;
        });

        System.out.println("Should exclude (shouldNotFilter): " + shouldExclude);
        System.out.println("===============================");

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
        
        if (PRE_FLIGHT_REQUEST_METHOD.equals(request.getMethod())) {
            log.debug("OPTIONS request detected, bypassing JWT validation");
            filterChain.doFilter(request, response);
            
            return;
        }
        
        System.out.println("==================------------------===============================");
        System.out.println(request.getHeader("Hello-Header"));
        System.out.println("=== JWT FILTER CALLED ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("========================");

        String token = extractTokenFromRequest(request);
        System.out.println("token ===> " + token);

        if (!StringUtils.hasText(token)) {
            System.out.println("No JWT token found in request");
            sendUnauthorizedError(response, "Authentication token required");

            return;
        }

        try {
            JWTClaimsSet claims = tokenValidator.validate(token);
            
            Long userId = (Long) claims.getClaim(CLAIM_USER_ID);
            String email = (String) claims.getSubject();
            String role = (String) claims.getClaim(CLAIM_USER_ROLE);
            UserRequestCache.put(userId, email, role, token);
            request.setAttribute(CLAIM_USER_ID, userId);
            
            System.out.println("Token validated successfully for user: {} ===> " + email);

            System.out.println("Response: " + response);
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