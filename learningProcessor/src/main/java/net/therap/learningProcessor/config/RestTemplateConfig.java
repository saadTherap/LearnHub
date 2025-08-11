package net.therap.learningProcessor.config;

import net.therap.auth.context.RequestTokenContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 8/11/25
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RequestTokenContext requestTokenContext) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            String token = requestTokenContext.getToken();
            System.out.println("Token for RestTemplate: " + token);

            if (Objects.nonNull(token)) {
                request.getHeaders().set("Authorization", "Bearer " + token);
            }

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}