package net.therap.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author gazizafor
 * @since 13/8/25
 */
public class AuthorizationUtil {
    
    @Autowired
    private MessageSource messageSource;
    
//    public UserRequestCache.UserInfo parseUserInfoFromRequest(HttpServletRequest request) throws BadRequestException {
//        long userId = Long.parseLong(request.getParameter("userId"));
//        UserRequestCache.UserInfo userInfo = AuthDataUtil.getUserInfo(userId);
//
//        if(userInfo == null) {
//            throw new BadRequestException(messageSource.getRole("invalid.user.id", null, Locale.getDefault()));
//        }
//    }
}