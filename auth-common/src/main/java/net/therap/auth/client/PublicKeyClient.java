package net.therap.auth.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private ServiceDiscoveryCache serviceDiscoveryCache;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getServiceBaseUrl() {
        Map<String, Object> instance = serviceDiscoveryCache.getInstance(SERVICE_NAME);
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
