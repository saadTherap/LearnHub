package net.therap.cache.support;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;

/**
 * @author riadanonto
 * @since 14/8/25
 */
@AutoConfiguration
@ConditionalOnClass(HazelcastClient.class)
@EnableConfigurationProperties(HazelcastClientProps.class)
public class HazelcastCacheAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "cache.hazelcast", name = "enabled", havingValue = "true", matchIfMissing = true)
    public HazelcastInstance hazelcastInstance(HazelcastClientProps props) {
        try (InputStream configStream = HazelcastCacheAutoConfiguration.class
                .getClassLoader()
                .getResourceAsStream(props.getClientYaml())) {

            if (configStream == null) {
                throw new IllegalStateException("Missing hazelcast client yaml: " + props.getClientYaml());
            }

            ClientConfig clientConfig = new YamlClientConfigBuilder(configStream).build();

            return HazelcastClient.newHazelcastClient(clientConfig);
        } catch (Exception e) {

            if (props.isOptional()) {
                return null;
            }
            throw new IllegalStateException("Failed to initialize Hazelcast client", e);
        }
    }

    @Bean
    @ConditionalOnBean(HazelcastInstance.class)
    @ConditionalOnMissingBean
    public HazelcastCacheService hazelcastCacheService(HazelcastInstance instance) {
        return new HazelcastCacheService(instance);
    }

    @Bean
    @ConditionalOnBean(HazelcastCacheService.class)
    @ConditionalOnMissingBean
    public CacheInvalidationUtil cacheInvalidationUtil(HazelcastCacheService cache) {
        return new CacheInvalidationUtil(cache);
    }
}
