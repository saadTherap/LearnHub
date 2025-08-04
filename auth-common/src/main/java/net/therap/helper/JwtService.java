package net.therap.helper;

import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.file.Files.readAllBytes;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
public class JwtService {
    
    public static PublicKey getPublicKey(String path) throws Exception {
        byte[] keyBytes = readAllBytes(Paths.get(path));
        
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        return kf.generatePublic(spec);
    }
}