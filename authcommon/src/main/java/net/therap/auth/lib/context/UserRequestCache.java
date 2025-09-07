package net.therap.auth.lib.context;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author apurboturjo
* @since 8/13/25
*/
@Component
public class UserRequestCache {
    
    public static Map<Long, UserInfo> cache = new ConcurrentHashMap<>();
    
    public static void put(Long userId, String email, String role, String token) {
        cache.put(userId, new UserInfo(email, role, token));
    }
    
    public static UserInfo get(Long userId) {
        return cache.get(userId);
    }
    
    public static void remove(Long userId) {
        cache.remove(userId);
    }
    
    public record UserInfo(String email, String role, String token) {}
    
    @Scheduled(fixedRate = 24*60*60)
    public void clearCachePeriodical() {
        cache.clear();
    }
}