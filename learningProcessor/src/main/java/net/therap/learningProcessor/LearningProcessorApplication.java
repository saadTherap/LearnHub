package net.therap.learningProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        CacheAutoConfiguration.class,
        HazelcastAutoConfiguration.class
})
//@EnableFeignClients(basePackages = "net.therap.learningProcessor.client")
@EnableScheduling
public class LearningProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }

}
