//package net.therap.learningProcessor.client;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//
///**
// * @author riadanonto
// * @since 6/8/25
// */
//@Component
//public class ServiceRegistryClient {
//    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryClient.class);
//
//    @Value("${registry.url}")
//    private String registryUrl;
//
//    @Value("${service.name}")
//    private String serviceName;
//
//    @Value("${server.port}")
//    private int servicePort;
//
//    @Value("${service.host}")
//    private String serviceHost;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    private ServiceRegistrationRequest getPayload() {
//        return new ServiceRegistrationRequest(serviceName, serviceHost, servicePort);
//    }
//
//    @PostConstruct
//    public void register() {
//        String url = registryUrl + "/register";
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, getPayload(), String.class);
//            logger.info("Registered with registry: {}", response.getBody());
//        } catch (Exception e) {
//            logger.error("Failed to register with registry!", e);
//        }
//    }
//
//    @Scheduled(fixedRate = 10000)
//    public void sendHeartbeat() {
//        String url = registryUrl + "/heartbeat";
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, getPayload(), String.class);
//            logger.info("Sent heartbeat: {}", response.getBody());
//        } catch (Exception e) {
//            logger.warn("Failed to send heartbeat", e);
//        }
//    }
//
//    @PreDestroy
//    public void deregister() {
//        String url = registryUrl + "/deregister";
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, getPayload(), String.class);
//            logger.info("Deregistered from registry: {}", response.getBody());
//        } catch (Exception e) {
//            logger.warn("Failed to deregister from registry", e);
//        }
//    }
//}
