//package net.therap.learningProcessor.config;
//
//import feign.Client;
//import feign.RequestInterceptor;
//import feign.codec.Encoder;
//import feign.codec.ErrorDecoder;
//import feign.form.spring.SpringFormEncoder;
//import lombok.extern.slf4j.Slf4j;
//import net.therap.learningProcessor.util.HmacUtils;
//import org.apache.http.conn.ssl.NoopHostnameVerifier;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Instant;
//
///**
// * @author avidewan
// * @since 8/5/25
// */
//@Configuration
//@Slf4j
//public class FeignConfig {
//
//    @Value("${secure-file-server.client-key}")
//    private String clientKey;
//
//    @Value("${secure-file-server.secret}")
//    private String secret;
//
//
//    @Bean
//    public Encoder feignFormEncoder() {
//        return new SpringFormEncoder();
//    }
//
//    @Bean
//    public Client feignClient() throws Exception {
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLContext(new SSLContextBuilder()
//                        .loadTrustMaterial(null, (chain, authType) -> true)
//                        .build())
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .build();
//
//        return new feign.httpclient.ApacheHttpClient(httpClient);
//    }
//
//    @Bean
//    public RequestInterceptor hmacRequestInterceptor() {
//        return requestTemplate -> {
//            String timestamp = String.valueOf(Instant.now().getEpochSecond());
//            String rawUrl = requestTemplate.url();
//
//            log.info("Feign gives us: '{}'", rawUrl);
//
//            String serverPath = "/api/secure-file-server/files";
//
//            if (!"/".equals(rawUrl)) {
//                serverPath = "/api/secure-file-server/files" + rawUrl;
//            }
//
//            String message = requestTemplate.method() + serverPath + timestamp;
//            String signature = HmacUtils.hmacSHA256(secret, message);
//
//            log.info("=== Outgoing HMAC Auth Request Message ===");
//            log.info("message: {}", message);
//
//            requestTemplate.header("X-Api-Key", clientKey);
//            requestTemplate.header("X-Timestamp", timestamp);
//            requestTemplate.header("X-Signature", signature);
//
//
//            log.info("=== Outgoing HMAC Auth Request ===");
//            log.info("API Key header: {}", clientKey);
//            log.info("Signature header: {}", signature);
//            log.info("Timestamp header: {}", timestamp);
//        };
//    }
//
//    @Bean
//    public ErrorDecoder feignErrorDecoder() {
//        return new FeignCustomErrorDecoder();
//    }
//}