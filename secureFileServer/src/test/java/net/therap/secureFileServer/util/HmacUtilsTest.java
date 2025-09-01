package net.therap.secureFileServer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author avidewan
 * @since 8/31/25
 */
class HmacUtilsTest {

    @Test
    void testHmacSHA256_CorrectValue() {
        String secret = "mySecretKey";
        String data = "HelloWorld";

        String hmac = HmacUtils.hmacSHA256(secret, data);

        assertNotNull(hmac);
        assertFalse(hmac.isEmpty());

        System.out.println(hmac);

        String expectedHmac = "xoMe8UQuY3GLSFephdUvOXrEyPDzUKctKFwTTu4RQ+E="; // precomputed
        assertEquals(expectedHmac, hmac);
    }

    @Test
    void testHmacSHA256_DifferentDataProducesDifferentHmac() {
        String secret = "mySecretKey";
        String data1 = "HelloWorld";
        String data2 = "HelloWorld2";

        String hmac1 = HmacUtils.hmacSHA256(secret, data1);
        String hmac2 = HmacUtils.hmacSHA256(secret, data2);

        assertNotEquals(hmac1, hmac2);
    }

    @Test
    void testHmacSHA256_DifferentSecretProducesDifferentHmac() {
        String secret1 = "mySecretKey";
        String secret2 = "anotherSecretKey";
        String data = "HelloWorld";

        String hmac1 = HmacUtils.hmacSHA256(secret1, data);
        String hmac2 = HmacUtils.hmacSHA256(secret2, data);

        assertNotEquals(hmac1, hmac2);
    }
}