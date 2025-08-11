package net.therap.learningProcessor.config;

import feign.Client;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import net.therap.learningProcessor.util.HmacUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Instant;

/**
 * @author avidewan
 * @since 8/5/25
 */
@Configuration
public class FeignConfig {

    @Value("${secure-file-server.client-key}")
    private String clientKey;

    @Value("${secure-file-server.secret}")
    private String secret;


    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

    @Bean
    public Client feignClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(new SSLContextBuilder()
                        .loadTrustMaterial(null, (chain, authType) -> true)
                        .build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        return new feign.httpclient.ApacheHttpClient(httpClient);
    }

    @Bean
    public RequestInterceptor hmacRequestInterceptor() {
        return requestTemplate -> {

            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String rawUrl = requestTemplate.url();
            String path;

            try {
                URI uri = new URI(rawUrl);
                path = uri.getPath();

                if (path == null || path.isEmpty()) {
                    int q = rawUrl.indexOf('?');
                    path = (q >= 0) ? rawUrl.substring(0, q) : rawUrl;
                }

            } catch (Exception e) {
                int q = rawUrl.indexOf('?');
                path = (q >= 0) ? rawUrl.substring(0, q) : rawUrl;
            }

            String message = requestTemplate.method() + path + timestamp;

            String signature = HmacUtils.hmacSHA256(secret, message);

            requestTemplate.header("X-Api-Key", clientKey);
            requestTemplate.header("X-Timestamp", timestamp);
            requestTemplate.header("X-Signature", signature);
        };
    }
}