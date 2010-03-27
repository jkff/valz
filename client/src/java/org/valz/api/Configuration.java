package org.valz.api;

import org.jetbrains.annotations.NotNull;

public class Configuration {
    private String serverURL;

    public Configuration() {
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(@NotNull String serverURL) {
        this.serverURL = serverURL;
    }
}
