package net.therap.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author gazizafor
 * @since 28/8/25
 */
class HtmlSanitizerUtilTest {
    
    private HtmlSanitizerUtil htmlSanitizerUtil;
    
    @BeforeEach
    void setUp() {
        htmlSanitizerUtil = new HtmlSanitizerUtil();
    }
    
    @Test
    void sanitizeHtml_nullInput_returnsNull() {
        assertNull(htmlSanitizerUtil.sanitizeHtml(null));
    }
    
    @Test
    void sanitizeHtml_emptyInput_returnsEmptyString() {
        assertEquals("", htmlSanitizerUtil.sanitizeHtml(""));
    }
    
    @Test
    void sanitizeHtml_safeHtml_returnsSameHtml() {
        String safeHtml = "<p>Hello <strong>World</strong>!</p>";
        assertEquals(safeHtml, htmlSanitizerUtil.sanitizeHtml(safeHtml));
    }
    
    @Test
    void sanitizeHtml_htmlWithScriptTag_removesScript() {
        String maliciousHtml = "<p>Hello</p><script>alert('xss');</script>";
        String expectedHtml = "<p>Hello</p>";
        assertEquals(expectedHtml, htmlSanitizerUtil.sanitizeHtml(maliciousHtml));
    }
    
    @Test
    void sanitizeHtml_htmlWithUnsafeAttributes_removesUnsafeAttributes() {
        String maliciousHtml = "<img src=\"x.jpg\" onerror=\"alert('xss')\">";
        String expectedHtml = "<img src=\"x.jpg\" />";
        assertEquals(expectedHtml, htmlSanitizerUtil.sanitizeHtml(maliciousHtml));
    }
    
    @Test
    void sanitizeHtml_htmlWithAllowedLink_preservesLink() {
        String htmlWithLink = "<a href=\"http://example.com\">Link</a>";
        String expectedHtml = "<a href=\"http://example.com\" rel=\"nofollow\">Link</a>";
        assertEquals(expectedHtml, htmlSanitizerUtil.sanitizeHtml(htmlWithLink));
    }
    
    @Test
    void sanitizeHtml_htmlWithAllowedStyle_preservesStyle() {
        String htmlWithStyle = "<p style=\"color:red;\">Styled text</p>";
        String expectedHtml = "<p style=\"color:red\">Styled text</p>";
        assertEquals(expectedHtml, htmlSanitizerUtil.sanitizeHtml(htmlWithStyle));
    }
    
    @Test
    void sanitizeHtml_complexMaliciousHtml_sanitizesCorrectly() {
        String complexMaliciousHtml = "<div onclick=\"alert('xss')\"><p>Test</p><img src=\"invalid\" onerror=\"alert" +
                "('xss')\"></div>";
        String expectedHtml = "<div><p>Test</p><img src=\"invalid\" /></div>";
        assertEquals(expectedHtml, htmlSanitizerUtil.sanitizeHtml(complexMaliciousHtml));
    }
}