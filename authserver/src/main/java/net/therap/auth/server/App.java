package net.therap.auth.server;

import jakarta.annotation.PostConstruct;
import net.therap.kafkaregistry.service.KafkaTopicRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"net.therap.auth.server", "net.therap.auth.lib", "net.therap.kafkaregistry" +
        ".service", "net.therap.cache.support"})
public class App {
    
    @Autowired
    private KafkaTopicRegistrar kafkaTopicRegistrar;
    
    @Value("${kafka.topics.student.registration}")
    private String studentRegistrationTopic;
    
    @Value("${kafka.topics.instructor.registration}")
    private String instructorRegistrationTopic;
    
    @Value("${kafka.topics.registration.partition}")
    private String registrationPartition;
    
    @Value("${kafka.topics.registration.replication}")
    private String registrationReplication;
    
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        kafkaTopicRegistrar.registerTopic(studentRegistrationTopic, Integer.parseInt(registrationPartition), Short.parseShort(registrationReplication));
        kafkaTopicRegistrar.registerTopic(instructorRegistrationTopic, Integer.parseInt(registrationPartition), Short.parseShort(registrationReplication));
    }
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
