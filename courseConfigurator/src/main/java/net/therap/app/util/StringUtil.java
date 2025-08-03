package net.therap.app.util;

/**
 * @author gazizafor
 * @since 3/8/25
 */
public class StringUtil {
    
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}