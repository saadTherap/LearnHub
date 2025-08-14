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

    public static HazelcastInstance startServer(String portStr) throws Exception {
        InputStream xmlStream = HazelcastServerApp.class.getClassLoader().getResourceAsStream("hazelcast-server.xml");
        XmlConfigBuilder configBuilder = new XmlConfigBuilder(xmlStream);
        Config config = configBuilder.build();

        int port = 5701;
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default 5701");
            }
        }

        config.getNetworkConfig().setPort(port);
        config.getNetworkConfig().setPortAutoIncrement(true);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        System.out.println("Hazelcast started on port: " + port);
        return hz;
    }

    public static void main(String[] args) throws Exception {
        startServer(System.getenv("HZ_PORT"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Hazelcast stopped")));
    }
}
