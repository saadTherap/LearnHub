package net.therap;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.splitbrainprotection.SplitBrainProtectionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author riadanonto
 * @since 17/8/25
 */
class HazelcastConfigurationTest {

    private static HazelcastInstance node1;
    private static HazelcastInstance node2;
    private static HazelcastInstance node3;

    @BeforeAll
    static void setup() throws Exception {
        InputStream xmlStream = HazelcastConfigurationTest.class
                .getClassLoader()
                .getResourceAsStream("hazelcast-server.xml");
        assertNotNull(xmlStream, "Hazelcast XML config not found");

        // Start 3 nodes
        node1 = Hazelcast.newHazelcastInstance(new XmlConfigBuilder(xmlStream).build());
        node2 = Hazelcast.newHazelcastInstance(new XmlConfigBuilder(
                HazelcastConfigurationTest.class.getClassLoader().getResourceAsStream("hazelcast-server.xml")
        ).build());
        node3 = Hazelcast.newHazelcastInstance(new XmlConfigBuilder(
                HazelcastConfigurationTest.class.getClassLoader().getResourceAsStream("hazelcast-server.xml")
        ).build());

        // Verify cluster size
        assertEquals(3, node1.getCluster().getMembers().size());
    }

    @AfterAll
    static void tearDown() {
        if (node1 != null) node1.shutdown();
        if (node2 != null) node2.shutdown();
        if (node3 != null) node3.shutdown();
    }

    @Test
    void testClusterSize() {
        // Cluster should have at least 3 members
        assertTrue(node1.getCluster().getMembers().size() >= 3);
        assertTrue(node2.getCluster().getMembers().size() >= 3);
        assertTrue(node3.getCluster().getMembers().size() >= 3);
    }

    @Test
    void testMapConfigs() {
        MapConfig coursesConfig = node1.getConfig().getMapConfig("courses");
        MapConfig instructorsConfig = node1.getConfig().getMapConfig("instructors");

        assertEquals(1, coursesConfig.getBackupCount());
        assertEquals(1800, coursesConfig.getTimeToLiveSeconds());
        assertEquals("mapProtection", coursesConfig.getSplitBrainProtectionName());

        assertEquals(1, instructorsConfig.getBackupCount());
        assertEquals(900, instructorsConfig.getTimeToLiveSeconds());
        assertEquals("mapProtection", instructorsConfig.getSplitBrainProtectionName());
    }

    @Test
    void testPutGetAcrossNodes() {
        IMap<String, String> map1 = node1.getMap("courses");
        IMap<String, String> map2 = node2.getMap("courses");

        map1.put("id1", "Course A");
        map2.put("id2", "Course B");

        assertEquals("Course A", map2.get("id1"));
        assertEquals("Course B", map1.get("id2"));
    }

    @Test
    void testSplitBrainProtectionPreventsWrites() {
        IMap<String, String> map = node1.getMap("courses");

        // Initial put
        map.put("c1", "Course 1");
        assertEquals("Course 1", map.get("c1"));

        // Shut down one node
        node2.shutdown();

        // Wait until the cluster size is 2
        assertTrue(awaitClusterSize(node1, 2));

        // Put should still work
        map.put("c2", "Course 2");
        assertEquals("Course 2", map.get("c2"));

        // Shut down another node
        node3.shutdown();

        // Wait until the cluster size is 1
        assertTrue(awaitClusterSize(node1, 1));

        // The put operation should now fail
        assertThrows(SplitBrainProtectionException.class, () -> map.put("c3", "Course 3"));
    }

    private boolean awaitClusterSize(HazelcastInstance instance, int size) {
        long deadline = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < deadline) {
            if (instance.getCluster().getMembers().size() == size) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }
}

