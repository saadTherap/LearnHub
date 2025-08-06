package net.therap.app.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

/**
 * @author gazizafor
 * @since 03/8/25
 */
@Component
public class HtmlSanitizerUtil {
    
    private static final PolicyFactory HTML_POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.STYLES)
            .and(Sanitizers.TABLES);
    
    public String sanitizeHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        return HTML_POLICY.sanitize(html);
    }
}