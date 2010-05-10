package org.valz.server;

import nl.chess.it.util.config.Config;

import java.util.Properties;

public class ServerConfig extends Config {
    /**
     * Name of the file we are looking for to read the configuration.
     */
    public static final String FILE_NAME = "serverconfig.properties";

    public ServerConfig(Properties properties) {
        super(properties);
    }

    public String getDataStoreFile() {
        return getString("property.dataStoreFile");
    }

    public int getPort() {
        return getInt("property.port");
    }

    public int getDelayForCaching() {
        return getInt("property.delayForCaching");
    }}
