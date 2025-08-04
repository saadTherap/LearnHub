package net.therap.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import net.therap.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;
import java.util.function.Function;

import static net.therap.util.JwtUtil.getPrivateKey;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Service
public class JwtService {
    
    private static final long ACCESS_EXPIRATION_MS = 86400000;
    
    private static final long REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;
    
    @Value("${jwt.private-key-path}")
    private String privateKeyPath;
    
    @Value("${jwt.public-key-path}")
    private String publicKeyPath;
    
    private PrivateKey privateKey;
    
    @PostConstruct
    public void loadKeys() throws Exception {
        this.privateKey = getPrivateKey(privateKeyPath);
    }
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_MS))
                .signWith(privateKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(privateKey, SignatureAlgorithm.HS256)
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
        return !isExpired(jwtToken) && userDetails.isEnabled();
    }
    
    private boolean isExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }
    
    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(privateKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}