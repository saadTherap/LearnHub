package net.therap.auth.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Component
public class MessageUtil {
    
    private static MessageSource messageSource;
    
    @Autowired
    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }
    
    public static String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.US);
    }
    
    public static String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }
    
    public static String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }
}