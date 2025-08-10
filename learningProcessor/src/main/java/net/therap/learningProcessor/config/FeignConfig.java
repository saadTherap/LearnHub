package net.therap.learningProcessor.config;

import feign.Client;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author avidewan
 * @since 8/5/25
 */
@Configuration
public class FeignConfig {

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
}