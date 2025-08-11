package net.therap.app.client;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author riadanonto
 * @since 6/8/25
 */
@Component
public class ServiceDiscoveryCache {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryCache.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${registry.url}")
    private String registryUrl;

    private final Map<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> rrIndex = new ConcurrentHashMap<>();

    @PostConstruct
    @Scheduled(fixedDelay = 30000)
    public void refresh() {
        try {
            List<String> services = List.of("learning-processor");
            for (String service : services) {
                List<Map<String, Object>> instances = restTemplate.getForObject(
                        registryUrl + "/services/" + service, List.class
                );
                cache.put(service, instances != null ? instances : List.of());
                // Reset round robin index if needed
                rrIndex.computeIfAbsent(service, s -> new AtomicInteger(0));
            }
            logger.info("Service discovery cache refreshed successfully.");
        } catch (ResourceAccessException e) {
            logger.error("Failed to refresh service discovery cache. Registry is unreachable. Will retry on next schedule.", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while refreshing service discovery cache.", e);
        }
    }

    public Map<String, Object> getInstance(String service) {
        List<Map<String, Object>> instances = cache.getOrDefault(service, List.of());
        if (instances.isEmpty()) {
            logger.warn("No instance available for service: {}. This might be due to a registry failure.", service);
            throw new RuntimeException("No instance for: " + service);
        }

        AtomicInteger index = rrIndex.computeIfAbsent(service, s -> new AtomicInteger(0));
        int pos = Math.abs(index.getAndIncrement()) % instances.size();
        return instances.get(pos);
    }
}


