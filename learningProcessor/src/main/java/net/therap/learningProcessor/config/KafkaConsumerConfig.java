package net.therap.learningProcessor.config;

import net.therap.learningProcessor.deserializer.UpdateInfoDeserializer;
import net.therap.learningProcessor.entity.UpdateInfo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "update-info-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "update-info-grp");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UpdateInfoDeserializer.class);

        return props;
    }

    @Bean
    public ConsumerFactory<String, UpdateInfo> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, UpdateInfo>> kafkaListenerContainerFactory(
            ConsumerFactory<String, UpdateInfo> consumerFactory
    ) {

        ConcurrentKafkaListenerContainerFactory<String, UpdateInfo> containerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();

        containerFactory.setConsumerFactory(consumerFactory);

        return containerFactory;
    }
}
