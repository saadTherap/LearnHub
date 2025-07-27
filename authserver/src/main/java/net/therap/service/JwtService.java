package net.therap.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
    
    private static final long REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L; // 7 days
    
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public boolean isValid(String token, String username) {
        return extractUsername(token).equals(username) && !isExpired(token);
    }
    
    private boolean isExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}