package net.therap.service_registry.controller;

import net.therap.service_registry.model.ServiceInstance;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
    public ResponseEntity<String> register(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        Object portObj = payload.get("port");

        if (service == null || host == null || portObj == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        int port;
        try {
            port = portObj instanceof Integer ? (Integer) portObj
                    : Integer.parseInt(portObj.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid port format");
        }

        ServiceInstance instance = new ServiceInstance(host, port);

        registry.computeIfAbsent(service, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registry.get(service).remove(instance);
        registry.get(service).add(instance);

        return ResponseEntity.status(HttpStatus.CREATED).body("registered");
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        Object portObj = payload.get("port");

        if (service == null || host == null || portObj == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        int port;
        try {
            port = portObj instanceof Integer ? (Integer) portObj
                    : Integer.parseInt(portObj.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid port format");
        }

        Set<ServiceInstance> instances = registry.get(service);
        boolean updated = false;

        if (instances != null) {
            for (ServiceInstance i : instances) {
                if (i.getHost().equals(host) && i.getPort() == port) {
                    i.setLastSeen(Instant.now());
                    updated = true;
                }
            }
        }

        if (updated) {
            return ResponseEntity.ok("heartbeat updated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("service instance not found");
        }
    }

    @PostMapping("/deregister")
    public ResponseEntity<String> deregister(@RequestBody Map<String, Object> payload) {
        String service = (String) payload.get("service");
        String host = (String) payload.get("host");
        Object portObj = payload.get("port");

        if (service == null || host == null || portObj == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        int port;
        try {
            port = portObj instanceof Integer ? (Integer) portObj
                    : Integer.parseInt(portObj.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid port format");
        }

        Set<ServiceInstance> instances = registry.get(service);
        boolean removed = false;

        if (instances != null) {
            removed = instances.remove(new ServiceInstance(host, port));
        }

        if (removed) {
            return ResponseEntity.ok("deregistered");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("service instance not found");
        }
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

