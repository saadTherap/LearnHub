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
        ".service"})
public class App {
    
    @Autowired
    private KafkaTopicRegistrar kafkaTopicRegistrar;
    
    @Value("${kafka.topics.student.deletion}")
    private String studentDeletionTopic;
    
    @Value("${kafka.topics.instructor.deletion}")
    private String instructorDeletionTopic;
    
    @Value("${kafka.topics.student.registration}")
    private String studentRegistrationTopic;
    
    @Value("${kafka.topics.instructor.registration}")
    private String instructorRegistrationTopic;
    
    @Value("${kafka.topics.partition}")
    private String partition;
    
    @Value("${kafka.topics.replication}")
    private String replication;
    
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        kafkaTopicRegistrar.registerTopic(studentRegistrationTopic, Integer.parseInt(partition), Short.parseShort(replication));
        kafkaTopicRegistrar.registerTopic(instructorRegistrationTopic, Integer.parseInt(partition), Short.parseShort(replication));
        kafkaTopicRegistrar.registerTopic(studentDeletionTopic, Integer.parseInt(partition), Short.parseShort(replication));
        kafkaTopicRegistrar.registerTopic(instructorDeletionTopic, Integer.parseInt(partition), Short.parseShort(replication));
    }
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
