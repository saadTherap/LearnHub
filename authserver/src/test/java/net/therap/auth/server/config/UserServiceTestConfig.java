package net.therap.auth.server.config;

import com.hazelcast.core.HazelcastInstance;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author apurboturjo
 * @since 9/1/25
 */
@TestConfiguration
public class UserServiceTestConfig {
    
    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Mockito.mock(HazelcastInstance.class);
    }
    
    @Bean
    public ProducerConsumerTask producerConsumerTask() {
        return Mockito.mock(ProducerConsumerTask.class);
    }
    
    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
    
    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        return Mockito.mock(ProducerFactory.class);
    }
    
    @Bean
    public ConsumerFactory<?, ?> consumerFactory() {
        return Mockito.mock(ConsumerFactory.class);
    }
    
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return Mockito.mock(KafkaAdmin.class);
    }
}