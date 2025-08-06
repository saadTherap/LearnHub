package net.therap.secureFileServer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
/**
 * @author avidewan
 * @since 7/22/25
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String uploadDir;
    private long maxFileSize;
    private List<String> allowedFileTypes;
    private boolean enableVirusScan;
}