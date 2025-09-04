package net.therap.auth.server.config;

import com.hazelcast.core.HazelcastInstance;
import net.therap.kafkaregistry.service.KafkaTopicRegistrar;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author apurboturjo
 * @since 9/1/25
 */
@TestConfiguration
public class TestServiceConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Mockito.mock(HazelcastInstance.class);
    }
    
    @Bean
    public ProducerConsumerTask producerConsumerTask() {
        return Mockito.mock(ProducerConsumerTask.class);
    }
    
    @Bean
    public KafkaTopicRegistrar kafkaTopicRegistrar() {
        return Mockito.mock(KafkaTopicRegistrar.class);
    }
}