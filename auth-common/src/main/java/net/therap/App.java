package net.therap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author apurboturjo
 * @since 7/27/25
 */

@SpringBootApplication
@EnableFeignClients(basePackages = "net.therap")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
