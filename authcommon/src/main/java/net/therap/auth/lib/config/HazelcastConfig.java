package net.therap.auth.lib.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author apurboturjo
 * @since 8/28/25
 */
@Configuration
@ConditionalOnProperty(name = "cache.hazelcast.enabled", havingValue = "true", matchIfMissing = true)
public class HazelcastConfig {
    
    @Bean
    public HazelcastInstance hazelcastInstance() throws IOException {
        ClientConfig clientConfig =
                new YamlClientConfigBuilder("hazelcast-client.yaml").build();
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
