package net.therap;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.InputStream;

/**
 * @author riadanonto
 * @since 24/7/25
 */
public class HazelcastServerApp {

    public static void main(String[] args) throws Exception {
        InputStream xmlStream = HazelcastServerApp.class.getClassLoader().getResourceAsStream("hazelcast-server.xml");
        XmlConfigBuilder configBuilder = new XmlConfigBuilder(xmlStream);
        Config config = configBuilder.build();

        // Read port from env variable, fallback to 5701 if not set
        String portStr = System.getenv("HZ_PORT");
        int port = 5701;
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port in HZ_PORT env var, defaulting to 5701");
            }
        }

        config.getNetworkConfig().setPort(port);
        config.getNetworkConfig().setPortAutoIncrement(true);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        System.out.println("Hazelcast server started on port: " + port + ", cluster size: " + hz.getCluster().getMembers().size());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hz.shutdown();
            System.out.println("Hazelcast server stopped.");
        }));
    }

}
