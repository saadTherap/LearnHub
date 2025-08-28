package net.therap.secureFileServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = {"net.therap.auth.lib.client"})
@EnableScheduling
@ComponentScan(basePackages = {
		"net.therap.secureFileServer",
		"net.therap.auth.lib"
}
)
public class SecureFileServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileServerApplication.class, args);
	}
}