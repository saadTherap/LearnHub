package net.therap.learningProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {
        CacheAutoConfiguration.class,
        HazelcastAutoConfiguration.class
//        HazelcastHealthContributorAutoConfiguration.class
})
@EnableFeignClients(basePackages = "net.therap.learningProcessor.client")
public class LearningProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }

}
