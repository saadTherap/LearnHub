package net.therap.learningProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "net.therap.learningProcessor.client")
public class LearningProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningProcessorApplication.class, args);
    }

}
