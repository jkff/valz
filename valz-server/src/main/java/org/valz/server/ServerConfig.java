package org.valz.server;

import java.util.prefs.Preferences;

public class ServerConfig {

    public static final String defaultDataStoreFile = "h2server-storage.db";
    public static final int defaultPort = 9125;
    public static final int defaultDelayForCaching = 100;

    public final String dataStoreFile;
    public final int port;
    public final int delayForCaching;

    public ServerConfig(int port, String dataStoreFile, int delayForCaching) {
        this.dataStoreFile = dataStoreFile;
        this.port = port;
        this.delayForCaching = delayForCaching;
    }

    public ServerConfig(int port) {
        this(port, defaultDataStoreFile, defaultDelayForCaching);
    }

    public ServerConfig() {
        this(defaultPort, defaultDataStoreFile, defaultDelayForCaching);
    }

    public static ServerConfig read() {
        Preferences prefs = Preferences.userRoot().node("server.config");
        return new ServerConfig(
                prefs.getInt("port", defaultPort),
                prefs.get("dataStoreFile", defaultDataStoreFile),
                prefs.getInt("delayForCaching", defaultDelayForCaching)
        );
    }
}

