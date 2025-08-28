package net.therap.app.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gazizafor
 * @since 28/8/25
 */
class StringUtilTest {
    
    @Test
    void isEmpty_nullString_returnsTrue() {
        assertTrue(StringUtil.isEmpty(null));
    }
    
    @Test
    void isEmpty_emptyString_returnsTrue() {
        assertTrue(StringUtil.isEmpty(""));
    }
    
    @Test
    void isEmpty_blankString_returnsTrue() {
        assertTrue(StringUtil.isEmpty("   "));
    }
    
    @Test
    void isEmpty_nonEmptyString_returnsFalse() {
        assertFalse(StringUtil.isEmpty("hello"));
    }
    
    @Test
    void isEmpty_stringWithSpaces_returnsFalse() {
        assertFalse(StringUtil.isEmpty(" hello "));
    }
}