package net.therap.learningProcessor;

import jakarta.annotation.PostConstruct;
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
@ComponentScan(basePackages = {"net.therap.auth", "net.therap.learningProcessor"})
public class LearningProcessorApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }
}