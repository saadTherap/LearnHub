package net.therap.auth.lib.util;

import net.therap.auth.lib.context.UserRequestCache;

import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/13/25
 */
public class AuthDataUtil {
    
    /**
     * @param userId
     * @return UserInfo(email, role)
     * You can access a user's info at max one time, after an access the local cache gets cleared up.
     */
    public static UserRequestCache.UserInfo getUserInfo(Long userId) {
        if (Objects.nonNull(userId)) {
            return UserRequestCache.get(userId);
        }
        
        return null;
    }
}