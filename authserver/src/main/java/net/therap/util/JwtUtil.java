package net.therap.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.enums.UserRole;
import net.therap.exception.InvalidRoleSpecifiedException;

import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.file.Files.readAllBytes;


/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class ServiceUtil {
    
    public static final String USER_ROLE_ERROR = "No such user role exists";
    
    public static UserRole toSystemFormatUserRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleSpecifiedException(USER_ROLE_ERROR + ": " + role);
        }
    }
    
    public static PrivateKey getPrivateKey(String path) throws Exception {
        byte[] keyBytes = readAllBytes(Paths.get(path));
        
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return kf.generatePrivate(spec);
    }
    
    public static PublicKey getPublicKey(String path) throws Exception {
        byte[] keyBytes = readAllBytes(Paths.get(path));
        
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return kf.generatePublic(spec);
    }
}