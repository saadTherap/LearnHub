package net.therap.service_registry.controller;

import net.therap.service_registry.model.ServiceInstance;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author riadanonto
 * @since 23/7/25
 */
@RestController
public class ServiceRegistryController {

    private final Map<String, Set<ServiceInstance>> registry = new ConcurrentHashMap<>();
    private static final long STALE_THRESHOLD_SECONDS = 30;

    @PostMapping("/register")
    public String register(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        int port = (int) payload.get("port");

        ServiceInstance instance = new ServiceInstance(host, port);

        registry.computeIfAbsent(service, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registry.get(service).remove(instance);
        registry.get(service).add(instance);

        return "registered";
    }

    @PostMapping("/heartbeat")
    public String heartbeat(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        int port = (int) payload.get("port");

        Set<ServiceInstance> instances = registry.get(service);

        if (instances != null) {
            instances.stream()
                    .filter(i -> i.getHost().equals(host) && i.getPort() == port)
                    .forEach(i -> i.setLastSeen(Instant.now()));
        }

        return "heartbeat updated";
    }

    @PostMapping("/deregister")
    public String deregister(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        int port = (int) payload.get("port");

        Set<ServiceInstance> instances = registry.get(service);

        if (instances != null) {
            instances.remove(new ServiceInstance(host, port));
        }

        return "deregistered";
    }

    @GetMapping("/services/{service}")
    public List<Map<String, Object>> getServiceInstances(@PathVariable String service) {
        cleanUpStale(service);
        Set<ServiceInstance> instances = registry.getOrDefault(service, Collections.emptySet());
        List<Map<String, Object>> result = new ArrayList<>();

        for (ServiceInstance i : instances) {
            Map<String, Object> map = new HashMap<>();
            map.put("host", i.getHost());
            map.put("port", i.getPort());

            result.add(map);
        }

        return result;
    }

    private void cleanUpStale(String service) {
        Set<ServiceInstance> instances = registry.get(service);

        if (instances != null) {
            Instant now = Instant.now();
            instances.removeIf(i -> now.getEpochSecond() - i.getLastSeen().getEpochSecond() > STALE_THRESHOLD_SECONDS);
        }
    }
}

