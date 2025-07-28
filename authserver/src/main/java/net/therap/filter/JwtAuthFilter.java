package net.therap.filter;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.entity.User;
import net.therap.service.JwtService;
import net.therap.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    private final CustomUserDetailsService customUserDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Extract username (email) from the token
                username = jwtService.extractUsername(jwt);
                
            } catch (JwtException ex) {
                // If token extraction fails (e.g., malformed token), log and let the filter chain proceed
                log.warn("JWT token parsing failed: {}", ex.getMessage());
            }
        }
        
        // If a username is found and the SecurityContext is not yet populated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Get the UserDetails object using the correct service method
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
                
                // Validate the token against the user's details
                if (jwtService.isValid(jwt, userDetails)) {
                    // Create an Authentication object with the correct details and authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set the Authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException | JwtException ex) {
                // Handle cases where the username is not found or token validation fails
                log.warn("Authentication failed for JWT token: {}", ex.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}