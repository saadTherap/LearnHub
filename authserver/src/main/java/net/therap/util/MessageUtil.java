package net.therap.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author apurboturjo
 * @since 7/28/25
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {
    
    private final MessageSource messageSource;
    
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.US);
    }
    
    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }
}