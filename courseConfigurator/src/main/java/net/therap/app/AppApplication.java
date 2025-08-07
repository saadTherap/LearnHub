package net.therap.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.hazelcast.HazelcastHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
		CacheAutoConfiguration.class,
		HazelcastAutoConfiguration.class,
		HazelcastHealthContributorAutoConfiguration.class
})
@EnableJpaRepositories(basePackages = "net.therap.app.repository")
@EnableScheduling
@EntityScan("net.therap.app.model")
//@EnableFeignClients(basePackages = "net.therap.auth.client")
//@ComponentScan(basePackages = "net.therap.auth")
public class AppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
	
}