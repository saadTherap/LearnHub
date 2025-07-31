package net.therap.app.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @author riadanonto
 * @since 31/7/25
 */
@Configuration
public class HazelcastConfig {

    @Bean
    @ConditionalOnMissingBean
    public HazelcastInstance hazelcastInstance() {
        try {

            return HazelcastClient.newHazelcastClient();
        } catch (Exception e) {

            System.out.println("Hazelcast not available, running without cache!");
            return null;
        }
    }
}

