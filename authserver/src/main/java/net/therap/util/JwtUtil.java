package net.therap.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.enums.UserRole;
import net.therap.exception.InvalidRoleSpecifiedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class JwtUtil {
    
    public static final String USER_ROLE_ERROR = "No such user role exists";
    
    public static UserRole toSystemFormatUserRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleSpecifiedException(USER_ROLE_ERROR + ": " + role);
        }
    }
    
    public static RSAPrivateKey getPrivateKey(String path) throws Exception {
        String key = sanitizeKeyString(path);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }
    
    public static RSAPublicKey getPublicKey(String path) throws Exception {
        String key = sanitizeKeyString(path);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return (RSAPublicKey) kf.generatePublic(spec);
    }
    
    private static String sanitizeKeyString(String path) throws IOException {
        return Files.readString(Paths.get(path))
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
    }
}