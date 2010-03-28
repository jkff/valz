package org.valz.util.protocol;

import java.util.List;

/**
 * Created on: 28.03.2010 11:35:53
 */
public class ReadConfiguration {
    private List<String> serverUrls;

    public ReadConfiguration() {
    }

    public List<String> getServerUrls() {
        return serverUrls;
    }

    public void setServerUrls(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }
}
