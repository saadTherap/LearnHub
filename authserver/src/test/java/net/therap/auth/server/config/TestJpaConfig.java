package net.therap.auth.server.config;

import net.therap.kafkaregistry.service.KafkaTopicRegistrar;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author apurboturjo
 * @since 9/2/25
 */
@TestConfiguration
@EnableJpaRepositories(basePackages = "net.therap.auth.server.respository")
@ComponentScan(basePackages = "net.therap.auth.server.entity")
public class TestJpaConfig {
    
    @Bean
    public KafkaTopicRegistrar kafkaTopicRegistrar() {
        return Mockito.mock(KafkaTopicRegistrar.class);
    }
}