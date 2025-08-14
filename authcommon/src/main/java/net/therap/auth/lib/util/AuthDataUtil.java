package net.therap.auth.lib.util;

import net.therap.auth.lib.context.UserRequestCache;

import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/13/25
 */
public class AuthDataUtil {

    public static UserRequestCache.UserInfo getUserInfo(Long userId) {
        if (Objects.nonNull(userId)) {
            UserRequestCache.UserInfo info = UserRequestCache.get(userId);
            UserRequestCache.remove(userId);
            
            return info;
        }
        
        return null;
    }
}