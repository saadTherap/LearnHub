package net.therap.app.validation.htmlSanitization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;

/**
 * @author gazizafor
 * @since 3/8/25
 */
@Slf4j
public class HtmlSanitizerDeserializer extends JsonDeserializer<String> {
    
    private static final PolicyFactory HTML_POLICY =
            Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS).and(Sanitizers.STYLES).and(Sanitizers.TABLES);
    
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        String s = HTML_POLICY.sanitize(value);
        log.info("[inside HtmlSanitizerDeserializer] {}", s);
        return s;
    }
}