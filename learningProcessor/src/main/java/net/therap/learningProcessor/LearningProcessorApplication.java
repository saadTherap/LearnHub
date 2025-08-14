package net.therap.learningProcessor;

import jakarta.annotation.PostConstruct;
import net.therap.kafkaregistry.service.KafkaTopicRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
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
@EnableFeignClients(basePackages = {"net.therap.learningProcessor.client", "net.therap.auth.client"})
@EnableScheduling
@ComponentScan(basePackages = {
        "net.therap.auth",
        "net.therap.learningProcessor",
        "net.therap.kafkaregistry.service"
}
)
public class LearningProcessorApplication {

    @Autowired
    private KafkaTopicRegistrar kafkaTopicRegistrar;

    @Value("${kafka.topics.notification}")
    private String notificationTopic;

    @Value("${kafka.topics.notification.partition")
    private String notificationPartition;

    @Value("${kafka.topics.notification.replication")
    private String notificationReplication;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        kafkaTopicRegistrar.registerTopic(notificationTopic, Integer.parseInt(notificationPartition), Short.parseShort(notificationReplication));
    }

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }
}