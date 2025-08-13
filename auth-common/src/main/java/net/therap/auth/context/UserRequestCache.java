package net.therap.auth.context;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author apurboturjo
* @since 8/13/25
*/
public class UserRequestCache {
    
    private static Map<Long, UserInfo> cache = new ConcurrentHashMap<>();
    
    public static void put(Long userId, String email, String role) {
        cache.put(userId, new UserInfo(email, role));
    }
    
    public static UserInfo get(Long userId) {
        return cache.get(userId);
    }
    
    public static void remove(Long userId) {
        cache.remove(userId);
    }
    
    public record UserInfo(String email, String role) {}
    
    @Scheduled(fixedRate = 1000)
    public void clearCache() {
        cache.clear();
    }
}