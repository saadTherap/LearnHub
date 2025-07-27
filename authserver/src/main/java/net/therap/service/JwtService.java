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
    private static final long EXPIRATION_MS = 86400000;
    
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
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