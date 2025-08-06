package net.therap.app.validation.htmlSanitization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author gazizafor
 * @since 3/8/25
 */
public class HtmlSanitizerDeserializer extends JsonDeserializer<String> {
    
    private static final Logger logger = LoggerFactory.getLogger(HtmlSanitizerDeserializer.class);
    
    private static final PolicyFactory HTML_POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.STYLES)
            .and(Sanitizers.TABLES);
    
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        String s = HTML_POLICY.sanitize(value);
        logger.info("[inside HtmlSanitizerDeserializer] {}", s);
        return s;
    }
}