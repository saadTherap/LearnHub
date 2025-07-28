package net.therap.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Service
public class JwtService {
    
    private static final long ACCESS_EXPIRATION_MS = 86400000;
    
    private static final long REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;
    
    private final Key key;
    
    @Autowired
    public JwtService(MessageUtil messageUtil) {
        key = Keys.hmacShaKeyFor(messageUtil.getMessage("token.secret.key").getBytes());
    }
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }
    
    public Long extractUserId(String jwtToken) {
        return extractClaim(jwtToken, claims -> claims.get("userId", Long.class));
    }
    
    public String extractEmail(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }
    
    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }
    
    public boolean isValid(String jwtToken, UserDetails userDetails) {
        final String username = extractEmail(jwtToken);
        
        return (username.equals(userDetails.getUsername())) && !isExpired(jwtToken) && userDetails.isEnabled();
    }
    
    private boolean isExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }
    
    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}