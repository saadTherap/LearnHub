package net.therap.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
public class MessageUtils {
    
    public static String getMessage(MessageSource messageSource, String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}