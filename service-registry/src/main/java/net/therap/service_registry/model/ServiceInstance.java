package net.therap.service_registry.model;

import java.time.Instant;
import java.util.Objects;
/**
 * @author riadanonto
 * @since 23/7/25
 */
public class ServiceInstance {

    private final String host;
    private final int port;
    private Instant lastSeen;

    public ServiceInstance(String host, int port) {
        this.host = host;
        this.port = port;
        this.lastSeen = Instant.now();
    }

    public String getHost() { return host; }

    public int getPort() { return port; }

    public Instant getLastSeen() { return lastSeen; }

    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInstance)) return false;
        ServiceInstance that = (ServiceInstance) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}

