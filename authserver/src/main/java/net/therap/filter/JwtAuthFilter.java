package net.therap.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.service.JwtService;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static net.therap.util.PublicEndpoints.WHITELIST;

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

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.warn("doFilterInternal ==>> path: {}", request.getServletPath());

        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            try {
                email = jwtService.extractEmail(jwt);

                UserDetails userDetails = userService.loadUserByEmail(email);

                if (userDetails == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                    return;
                }

                if (jwtService.isValid(jwt, userDetails)) {
                    request.setAttribute("authenticatedUserEmail", email);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }

            } catch (JwtException ex) {
                log.warn("JWT token parsing failed: {}", ex.getMessage());
            }
        }

        if (Objects.nonNull(email)) {
            try {
                if (jwtService.isValid(jwt, email)) {
                    request.setAttribute("authenticatedUserEmail", email);
                }

            } catch (JwtException ex) {
                log.warn("Authentication failed for JWT token: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

        } else {
            log.warn("No valid JWT token found in request");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");

            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        log.warn("=== SHOULD NOT FILTER DEBUG ===");
        log.warn("Request URI: {}", requestURI);
        log.warn("Excluded Paths: {}", (Object) WHITELIST);

        boolean shouldExclude = Stream.of(WHITELIST).anyMatch(excludedPath -> {
            boolean matches = requestURI.startsWith(excludedPath);
            System.out.println("Checking '" + requestURI + "' starts with '" + excludedPath + "': " + matches);
            return matches;
        });

        log.warn("Should exclude (shouldNotFilter): {}", shouldExclude);
        log.warn("===============================");

        return shouldExclude;
    }
}
