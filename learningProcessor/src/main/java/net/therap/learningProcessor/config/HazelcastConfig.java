package net.therap.learningProcessor.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

/**
 * @author riadanonto
 * @since 31/7/25
 */
@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        try (InputStream configStream = getClass().getClassLoader().getResourceAsStream("hazelcast-client.yaml")) {

            ClientConfig clientConfig = new YamlClientConfigBuilder(configStream).build();

            return HazelcastClient.newHazelcastClient(clientConfig);
        } catch (Exception e) {

            System.out.println("Hazelcast not available, running without cache!");
            return null;
        }
    }
}


