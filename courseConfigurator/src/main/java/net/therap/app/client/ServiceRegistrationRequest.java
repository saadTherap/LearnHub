package net.therap.app.client;

/**
 * @author riadanonto
 * @since 6/8/25
 */
public class ServiceRegistrationRequest {
    private String service;
    private String host;
    private int port;

    public ServiceRegistrationRequest() {}

    public ServiceRegistrationRequest(String service, String host, int port) {
        this.service = service;
        this.host = host;
        this.port = port;
    }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}

