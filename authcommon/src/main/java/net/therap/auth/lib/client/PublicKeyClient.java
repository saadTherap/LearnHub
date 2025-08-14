package net.therap.auth.lib.client;

import net.therap.auth.lib.discovery.ServiceDiscoveryCacheAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Component
public class PublicKeyClient {

    private static final String SERVICE_NAME = "auth";
    private static final String PUBLIC_KEY_PATH = "/auth/pk";

    @Autowired
    private ServiceDiscoveryCacheAuth serviceDiscoveryCacheAuth;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getServiceBaseUrl() {
        Map<String, Object> instance = serviceDiscoveryCacheAuth.getInstance(SERVICE_NAME);
        String host = (String) instance.get("host");
        Integer port = (Integer) instance.get("port");
        return "http://" + host + ":" + port;
    }

    public ResponseEntity<String> getPublicKey(String keyId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(getServiceBaseUrl())
                .path(PUBLIC_KEY_PATH)
                .queryParam("kid", keyId)
                .toUriString();

        return restTemplate.getForEntity(url, String.class);
    }
}