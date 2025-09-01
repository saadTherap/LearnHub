package net.therap.learningProcessor;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.therap.kafkaregistry.service.KafkaTopicRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


/**
 * @author avidewan
 * @since 8/7/25
 */
@SpringBootApplication(exclude = {
        CacheAutoConfiguration.class,
        HazelcastAutoConfiguration.class
})
@EnableJpaRepositories(basePackages = "net.therap.learningProcessor.repository")
@EnableFeignClients(basePackages = {"net.therap.auth.lib.client"})
@EnableScheduling
@ComponentScan(basePackages = {
        "net.therap.learningProcessor",
        "net.therap.kafkaregistry.service",
        "net.therap.auth.lib"
}
)
@Slf4j
public class LearningProcessorApplication {

    @Autowired
    private KafkaTopicRegistrar kafkaTopicRegistrar;

    @Value("${kafka.topics.notification}")
    private String notificationTopic;

    @Value("${kafka.topics.notification.partition}")
    private String notificationPartition;

    @Value("${kafka.topics.notification.replication}")
    private String notificationReplication;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        try {
            kafkaTopicRegistrar.registerTopic(notificationTopic, Integer.parseInt(notificationPartition), Short.parseShort(notificationReplication));

        } catch (Exception e) {
            log.debug("[Kafka Error] : {}", String.valueOf(e));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }
}