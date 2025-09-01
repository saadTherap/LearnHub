package net.therap.auth.server.config;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author apurboturjo
 * @since 9/1/25
 */
@TestConfiguration
public class ServiceTestConfig {

//    @Bean
//    public HazelcastInstance hazelcastInstance() {
//        return Mockito.mock(HazelcastInstance.class);
//    }
    
    @Bean
    public ProducerConsumerTask producerConsumerTask() {
        return Mockito.mock(ProducerConsumerTask.class);
    }

//    @Bean
//    public KafkaTemplate<?, ?> kafkaTemplate() {
//        return Mockito.mock(KafkaTemplate.class);
//    }
//
//    @Bean
//    public ProducerFactory<?, ?> producerFactory() {
//        return Mockito.mock(ProducerFactory.class);
//    }
//
//    @Bean
//    public ConsumerFactory<?, ?> consumerFactory() {
//        return Mockito.mock(ConsumerFactory.class);
//    }
//
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        return Mockito.mock(KafkaAdmin.class);
//    }
}