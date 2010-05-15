package org.valz.server;

import java.util.prefs.Preferences;

public class ServerConfig {

    public final String dataStoreFile;
    public final int port;
    public final int delayForCaching;

    public ServerConfig(String dataStoreFile, int port, int delayForCaching) {
        this.dataStoreFile = dataStoreFile;
        this.port = port;
        this.delayForCaching = delayForCaching;
    }

    public static ServerConfig read() {
        Preferences prefs = Preferences.userRoot().node("server.config");
        ServerConfig config = new ServerConfig(
                prefs.get("dataStoreFile", "h2store"),
                prefs.getInt("port", 8080),
                prefs.getInt("delayForCaching", 100)
        );
        return config;
    }
}

