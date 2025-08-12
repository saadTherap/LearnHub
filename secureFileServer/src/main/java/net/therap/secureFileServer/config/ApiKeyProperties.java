package net.therap.secureFileServer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author avidewan
 * @since 8/11/25
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api.keys")
public class ApiKeyProperties {

    private Map<String, String> clients;

    public void setClients(Map<String, String> clients) {
        System.out.println("Setting clients: " + clients);

        this.clients = clients;
    }
}