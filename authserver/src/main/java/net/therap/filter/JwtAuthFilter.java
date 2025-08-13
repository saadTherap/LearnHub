package net.therap.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.service.JwtService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static net.therap.util.Constants.WHITELIST;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("doFilterInternal ==>> path: {}", request.getServletPath());

        final String authHeader = request.getHeader("Authorization");

        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("No valid Authorization header found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");

            return;
        }

        String jwt = authHeader.substring(7);
        String email;

        try {
            if (!jwtService.isValid(jwt)) {
                log.warn("Invalid or expired token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");

                return;
            }

            email = jwtService.extractEmail(jwt);
            Long userId = jwtService.extractUserId(jwt);
            String role = jwtService.extractRole(jwt);

            request.setAttribute("authenticatedUserEmail", email);
            request.setAttribute("authenticatedUserId", userId);
            request.setAttribute("authenticatedUserRole", role);

            log.debug("Successfully authenticated user: {}", email);

        } catch (RuntimeException ex) {
            log.warn("JWT token validation failed: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");

            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        log.debug("=== SHOULD NOT FILTER DEBUG ===");
        log.debug("Request URI: {}", requestURI);
        log.debug("Excluded Paths: {}", (Object) WHITELIST);

        boolean shouldExclude = Stream.of(WHITELIST).anyMatch(excludedPath -> {
            boolean matches = requestURI.startsWith(excludedPath);
            log.debug("Checking '{}' starts with '{}': {}", requestURI, excludedPath, matches);
            return matches;
        });

        log.debug("Should exclude (shouldNotFilter): {}", shouldExclude);
        log.debug("===============================");

        return shouldExclude;
    }
}