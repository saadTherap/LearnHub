package net.therap.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Configuration
public class KafkaTopicConfig {

//    @Bean
//    public NewTopic infoUpdateTopic() {
//        return TopicBuilder.name("update-info-topic")
//                .partitions(10)
//                .replicas(3)
//                .build();
//    }
}
