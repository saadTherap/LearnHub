package net.therap;

import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author riadanonto
 * @since 14/8/25
 */
class HazelcastServerAppTest {

    @Test
    void testStartServer_withValidPort() throws Exception {
        HazelcastInstance hz = HazelcastServerApp.startServer("5801");
        assertNotNull(hz);
        hz.shutdown();
    }

    @Test
    void testStartServer_withInvalidPort() throws Exception {
        HazelcastInstance hz = HazelcastServerApp.startServer("invalid");
        assertNotNull(hz);
        hz.shutdown();
    }

    @Test
    void testStartServer_withNullPort() throws Exception {
        HazelcastInstance hz = HazelcastServerApp.startServer(null);
        assertNotNull(hz);
        hz.shutdown();
    }
}

