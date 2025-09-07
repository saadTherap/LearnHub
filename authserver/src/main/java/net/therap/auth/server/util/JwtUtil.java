package net.therap.auth.server.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

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
    
    public static RSAPrivateKey decodeKey(String key) throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }
}