package org.valz.api;

import java.util.List;

public class Configuration {
    private List<String> servers;

    public Configuration() {
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
