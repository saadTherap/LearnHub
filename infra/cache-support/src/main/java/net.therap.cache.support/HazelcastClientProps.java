package net.therap.cache.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author riadanonto
 * @since 14/8/25
 */
@ConfigurationProperties(prefix = "cache.hazelcast")
public class HazelcastClientProps {

    /**
     * Enable/disable the auto-config.
     */
    private boolean enabled = true;

    /**
     * If true, failures to create a client will be ignored (no bean).
     * Use this when cache is optional for a service.
     */
    private boolean optional = true;

    /**
     * Classpath location of hazelcast client YAML.
     */
    private String clientYaml = "hazelcast-client.yaml";

    // getters & setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isOptional() { return optional; }
    public void setOptional(boolean optional) { this.optional = optional; }
    public String getClientYaml() { return clientYaml; }
    public void setClientYaml(String clientYaml) { this.clientYaml = clientYaml; }
}

