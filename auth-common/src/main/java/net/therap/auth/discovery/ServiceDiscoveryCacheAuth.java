package net.therap.auth.discovery;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author riadanonto
 * @since 6/8/25
 */
@Component
public class ServiceDiscoveryCacheAuth {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${registry.url}")
    private String registryUrl;

    private final Map<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> rrIndex = new ConcurrentHashMap<>();

    @PostConstruct
    @Scheduled(fixedDelay = 30000)
    public void refresh() {
        List<String> services = List.of("auth");
        for (String service : services) {
            List<Map<String, Object>> instances = restTemplate.getForObject(
                    registryUrl + "/services/" + service, List.class
            );
            cache.put(service, instances != null ? instances : List.of());
            // Reset round robin index if needed
            rrIndex.computeIfAbsent(service, s -> new AtomicInteger(0));
        }
    }

    public Map<String, Object> getInstance(String service) {
        List<Map<String, Object>> instances = cache.getOrDefault(service, List.of());
        if (instances.isEmpty())
            throw new RuntimeException("No instance for: " + service);

        AtomicInteger index = rrIndex.computeIfAbsent(service, s -> new AtomicInteger(0));
        int pos = Math.abs(index.getAndIncrement()) % instances.size();
        return instances.get(pos);
    }
}


