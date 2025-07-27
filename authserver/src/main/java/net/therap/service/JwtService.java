package net.therap.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.therap.entity.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Service
public class JwtService {
    
    private static final String SECRET = "verysecretkey1234567890verysecretkey1234567890";
    
    private static final long ACCESS_EXPIRATION_MS = 86400000;
    
    private static final long REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;
    
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Long extractUserId(String jwtToken) {
        String sub = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
        
        return Long.parseLong(sub);
    }
    
    public boolean isValid(String jwtToken, Long userId) {
        return extractUserId(jwtToken).equals(userId) && !isExpired(jwtToken);
    }
    
    private boolean isExpired(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}