package net.therap.auth.server.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;

import java.security.KeyFactory;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class JwtUtil {
    
    public static UserRole toSystemFormatUserRole(String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            
            if (userRole == UserRole.ADMIN) {
                throw new AuthServerException(MessageUtil.getMessage("err.role.invalid"));
            }
            
            return userRole;
            
        } catch (IllegalArgumentException e) {
            throw new AuthServerException(MessageUtil.getMessage("err.role.invalid"));
        }
    }
    
    public static RSAKey getRSAKey(String key, String type) throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return "public".equalsIgnoreCase(type) ? (RSAKey) kf.generatePublic(keySpec) :
                (RSAKey) kf.generatePrivate(keySpec);
    }
}